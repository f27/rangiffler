package guru.qa.rangiffler.test.grpc.photo;

import guru.qa.grpc.rangiffler.grpc.LikePhotoRequest;
import guru.qa.grpc.rangiffler.grpc.PhotoResponse;
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

import java.util.UUID;

import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;
import static io.qameta.allure.Allure.step;

@Feature("PHOTO")
@Story("LikePhoto")
@DisplayName("LikePhoto")
public class LikePhotoTest extends BaseGrpcTest {

    @Test
    @GenerateUser(photos = @Photo)
    @DisplayName("Ответ на лайк фотографии должен содержать информацию о фотографии и должен добавить лайк")
    void likePhotoResponseShouldHavePhotoInfoWithLikesTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel photo = user.photos().get(0);

        PhotoResponse response = photoGrpcClient.likePhoto(LikePhotoRequest.newBuilder()
                .setUserId(user.id().toString())
                .setPhotoId(photo.id().toString())
                .build());

        step("id пользователя должен быть тот, что отправили",
                () -> Assertions.assertEquals(user.id(), UUID.fromString(response.getUserId())));
        step("id фотографии должно быть тем же, что и отправили",
                () -> Assertions.assertEquals(photo.id().toString(), response.getPhotoId()));
        step("Фотография должна быть той же, что и была",
                () -> Assertions.assertEquals(photo.getPhotoAsBase64(), response.getSrc()));
        step("Код страны фотографии должен быть тем же, что и был",
                () -> Assertions.assertEquals(photo.country().getCode(), response.getCountryCode()));
        step("Описание фотографии должно быть тем же, что и был",
                () -> Assertions.assertEquals(photo.description(), response.getDescription()));
        step("Лайков должно быть 1",
                () -> Assertions.assertEquals(1, response.getLikes().getLikesCount()));
    }

    @Test
    @GenerateUser(photos = @Photo)
    @DisplayName("Ответ на повторный лайк фотографии должен содержать информацию о фотографии и должен убрать лайк")
    void unlikePhotoResponseShouldHavePhotoInfoWithLikesTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel photo = user.photos().get(0);
        photoGrpcClient.likePhoto(LikePhotoRequest.newBuilder()
                .setUserId(user.id().toString())
                .setPhotoId(photo.id().toString())
                .build());

        PhotoResponse unlikeResponse = photoGrpcClient.likePhoto(LikePhotoRequest.newBuilder()
                .setUserId(user.id().toString())
                .setPhotoId(photo.id().toString())
                .build());

        step("id пользователя должен быть тот, что отправили",
                () -> Assertions.assertEquals(user.id(), UUID.fromString(unlikeResponse.getUserId())));
        step("id фотографии должно быть тем же, что и отправили",
                () -> Assertions.assertEquals(photo.id().toString(), unlikeResponse.getPhotoId()));
        step("Фотография должна быть той же, что и была",
                () -> Assertions.assertEquals(photo.getPhotoAsBase64(), unlikeResponse.getSrc()));
        step("Код страны фотографии должен быть тем же, что и был",
                () -> Assertions.assertEquals(photo.country().getCode(), unlikeResponse.getCountryCode()));
        step("Описание фотографии должно быть тем же, что и был",
                () -> Assertions.assertEquals(photo.description(), unlikeResponse.getDescription()));
        step("Лайков должно быть 0",
                () -> Assertions.assertEquals(0, unlikeResponse.getLikes().getLikesCount()));
    }

    @Test
    @GenerateUser(photos = @Photo)
    @DisplayName("Лайк фотографии с некорректным user id должно возвращать INVALID_ARGUMENT")
    void shouldNotLikePhotoWithIncorrectUserIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel photo = user.photos().get(0);

        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.likePhoto(LikePhotoRequest.newBuilder()
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
    @DisplayName("Лайк фотографии с некорректным photo id должно возвращать INVALID_ARGUMENT")
    void shouldNotLikePhotoWithIncorrectPhotoIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.likePhoto(LikePhotoRequest.newBuilder()
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
    @DisplayName("Лайк фотографии с несуществующим photo id должно возвращать NOT_FOUND")
    void shouldNotLikePhotoWithNotExistingPhotoIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.likePhoto(LikePhotoRequest.newBuilder()
                        .setUserId(user.id().toString())
                        .setPhotoId(UUID.randomUUID().toString())
                        .build())
        );
        Assertions.assertEquals(
                Status.NOT_FOUND.withDescription("Photo not found").asRuntimeException().getMessage(),
                e.getMessage());
    }
}
