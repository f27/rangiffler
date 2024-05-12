package guru.qa.rangiffler.test.grpc.photo;

import guru.qa.grpc.rangiffler.grpc.PhotoResponse;
import guru.qa.grpc.rangiffler.grpc.UpdatePhotoRequest;
import guru.qa.rangiffler.db.entity.photo.PhotoEntity;
import guru.qa.rangiffler.db.repository.PhotoRepository;
import guru.qa.rangiffler.db.repository.hibernate.PhotoRepositoryHibernate;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.grpc.BaseGrpcTest;
import guru.qa.rangiffler.util.DataUtil;
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
@Story("UpdatePhoto")
@DisplayName("UpdatePhoto")
public class UpdatePhotoTest extends BaseGrpcTest {
    private final PhotoRepository photoRepository = new PhotoRepositoryHibernate();

    @Test
    @GenerateUser(photos = @Photo(country = CountryEnum.KAZAKHSTAN, description = "initial description"))
    @DisplayName("Ответ на обновление фотографии должен содержать информацию об обновленной фотографии")
    void updatePhotoResponseShouldHavePhotoInfoTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel photo = user.photos().get(0);
        String newDescription = DataUtil.generateStringWithLength(50);
        String newCountryCode = DataUtil.generateRandomCountry().getCode();
        PhotoResponse response = photoGrpcBlockingStub.updatePhoto(
                UpdatePhotoRequest.newBuilder()
                        .setUserId(user.id().toString())
                        .setPhotoId(photo.id().toString())
                        .setCountryCode(newCountryCode)
                        .setDescription(newDescription)
                        .build());

        step("id пользователя должен быть тот, что отправили",
                () -> Assertions.assertEquals(user.id(), UUID.fromString(response.getUserId())));
        step("id фотографии должно быть тем же, что и отправили",
                () -> Assertions.assertEquals(photo.id().toString(), response.getPhotoId()));
        step("Фотография должна быть той же, что и была",
                () -> Assertions.assertEquals(photo.getPhotoAsBase64(), response.getSrc()));
        step("Код страны фотографии должен быть тем же, что и отправили",
                () -> Assertions.assertEquals(newCountryCode, response.getCountryCode()));
        step("Описание фотографии должно быть тем же, что и отправили",
                () -> Assertions.assertEquals(newDescription, response.getDescription()));
        step("Лайков должно быть 0",
                () -> Assertions.assertEquals(0, response.getLikes().getLikesCount()));
        step("Фотография должна быть обновлена в БД", () -> {
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
                    () -> Assertions.assertEquals(newCountryCode, photoEntity.getCountryCode()));
            step("Описание фотографии должно быть тем же, что и отправили",
                    () -> Assertions.assertEquals(newDescription, photoEntity.getDescription()));
            step("Лайков должно быть 0",
                    () -> Assertions.assertEquals(0, photoEntity.getLikes().size()));
        });
    }

    @Test
    @GenerateUser(photos = @Photo(country = CountryEnum.KAZAKHSTAN, description = "initial description"))
    @DisplayName("Обновление фотографии с некорректным photo id должно возвращать INVALID_ARGUMENT")
    void shouldNotUpdatePhotoWithIncorrectPhotoIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel photo = user.photos().get(0);
        String newDescription = DataUtil.generateStringWithLength(50);
        String newCountryCode = DataUtil.generateRandomCountry().getCode();

        StatusRuntimeException e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcBlockingStub.updatePhoto(
                        UpdatePhotoRequest.newBuilder()
                                .setUserId(user.id().toString())
                                .setPhotoId("")
                                .setCountryCode(newCountryCode)
                                .setDescription(newDescription)
                                .build())
        );
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad UUID").asRuntimeException().getMessage(),
                e.getMessage());
        step("Фотография не должна быть обновлена в БД", () -> {
            List<PhotoEntity> allUsersPhotos = photoRepository.findByUserId(user.id());
            Assertions.assertEquals(1, allUsersPhotos.size());
            PhotoEntity photoEntity = allUsersPhotos.get(0);

            step("id пользователя должен быть тот же",
                    () -> Assertions.assertEquals(user.id(), photoEntity.getUserId()));
            step("id фотографии должно быть тем же",
                    () -> Assertions.assertEquals(photo.id(), photoEntity.getId()));
            step("Фотография должна быть той же",
                    () -> Assertions.assertEquals(photo.getPhotoAsBase64(), new String(photoEntity.getPhoto(), StandardCharsets.UTF_8)));
            step("Код страны фотографии должен быть тем же",
                    () -> Assertions.assertEquals(photo.country().getCode(), photoEntity.getCountryCode()));
            step("Описание фотографии должно быть тем же",
                    () -> Assertions.assertEquals(photo.description(), photoEntity.getDescription()));
            step("Лайков должно быть 0",
                    () -> Assertions.assertEquals(0, photoEntity.getLikes().size()));
        });
    }

    @Test
    @GenerateUser(photos = @Photo(country = CountryEnum.KAZAKHSTAN, description = "initial description"))
    @DisplayName("Обновление фотографии с некорректным user id должно возвращать INVALID_ARGUMENT")
    void shouldNotUpdatePhotoWithIncorrectUserIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel photo = user.photos().get(0);
        String newDescription = DataUtil.generateStringWithLength(50);
        String newCountryCode = DataUtil.generateRandomCountry().getCode();

        StatusRuntimeException e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcBlockingStub.updatePhoto(
                        UpdatePhotoRequest.newBuilder()
                                .setUserId("")
                                .setPhotoId(photo.id().toString())
                                .setCountryCode(newCountryCode)
                                .setDescription(newDescription)
                                .build())
        );
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad UUID").asRuntimeException().getMessage(),
                e.getMessage());
        step("Фотография не должна быть обновлена в БД", () -> {
            List<PhotoEntity> allUsersPhotos = photoRepository.findByUserId(user.id());
            Assertions.assertEquals(1, allUsersPhotos.size());
            PhotoEntity photoEntity = allUsersPhotos.get(0);

            step("id пользователя должен быть тот же",
                    () -> Assertions.assertEquals(user.id(), photoEntity.getUserId()));
            step("id фотографии должно быть тем же",
                    () -> Assertions.assertEquals(photo.id(), photoEntity.getId()));
            step("Фотография должна быть той же",
                    () -> Assertions.assertEquals(photo.getPhotoAsBase64(), new String(photoEntity.getPhoto(), StandardCharsets.UTF_8)));
            step("Код страны фотографии должен быть тем же",
                    () -> Assertions.assertEquals(photo.country().getCode(), photoEntity.getCountryCode()));
            step("Описание фотографии должно быть тем же",
                    () -> Assertions.assertEquals(photo.description(), photoEntity.getDescription()));
            step("Лайков должно быть 0",
                    () -> Assertions.assertEquals(0, photoEntity.getLikes().size()));
        });
    }

    @Test
    @GenerateUser(photos = @Photo(country = CountryEnum.KAZAKHSTAN, description = "initial description"))
    @GenerateUser(photos = @Photo(country = CountryEnum.BRAZIL, description = "initial description"))
    @DisplayName("Обновление фотографии с чужим photo id должно возвращать NOT_FOUND")
    void shouldNotUpdatePhotoWithAnotherUsersPhotoIdTest(@User(FOR_GENERATE_USER) UserModel[] users) {
        UserModel firstUser = users[0];
        PhotoModel firstUserPhoto = firstUser.photos().get(0);
        UserModel secondUser = users[1];
        PhotoModel secondUserPhoto = secondUser.photos().get(0);

        String newDescription = DataUtil.generateStringWithLength(50);
        String newCountryCode = DataUtil.generateRandomCountry().getCode();

        StatusRuntimeException e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcBlockingStub.updatePhoto(
                        UpdatePhotoRequest.newBuilder()
                                .setUserId(firstUser.id().toString())
                                .setPhotoId(secondUserPhoto.id().toString())
                                .setCountryCode(newCountryCode)
                                .setDescription(newDescription)
                                .build())
        );
        Assertions.assertEquals(
                Status.NOT_FOUND.withDescription("Photo not found").asRuntimeException().getMessage(),
                e.getMessage());
        step("Фотография первого пользователя не должна быть обновлена в БД", () -> {
            List<PhotoEntity> allUsersPhotos = photoRepository.findByUserId(firstUser.id());
            Assertions.assertEquals(1, allUsersPhotos.size());
            PhotoEntity photoEntity = allUsersPhotos.get(0);

            step("id пользователя должен быть тот же",
                    () -> Assertions.assertEquals(firstUser.id(), photoEntity.getUserId()));
            step("id фотографии должно быть тем же",
                    () -> Assertions.assertEquals(firstUserPhoto.id(), photoEntity.getId()));
            step("Фотография должна быть той же",
                    () -> Assertions.assertEquals(firstUserPhoto.getPhotoAsBase64(), new String(photoEntity.getPhoto(), StandardCharsets.UTF_8)));
            step("Код страны фотографии должен быть тем же",
                    () -> Assertions.assertEquals(firstUserPhoto.country().getCode(), photoEntity.getCountryCode()));
            step("Описание фотографии должно быть тем же",
                    () -> Assertions.assertEquals(firstUserPhoto.description(), photoEntity.getDescription()));
            step("Лайков должно быть 0",
                    () -> Assertions.assertEquals(0, photoEntity.getLikes().size()));
        });

        step("Фотография второго пользователя не должна быть обновлена в БД", () -> {
            List<PhotoEntity> allUsersPhotos = photoRepository.findByUserId(secondUser.id());
            Assertions.assertEquals(1, allUsersPhotos.size());
            PhotoEntity photoEntity = allUsersPhotos.get(0);

            step("id пользователя должен быть тот же",
                    () -> Assertions.assertEquals(secondUser.id(), photoEntity.getUserId()));
            step("id фотографии должно быть тем же",
                    () -> Assertions.assertEquals(secondUserPhoto.id(), photoEntity.getId()));
            step("Фотография должна быть той же",
                    () -> Assertions.assertEquals(secondUserPhoto.getPhotoAsBase64(), new String(photoEntity.getPhoto(), StandardCharsets.UTF_8)));
            step("Код страны фотографии должен быть тем же",
                    () -> Assertions.assertEquals(secondUserPhoto.country().getCode(), photoEntity.getCountryCode()));
            step("Описание фотографии должно быть тем же",
                    () -> Assertions.assertEquals(secondUserPhoto.description(), photoEntity.getDescription()));
            step("Лайков должно быть 0",
                    () -> Assertions.assertEquals(0, photoEntity.getLikes().size()));
        });
    }

    @Test
    @GenerateUser(photos = @Photo(country = CountryEnum.KAZAKHSTAN, description = "initial description"))
    @DisplayName("Обновление фотографии на слишком длинное описание должно возвращать INVALID_ARGUMENT")
    void shouldNotUpdatePhotoWithTooLongDescriptionTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel photo = user.photos().get(0);
        String newDescription = DataUtil.generateStringWithLength(256);
        String newCountryCode = DataUtil.generateRandomCountry().getCode();

        StatusRuntimeException e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcBlockingStub.updatePhoto(
                        UpdatePhotoRequest.newBuilder()
                                .setUserId(user.id().toString())
                                .setPhotoId(photo.id().toString())
                                .setCountryCode(newCountryCode)
                                .setDescription(newDescription)
                                .build())
        );
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Too long description").asRuntimeException().getMessage(),
                e.getMessage());
        step("Фотография не должна быть обновлена в БД", () -> {
            List<PhotoEntity> allUsersPhotos = photoRepository.findByUserId(user.id());
            Assertions.assertEquals(1, allUsersPhotos.size());
            PhotoEntity photoEntity = allUsersPhotos.get(0);

            step("id пользователя должен быть тот же",
                    () -> Assertions.assertEquals(user.id(), photoEntity.getUserId()));
            step("id фотографии должно быть тем же",
                    () -> Assertions.assertEquals(photo.id(), photoEntity.getId()));
            step("Фотография должна быть той же",
                    () -> Assertions.assertEquals(photo.getPhotoAsBase64(), new String(photoEntity.getPhoto(), StandardCharsets.UTF_8)));
            step("Код страны фотографии должен быть тем же",
                    () -> Assertions.assertEquals(photo.country().getCode(), photoEntity.getCountryCode()));
            step("Описание фотографии должно быть тем же",
                    () -> Assertions.assertEquals(photo.description(), photoEntity.getDescription()));
            step("Лайков должно быть 0",
                    () -> Assertions.assertEquals(0, photoEntity.getLikes().size()));
        });
    }

    @Test
    @GenerateUser(photos = @Photo(country = CountryEnum.KAZAKHSTAN, description = "initial description"))
    @DisplayName("Обновление фотографии на слишком длинное описание должно возвращать INVALID_ARGUMENT")
    void shouldNotUpdatePhotoWithTooLongCountryCodeTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel photo = user.photos().get(0);
        String newDescription = DataUtil.generateStringWithLength(50);
        String newCountryCode = DataUtil.generateStringWithLength(51);

        StatusRuntimeException e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcBlockingStub.updatePhoto(
                        UpdatePhotoRequest.newBuilder()
                                .setUserId(user.id().toString())
                                .setPhotoId(photo.id().toString())
                                .setCountryCode(newCountryCode)
                                .setDescription(newDescription)
                                .build())
        );
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Too long country code").asRuntimeException().getMessage(),
                e.getMessage());
        step("Фотография не должна быть обновлена в БД", () -> {
            List<PhotoEntity> allUsersPhotos = photoRepository.findByUserId(user.id());
            Assertions.assertEquals(1, allUsersPhotos.size());
            PhotoEntity photoEntity = allUsersPhotos.get(0);

            step("id пользователя должен быть тот же",
                    () -> Assertions.assertEquals(user.id(), photoEntity.getUserId()));
            step("id фотографии должно быть тем же",
                    () -> Assertions.assertEquals(photo.id(), photoEntity.getId()));
            step("Фотография должна быть той же",
                    () -> Assertions.assertEquals(photo.getPhotoAsBase64(), new String(photoEntity.getPhoto(), StandardCharsets.UTF_8)));
            step("Код страны фотографии должен быть тем же",
                    () -> Assertions.assertEquals(photo.country().getCode(), photoEntity.getCountryCode()));
            step("Описание фотографии должно быть тем же",
                    () -> Assertions.assertEquals(photo.description(), photoEntity.getDescription()));
            step("Лайков должно быть 0",
                    () -> Assertions.assertEquals(0, photoEntity.getLikes().size()));
        });
    }
}
