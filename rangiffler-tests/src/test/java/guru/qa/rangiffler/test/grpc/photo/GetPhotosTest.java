package guru.qa.rangiffler.test.grpc.photo;

import guru.qa.grpc.rangiffler.grpc.GetPhotosRequest;
import guru.qa.grpc.rangiffler.grpc.PhotoResponse;
import guru.qa.grpc.rangiffler.grpc.PhotoSliceResponse;
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

import java.util.List;

import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;
import static io.qameta.allure.Allure.step;

@Feature("PHOTO")
@Story("GetPhotos")
@DisplayName("GetPhotos")
public class GetPhotosTest extends BaseGrpcTest {

    @Test
    @GenerateUser(photos = {
            @Photo(description = "1st photo"),
            @Photo(description = "2nd photo")
    })
    @DisplayName("Ответ на получение фотографий должен содержать список с фотографиями и hasNext")
    void getPhotosResponseShouldContainPhotoListAndHasNextTest(@User(FOR_GENERATE_USER) UserModel user) {
        PhotoModel firstExpectedPhoto = user.photos().get(0);
        PhotoModel secondExpectedPhoto = user.photos().get(1);
        GetPhotosRequest request = GetPhotosRequest.newBuilder()
                .addUserId(user.id().toString())
                .setPage(0)
                .setSize(2)
                .build();
        PhotoSliceResponse response = photoGrpcClient.getPhotos(request);
        List<PhotoResponse> photosFromResponse = response.getPhotosList();

        step("Проверить, что количество фотографий в ответе ожидаемое", () ->
                Assertions.assertEquals(2, photosFromResponse.size()));
        step("Проверить, что hasNext имеет ожидаемое значение", () ->
                Assertions.assertFalse(response.getHasNext()));
        step("Проверить первую фотографию", () -> {
            PhotoResponse photoFromResponse = photosFromResponse.stream()
                    .filter(photoResponse -> photoResponse.getPhotoId().equals(firstExpectedPhoto.id().toString()))
                    .findAny().orElseThrow();
            step("Фотография должна быть ожидаемой",
                    () -> Assertions.assertEquals(firstExpectedPhoto.getPhotoAsBase64(), photoFromResponse.getSrc()));
            step("Страна должна быть ожидаемой",
                    () -> Assertions.assertEquals(firstExpectedPhoto.country().getCode(), photoFromResponse.getCountryCode()));
            step("Описание должно быть ожидаемым",
                    () -> Assertions.assertEquals(firstExpectedPhoto.description(), photoFromResponse.getDescription()));
            step("Количество лайков должно быть ожидаемым",
                    () -> Assertions.assertEquals(0, photoFromResponse.getLikes().getLikesCount()));
        });
        step("Проверить вторую фотографию", () -> {
            PhotoResponse photoFromResponse = photosFromResponse.stream()
                    .filter(photoResponse -> photoResponse.getPhotoId().equals(secondExpectedPhoto.id().toString()))
                    .findAny().orElseThrow();
            step("Фотография должна быть ожидаемой",
                    () -> Assertions.assertEquals(secondExpectedPhoto.getPhotoAsBase64(), photoFromResponse.getSrc()));
            step("Страна должна быть ожидаемой",
                    () -> Assertions.assertEquals(secondExpectedPhoto.country().getCode(), photoFromResponse.getCountryCode()));
            step("Описание должно быть ожидаемым",
                    () -> Assertions.assertEquals(secondExpectedPhoto.description(), photoFromResponse.getDescription()));
            step("Количество лайков должно быть ожидаемым",
                    () -> Assertions.assertEquals(0, photoFromResponse.getLikes().getLikesCount()));
        });
    }

    @Test
    @GenerateUser(photos = {
            @Photo(description = "1st photo"),
    })
    @DisplayName("Ответ на получение фотографий должен содержать значение hasNext false если в списке все фотографии")
    void hasNextShouldBeFalseIfResponseContainsAllPhotosTest(@User(FOR_GENERATE_USER) UserModel user) {
        GetPhotosRequest request = GetPhotosRequest.newBuilder()
                .addUserId(user.id().toString())
                .setPage(0)
                .setSize(1)
                .build();
        PhotoSliceResponse response = photoGrpcClient.getPhotos(request);

        step("Проверить, что hasNext в ответе ожидаемый", () ->
                Assertions.assertFalse(response.getHasNext()));
    }

    @Test
    @GenerateUser(photos = {
            @Photo(description = "1st photo"),
            @Photo(description = "1st photo"),
    })
    @DisplayName("Ответ на получение фотографий должен содержать значение hasNext false если в списке все фотографии")
    void hasNextShouldBeTrueIfResponseContainsNotAllPhotosTest(@User(FOR_GENERATE_USER) UserModel user) {
        GetPhotosRequest request = GetPhotosRequest.newBuilder()
                .addUserId(user.id().toString())
                .setPage(0)
                .setSize(1)
                .build();
        PhotoSliceResponse response = photoGrpcClient.getPhotos(request);

        step("Проверить, что hasNext в ответе ожидаемый", () ->
                Assertions.assertTrue(response.getHasNext()));
    }

    @Test
    @GenerateUser(photos = @Photo(description = "1st user photo"))
    @GenerateUser(photos = @Photo(description = "2nd user photo"))
    @DisplayName("Ответ на получение фотографий если указаны несколько пользователей" +
            " должен содержать список с фотографиями и hasNext")
    void getPhotosResponseShouldContainPhotoListAndHasNextWhenTwoUsersInRequestTest(@User(FOR_GENERATE_USER) UserModel[] users) {
        UserModel firstUser = users[0];
        UserModel secondUser = users[1];
        PhotoModel firstExpectedPhoto = firstUser.photos().get(0);
        PhotoModel secondExpectedPhoto = secondUser.photos().get(0);
        GetPhotosRequest request = GetPhotosRequest.newBuilder()
                .addUserId(firstUser.id().toString())
                .addUserId(secondUser.id().toString())
                .setPage(0)
                .setSize(2)
                .build();
        PhotoSliceResponse response = photoGrpcClient.getPhotos(request);
        List<PhotoResponse> photosFromResponse = response.getPhotosList();

        step("Проверить, что количество фотографий в ответе ожидаемое", () ->
                Assertions.assertEquals(2, photosFromResponse.size()));
        step("Проверить, что hasNext имеет ожидаемое значение", () ->
                Assertions.assertFalse(response.getHasNext()));
        step("Проверить первую фотографию", () -> {
            PhotoResponse photoFromResponse = photosFromResponse.stream()
                    .filter(photoResponse -> photoResponse.getPhotoId().equals(firstExpectedPhoto.id().toString()))
                    .findAny().orElseThrow();
            step("Фотография должна быть ожидаемой",
                    () -> Assertions.assertEquals(firstExpectedPhoto.getPhotoAsBase64(), photoFromResponse.getSrc()));
            step("Страна должна быть ожидаемой",
                    () -> Assertions.assertEquals(firstExpectedPhoto.country().getCode(), photoFromResponse.getCountryCode()));
            step("Описание должно быть ожидаемым",
                    () -> Assertions.assertEquals(firstExpectedPhoto.description(), photoFromResponse.getDescription()));
            step("Количество лайков должно быть ожидаемым",
                    () -> Assertions.assertEquals(0, photoFromResponse.getLikes().getLikesCount()));
        });
        step("Проверить вторую фотографию", () -> {
            PhotoResponse photoFromResponse = photosFromResponse.stream()
                    .filter(photoResponse -> photoResponse.getPhotoId().equals(secondExpectedPhoto.id().toString()))
                    .findAny().orElseThrow();
            step("Фотография должна быть ожидаемой",
                    () -> Assertions.assertEquals(secondExpectedPhoto.getPhotoAsBase64(), photoFromResponse.getSrc()));
            step("Страна должна быть ожидаемой",
                    () -> Assertions.assertEquals(secondExpectedPhoto.country().getCode(), photoFromResponse.getCountryCode()));
            step("Описание должно быть ожидаемым",
                    () -> Assertions.assertEquals(secondExpectedPhoto.description(), photoFromResponse.getDescription()));
            step("Количество лайков должно быть ожидаемым",
                    () -> Assertions.assertEquals(0, photoFromResponse.getLikes().getLikesCount()));
        });
    }

    @Test
    @GenerateUser(photos = @Photo)
    @DisplayName("Получение фотографий по некорректному user id должно возвращать INVALID_ARGUMENT")
    void getPhotosWithIncorrectUserIdTest() {
        GetPhotosRequest request = GetPhotosRequest.newBuilder()
                .addUserId("")
                .setPage(0)
                .setSize(1)
                .build();
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.getPhotos(request));
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad user id").asRuntimeException().getMessage(),
                e.getMessage());
    }

    @Test
    @GenerateUser(photos = @Photo)
    @DisplayName("Получение фотографий по одному верному и одному некорректному user id должно возвращать INVALID_ARGUMENT")
    void getPhotosWithCorrectAndIncorrectUserIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        GetPhotosRequest request = GetPhotosRequest.newBuilder()
                .addUserId(user.id().toString())
                .addUserId("")
                .setPage(0)
                .setSize(1)
                .build();
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.getPhotos(request));
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad user id").asRuntimeException().getMessage(),
                e.getMessage());
    }

    @Test
    @GenerateUser(photos = @Photo)
    @DisplayName("Получение фотографий с некорректным size должно возвращать INVALID_ARGUMENT")
    void getPhotosWithIncorrectSizeTest(@User(FOR_GENERATE_USER) UserModel user) {
        GetPhotosRequest request = GetPhotosRequest.newBuilder()
                .addUserId(user.id().toString())
                .setPage(0)
                .setSize(0)
                .build();
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.getPhotos(request));
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad size").asRuntimeException().getMessage(),
                e.getMessage());
    }

    @Test
    @GenerateUser(photos = @Photo)
    @DisplayName("Получение фотографий с некорректным page должно возвращать INVALID_ARGUMENT")
    void getPhotosWithIncorrectPageTest(@User(FOR_GENERATE_USER) UserModel user) {
        GetPhotosRequest request = GetPhotosRequest.newBuilder()
                .addUserId(user.id().toString())
                .setPage(-1)
                .setSize(1)
                .build();
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.getPhotos(request));
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad page").asRuntimeException().getMessage(),
                e.getMessage());
    }
}
