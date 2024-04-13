package guru.qa.rangiffler.test.grpc.photo;

import guru.qa.grpc.rangiffler.grpc.GetStatRequest;
import guru.qa.grpc.rangiffler.grpc.StatMapResponse;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.CountryEnum;
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
@Story("GetStat")
@DisplayName("GetStat")
public class GetStatTest extends BaseGrpcTest {

    @Test
    @GenerateUser(photos = {
            @Photo(country = CountryEnum.SPAIN),
            @Photo(country = CountryEnum.SPAIN),
            @Photo(country = CountryEnum.ITALY),
            @Photo(country = CountryEnum.ITALY),
            @Photo(country = CountryEnum.ITALY),
            @Photo(country = CountryEnum.TURKEY),
    })
    @DisplayName("Должна возвращаться правильная статистика по одному пользователю")
    void getStatWithOneUserTest(@User(FOR_GENERATE_USER) UserModel user) {
        GetStatRequest request = GetStatRequest.newBuilder()
                .addUserId(user.id().toString())
                .build();
        StatMapResponse response = photoGrpcClient.getStat(request);
        step("Проверить, что в ответе ожидаемое количество стран",
                () -> Assertions.assertEquals(3, response.getStatMap().keySet().size()));
        step("Проверить, что в что значения ожидаемые",
                () -> {
                    Assertions.assertEquals(2, response.getStatMap().get(CountryEnum.SPAIN.getCode()));
                    Assertions.assertEquals(3, response.getStatMap().get(CountryEnum.ITALY.getCode()));
                    Assertions.assertEquals(1, response.getStatMap().get(CountryEnum.TURKEY.getCode()));
                });
    }

    @Test
    @GenerateUser
    @DisplayName("Должна возвращаться правильная статистика по одному пользователю без фотографий")
    void getStatWithOneUserWithoutPhotosTest(@User(FOR_GENERATE_USER) UserModel user) {
        GetStatRequest request = GetStatRequest.newBuilder()
                .addUserId(user.id().toString())
                .build();
        StatMapResponse response = photoGrpcClient.getStat(request);
        step("Проверить, что в ответе нет стран",
                () -> Assertions.assertEquals(0, response.getStatMap().keySet().size()));
    }

    @Test
    @GenerateUser(photos = {
            @Photo(country = CountryEnum.SPAIN),
            @Photo(country = CountryEnum.SPAIN),
    })
    @GenerateUser(photos = {
            @Photo(country = CountryEnum.KAZAKHSTAN),
            @Photo(country = CountryEnum.RUSSIAN_FEDERATION),
    })
    @GenerateUser(photos = {
            @Photo(country = CountryEnum.KAZAKHSTAN),
            @Photo(country = CountryEnum.RUSSIAN_FEDERATION),
            @Photo(country = CountryEnum.SPAIN),
    })
    @DisplayName("Должна возвращаться правильная статистика по нескольким пользователям")
    void getStatWithFewUsersTest(@User(FOR_GENERATE_USER) UserModel[] users) {
        UserModel firstUser = users[0];
        UserModel secondUser = users[1];
        UserModel thirdUser = users[2];
        GetStatRequest request = GetStatRequest.newBuilder()
                .addUserId(firstUser.id().toString())
                .addUserId(secondUser.id().toString())
                .addUserId(thirdUser.id().toString())
                .build();
        StatMapResponse response = photoGrpcClient.getStat(request);
        step("Проверить, что в ответе ожидаемое количество стран",
                () -> Assertions.assertEquals(3, response.getStatMap().keySet().size()));
        step("Проверить, что в что значения ожидаемые",
                () -> {
                    Assertions.assertEquals(3, response.getStatMap().get(CountryEnum.SPAIN.getCode()));
                    Assertions.assertEquals(2, response.getStatMap().get(CountryEnum.KAZAKHSTAN.getCode()));
                    Assertions.assertEquals(2, response.getStatMap().get(CountryEnum.RUSSIAN_FEDERATION.getCode()));
                });
    }

    @Test
    @GenerateUser
    @GenerateUser
    @DisplayName("Должна возвращаться правильная статистика по нескольким пользователям без фотографий")
    void getStatWithFewUserWithoutPhotosTest(@User(FOR_GENERATE_USER) UserModel[] user) {
        GetStatRequest request = GetStatRequest.newBuilder()
                .addUserId(user[0].id().toString())
                .addUserId(user[1].id().toString())
                .build();
        StatMapResponse response = photoGrpcClient.getStat(request);
        step("Проверить, что в ответе нет стран",
                () -> Assertions.assertEquals(0, response.getStatMap().keySet().size()));
    }

    @Test
    @GenerateUser
    @DisplayName("Получение статистики по некорректному пользователю должно возвращать INVALID_ARGUMENT")
    void getStatWithIncorrectUserIdTest() {
        GetStatRequest request = GetStatRequest.newBuilder()
                .addUserId("")
                .build();
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.getStat(request));
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad user id").asRuntimeException().getMessage(),
                e.getMessage());
    }

    @Test
    @GenerateUser
    @DisplayName("Получение статистики по одному корректному и одному некорректному пользователю должно возвращать INVALID_ARGUMENT")
    void getStatWithCorrectAndIncorrectUserIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        GetStatRequest request = GetStatRequest.newBuilder()
                .addUserId(user.id().toString())
                .addUserId("")
                .build();
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.getStat(request));
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad user id").asRuntimeException().getMessage(),
                e.getMessage());
    }
}
