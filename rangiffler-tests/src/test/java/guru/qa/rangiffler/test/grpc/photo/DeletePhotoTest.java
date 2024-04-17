package guru.qa.rangiffler.test.grpc.photo;

import guru.qa.grpc.rangiffler.grpc.DeletePhotoRequest;
import guru.qa.rangiffler.db.entity.photo.PhotoEntity;
import guru.qa.rangiffler.db.repository.PhotoRepository;
import guru.qa.rangiffler.db.repository.hibernate.PhotoRepositoryHibernate;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.grpc.BaseGrpcTest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;
import static io.qameta.allure.Allure.step;

@Feature("PHOTO")
@Story("DeletePhoto")
@DisplayName("DeletePhoto")
public class DeletePhotoTest extends BaseGrpcTest {
    private final PhotoRepository photoRepository = new PhotoRepositoryHibernate();

    @Test
    @GenerateUser(photos = @Photo)
    @DisplayName("Удаление фотографии с корректным запросом не должно выбрасывать исключений")
    void deletePhotoWithCorrectRequestShouldNotThrowExceptionTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel photo = user.photos().get(0);
        step("Отправить запрос и проверить, что нет исключений",
                () -> Assertions.assertDoesNotThrow(
                        () -> photoGrpcBlockingStub.deletePhoto(
                                DeletePhotoRequest.newBuilder()
                                        .setUserId(user.id().toString())
                                        .setPhotoId(photo.id().toString())
                                        .build()))
        );
        step("Фотография не должна быть в БД", () -> {
            List<PhotoEntity> allUsersPhotos = photoRepository.findByUserId(user.id());
            Assertions.assertEquals(0, allUsersPhotos.size());
        });
    }

    @Test
    @GenerateUser(photos = @Photo)
    @DisplayName("Удаление фотографии с некорректным user id должно возвращать INVALID_ARGUMENT")
    void deletePhotoWithIncorrectUserIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel photo = user.photos().get(0);
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcBlockingStub.deletePhoto(DeletePhotoRequest.newBuilder()
                        .setUserId("")
                        .setPhotoId(photo.id().toString())
                        .build())
        );
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad UUID").asRuntimeException().getMessage(),
                e.getMessage());
        step("Фотография должна быть в БД", () -> {
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
    @GenerateUser(photos = @Photo)
    @DisplayName("Удаление фотографии с некорректным photo id должно возвращать INVALID_ARGUMENT")
    void deletePhotoWithIncorrectPhotoIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel photo = user.photos().get(0);
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcBlockingStub.deletePhoto(DeletePhotoRequest.newBuilder()
                        .setUserId(user.id().toString())
                        .setPhotoId("")
                        .build())
        );
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad UUID").asRuntimeException().getMessage(),
                e.getMessage());
        step("Фотография должна быть в БД", () -> {
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
    @GenerateUser(photos = @Photo)
    @GenerateUser(photos = @Photo)
    @DisplayName("Удаление чужой фотографии должно возвращать NOT_FOUND")
    void deleteAnotherUsersPhotoTest(@User(FOR_GENERATE_USER) UserModel[] users) {
        UserModel firstUser = users[0];
        PhotoModel firstUserPhoto = firstUser.photos().get(0);
        UserModel secondUser = users[1];
        PhotoModel secondUserPhoto = secondUser.photos().get(0);
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcBlockingStub.deletePhoto(DeletePhotoRequest.newBuilder()
                        .setUserId(firstUser.id().toString())
                        .setPhotoId(secondUserPhoto.id().toString())
                        .build())
        );
        Assertions.assertEquals(
                Status.NOT_FOUND.withDescription("Photo not found").asRuntimeException().getMessage(),
                e.getMessage());
        step("Фотография первого пользователя должна быть в БД", () -> {
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
        step("Фотография второго пользователя должна быть в БД", () -> {
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
}
