package guru.qa.rangiffler.test.grpc.photo;

import guru.qa.grpc.rangiffler.grpc.CreatePhotoRequest;
import guru.qa.grpc.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.db.entity.photo.PhotoEntity;
import guru.qa.rangiffler.db.repository.PhotoRepository;
import guru.qa.rangiffler.db.repository.hibernate.PhotoRepositoryHibernate;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.grpc.BaseGrpcTest;
import guru.qa.rangiffler.util.DataUtil;
import guru.qa.rangiffler.util.ImageUtil;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;
import static io.qameta.allure.Allure.step;

@Feature("PHOTO")
@Story("CreatePhoto")
@DisplayName("CreatePhoto")
public class CreatePhotoTest extends BaseGrpcTest {
    private final PhotoRepository photoRepository = new PhotoRepositoryHibernate();

    @Test
    @GenerateUser
    @DisplayName("Ответ на создание фотографии должен содержать информацию о созданной фотографии")
    void createPhotoResponseShouldHavePhotoInfoTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel photo = PhotoModel.create("img/photo/1.jpg");

        PhotoResponse response = photoGrpcClient.createPhoto(
                CreatePhotoRequest.newBuilder()
                        .setUserId(user.id().toString())
                        .setSrc(photo.getPhotoAsBase64())
                        .setCountryCode(photo.country().getCode())
                        .setDescription(photo.description())
                        .build());

        step("id пользователя должен быть тот, что отправили",
                () -> Assertions.assertEquals(user.id(), UUID.fromString(response.getUserId())));
        step("id фотографии не должно быть пустым",
                () -> Assertions.assertNotEquals("", response.getPhotoId()));
        step("Фотография должна быть той же, что и отправили",
                () -> Assertions.assertEquals(photo.getPhotoAsBase64(), response.getSrc()));
        step("Код страны фотографии должен быть тем же, что и отправили",
                () -> Assertions.assertEquals(photo.country().getCode(), response.getCountryCode()));
        step("Описание фотографии должно быть тем же, что и отправили",
                () -> Assertions.assertEquals(photo.description(), response.getDescription()));
        step("Лайков должно быть 0",
                () -> Assertions.assertEquals(0, response.getLikes().getLikesCount()));

        step("Фотография должна быть в БД", () -> {
            List<PhotoEntity> allUsersPhotos = photoRepository.findByUserId(user.id());
            Assertions.assertEquals(1, allUsersPhotos.size());
            PhotoEntity photoEntity = allUsersPhotos.get(0);

            step("id пользователя должен быть тот, что отправили",
                    () -> Assertions.assertEquals(user.id(), photoEntity.getUserId()));
            step("id фотографии должно быть тем же, что и в ответе на создание",
                    () -> Assertions.assertEquals(UUID.fromString(response.getPhotoId()), photoEntity.getId()));
            step("Фотография должна быть той же, что и отправили",
                    () -> Assertions.assertEquals(photo.getPhotoAsBase64(), new String(photoEntity.getPhoto(), StandardCharsets.UTF_8)));
            step("Код страны фотографии должен быть тем же, что и отправили",
                    () -> Assertions.assertEquals(photo.country().getCode(), photoEntity.getCountryCode()));
            step("Описание фотографии должно быть тем же, что и отправили",
                    () -> Assertions.assertEquals(photo.description(), photoEntity.getDescription()));
            step("Лайков должно быть 0",
                    () -> Assertions.assertEquals(0, photoEntity.getLikes().size()));
        });
    }

    @Test
    @GenerateUser
    @DisplayName("Создание фотографии с некорректной картинкой должно возвращать INVALID_ARGUMENT")
    void shouldNotCreatePhotoWithBadImageTest(@User(FOR_GENERATE_USER) UserModel user) {
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.createPhoto(
                        CreatePhotoRequest.newBuilder()
                                .setUserId(user.id().toString())
                                .setSrc("https://evil/saveIp")
                                .setCountryCode(DataUtil.generateRandomCountry().getCode())
                                .setDescription("")
                                .build())
        );
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad image").asRuntimeException().getMessage(),
                e.getMessage());
        step("Фотография не должна быть в БД", () -> {
            List<PhotoEntity> allUsersPhotos = photoRepository.findByUserId(user.id());
            Assertions.assertEquals(0, allUsersPhotos.size());
        });
    }

    @Test
    @GenerateUser
    @DisplayName("Создание фотографии со слишком длинным описанием должно возвращать INVALID_ARGUMENT")
    void shouldNotCreatePhotoWithTooLongDescriptionTest(@User(FOR_GENERATE_USER) UserModel user) {
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.createPhoto(
                        CreatePhotoRequest.newBuilder()
                                .setUserId(user.id().toString())
                                .setSrc(ImageUtil.getImageAsBase64("img/photo/1.jpg"))
                                .setCountryCode(DataUtil.generateRandomCountry().getCode())
                                .setDescription(DataUtil.generateStringWithLength(256))
                                .build())
        );
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Too long description").asRuntimeException().getMessage(),
                e.getMessage());
        step("Фотография не должна быть в БД", () -> {
            List<PhotoEntity> allUsersPhotos = photoRepository.findByUserId(user.id());
            Assertions.assertEquals(0, allUsersPhotos.size());
        });
    }

    @Test
    @GenerateUser
    @DisplayName("Создание фотографии со слишком длинным кодом страны должно возвращать INVALID_ARGUMENT")
    void shouldNotCreatePhotoWithTooCountryCodeTest(@User(FOR_GENERATE_USER) UserModel user) {
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.createPhoto(
                        CreatePhotoRequest.newBuilder()
                                .setUserId(user.id().toString())
                                .setSrc(ImageUtil.getImageAsBase64("img/photo/1.jpg"))
                                .setCountryCode(DataUtil.generateStringWithLength(51))
                                .setDescription("")
                                .build())
        );
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Too long country code").asRuntimeException().getMessage(),
                e.getMessage());
        step("Фотография не должна быть в БД", () -> {
            List<PhotoEntity> allUsersPhotos = photoRepository.findByUserId(user.id());
            Assertions.assertEquals(0, allUsersPhotos.size());
        });
    }

    @Test
    @GenerateUser
    @DisplayName("Создание фотографии с некорректным id пользователя должно возвращать INVALID_ARGUMENT")
    void shouldNotCreatePhotoWithIncorrectUserIdTest() {
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.createPhoto(
                        CreatePhotoRequest.newBuilder()
                                .setUserId("")
                                .setSrc(ImageUtil.getImageAsBase64("img/photo/1.jpg"))
                                .setCountryCode(DataUtil.generateRandomCountry().getCode())
                                .setDescription("")
                                .build())
        );
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad user id").asRuntimeException().getMessage(),
                e.getMessage());
    }
}
