package guru.qa.rangiffler.test.grpc.photo;

import guru.qa.grpc.rangiffler.grpc.DeletePhotoRequest;
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

import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;
import static io.qameta.allure.Allure.step;

@Feature("PHOTO")
@Story("DeletePhoto")
@DisplayName("DeletePhoto")
public class DeletePhotoTest extends BaseGrpcTest {

    @Test
    @GenerateUser(photos = @Photo)
    @DisplayName("Удаление фотографии с корректным запросом не должно выбрасывать исключений")
    void deletePhotoWithCorrectRequestShouldNotThrowExceptionTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel photo = user.photos().get(0);
        step("Отправить запрос и проверить, что нет исключений",
                () -> Assertions.assertDoesNotThrow(
                        () -> photoGrpcClient.deletePhoto(
                                DeletePhotoRequest.newBuilder()
                                        .setUserId(user.id().toString())
                                        .setPhotoId(photo.id().toString())
                                        .build()))
        );
    }

    @Test
    @GenerateUser(photos = @Photo)
    @DisplayName("Удаление фотографии с некорректным user id должно возвращать INVALID_ARGUMENT")
    void deletePhotoWithIncorrectUserIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel photo = user.photos().get(0);
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.deletePhoto(DeletePhotoRequest.newBuilder()
                        .setUserId("")
                        .setPhotoId(photo.id().toString())
                        .build())
        );
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad user id").asRuntimeException().getMessage(),
                e.getMessage());
    }

    @Test
    @GenerateUser(photos = @Photo)
    @DisplayName("Удаление фотографии с некорректным photo id должно возвращать INVALID_ARGUMENT")
    void deletePhotoWithIncorrectPhotoIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.deletePhoto(DeletePhotoRequest.newBuilder()
                        .setUserId(user.id().toString())
                        .setPhotoId("")
                        .build())
        );
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad photo id").asRuntimeException().getMessage(),
                e.getMessage());
    }

    @Test
    @GenerateUser(photos = @Photo)
    @GenerateUser(photos = @Photo)
    @DisplayName("Удаление чужой фотографии должно возвращать NOT_FOUND")
    void deleteAnotherUsersPhotoTest(@User(FOR_GENERATE_USER) UserModel[] users) {
        UserModel firstUser = users[0];
        UserModel secondUser = users[1];
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.deletePhoto(DeletePhotoRequest.newBuilder()
                        .setUserId(firstUser.id().toString())
                        .setPhotoId(secondUser.photos().get(0).id().toString())
                        .build())
        );
        Assertions.assertEquals(
                Status.NOT_FOUND.withDescription("Photo not found").asRuntimeException().getMessage(),
                e.getMessage());
    }
}
