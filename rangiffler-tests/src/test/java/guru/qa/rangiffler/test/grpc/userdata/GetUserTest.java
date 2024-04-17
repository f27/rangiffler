package guru.qa.rangiffler.test.grpc.userdata;

import guru.qa.grpc.rangiffler.grpc.GrpcUser;
import guru.qa.grpc.rangiffler.grpc.Username;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
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

@Feature("USERDATA")
@Story("GetUser")
@DisplayName("GetUser")
public class GetUserTest extends BaseGrpcTest {

    @Test
    @GenerateUser
    @DisplayName("GetUser должен возвращать пользователя при корректном запросе")
    void getUserTest(@User(FOR_GENERATE_USER) UserModel user) {
        GrpcUser response = userdataGrpcBlockingStub.getUser(Username.newBuilder().setUsername(user.username()).build());
        step("Проверить ответ", () -> {
            step("Проверить, что в ответе ожидаемый user id",
                    () -> Assertions.assertEquals(user.id(), UUID.fromString(response.getId())));
            step("Проверить, что в ответе ожидаемый username",
                    () -> Assertions.assertEquals(user.username(), response.getUsername()));
            step("Проверить, что в ответе ожидаемый firstname",
                    () -> Assertions.assertEquals(user.firstname(), response.getFirstname()));
            step("Проверить, что в ответе ожидаемый lastname",
                    () -> Assertions.assertEquals(user.lastname(), response.getLastname()));
            step("Проверить, что в ответе ожидаемый avatar",
                    () -> Assertions.assertEquals(user.getAvatarAsBase64(), response.getAvatar()));
            step("Проверить, что в ответе ожидаемый countryCode",
                    () -> Assertions.assertEquals(user.country().getCode(), response.getCountryCode()));
        });
    }

    @Test
    @GenerateUser(generateLastname = true, generateFirstname = true, avatar = "img/avatar/1.jpg")
    @DisplayName("GetUser должен возвращать пользователя с заполненными полями при корректном запросе")
    void getUserWithFullInfoTest(@User(FOR_GENERATE_USER) UserModel user) {
        GrpcUser response = userdataGrpcBlockingStub.getUser(Username.newBuilder().setUsername(user.username()).build());
        step("Проверить ответ", () -> {
            step("Проверить, что в ответе ожидаемый user id",
                    () -> Assertions.assertEquals(user.id(), UUID.fromString(response.getId())));
            step("Проверить, что в ответе ожидаемый username",
                    () -> Assertions.assertEquals(user.username(), response.getUsername()));
            step("Проверить, что в ответе ожидаемый firstname",
                    () -> Assertions.assertEquals(user.firstname(), response.getFirstname()));
            step("Проверить, что в ответе ожидаемый lastname",
                    () -> Assertions.assertEquals(user.lastname(), response.getLastname()));
            step("Проверить, что в ответе ожидаемый avatar",
                    () -> Assertions.assertEquals(user.getAvatarAsBase64(), response.getAvatar()));
            step("Проверить, что в ответе ожидаемый countryCode",
                    () -> Assertions.assertEquals(user.country().getCode(), response.getCountryCode()));
        });
    }

    @Test
    @GenerateUser
    @DisplayName("GetUser по некорректному пользователю должно возвращать NOT_FOUND")
    void getUserWithIncorrectUsernameTest() {
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> userdataGrpcBlockingStub.getUser(Username.newBuilder().setUsername(".").build()));
        Assertions.assertEquals(
                Status.NOT_FOUND.withDescription("User not found").asRuntimeException().getMessage(),
                e.getMessage());
    }

    @Test
    @GenerateUser
    @DisplayName("GetUser по пустому пользователю должно возвращать INVALID_ARGUMENT")
    void getUserWithEmptyUsernameTest() {
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> userdataGrpcBlockingStub.getUser(Username.newBuilder().setUsername("").build()));
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Username can't be empty").asRuntimeException().getMessage(),
                e.getMessage());
    }
}
