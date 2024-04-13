package guru.qa.rangiffler.test.grpc.userdata;

import guru.qa.grpc.rangiffler.grpc.UsersRequest;
import guru.qa.grpc.rangiffler.grpc.UsersResponse;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.grpc.BaseGrpcTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;
import static io.qameta.allure.Allure.step;

@Feature("USERDATA")
@Story("GetPeople")
@DisplayName("GetPeople")
public class GetPeopleTest extends BaseGrpcTest {

    @Test
    @GenerateUser
    @GenerateUser
    @DisplayName("GetPeople без указания searchQuery, page и size должен возвращать список пользователей без запрашивающего")
    void getUsersTest(@User(FOR_GENERATE_USER) UserModel[] users) {
        UserModel firstUser = users[0];
        UserModel secondUser = users[1];
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(firstUser.username())
                .build();
        UsersResponse response = userdataGrpcClient.getPeople(request);
        step("Проверить ответ", () -> {
            step("Проверить, что в ответе список пользователей не 0",
                    () -> Assertions.assertNotEquals(0, response.getUsersCount()));
            step("Проверить, что в ответе нет запрашивающего пользователя",
                    () -> Assertions.assertEquals(0, response.getUsersList().stream()
                            .filter(grpcUser -> grpcUser.getUsername().equals(firstUser.username()))
                            .count()
                    ));
            step("Проверить, что в ответе есть второй пользователь",
                    () -> Assertions.assertNotEquals(0, response.getUsersList().stream()
                            .filter(grpcUser -> grpcUser.getUsername().equals(secondUser.username()))
                            .count()
                    ));
            step("Проверить, что в ответе hasNext = false",
                    () -> Assertions.assertFalse(response.getHasNext()));
        });
    }

    @Test
    @GenerateUser
    @GenerateUser
    @DisplayName("GetPeople без указания page и size, с указанием searchQuery с именем второго пользователя" +
            " должен возвращать список пользователей без запрашивающего")
    void getUsersWithSearchQueryTest(@User(FOR_GENERATE_USER) UserModel[] users) {
        UserModel firstUser = users[0];
        UserModel secondUser = users[1];
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(firstUser.username())
                .setSearchQuery(secondUser.username())
                .build();
        UsersResponse response = userdataGrpcClient.getPeople(request);
        step("Проверить ответ", () -> {
            step("Проверить, что в ответе список пользователей не 0",
                    () -> Assertions.assertNotEquals(0, response.getUsersCount()));
            step("Проверить, что в ответе нет запрашивающего пользователя",
                    () -> Assertions.assertEquals(0, response.getUsersList().stream()
                            .filter(grpcUser -> grpcUser.getUsername().equals(firstUser.username()))
                            .count()
                    ));
            step("Проверить, что в ответе есть второй пользователь",
                    () -> Assertions.assertNotEquals(0, response.getUsersList().stream()
                            .filter(grpcUser -> grpcUser.getUsername().equals(secondUser.username()))
                            .count()
                    ));
            step("Проверить, что в ответе hasNext = false",
                    () -> Assertions.assertFalse(response.getHasNext()));
        });
    }

    @Test
    @GenerateUser
    @GenerateUser
    @GenerateUser
    @DisplayName("GetPeople без указания searchQuery, с указанием page и size" +
            " должен возвращать список пользователей без запрашивающего")
    void getUsersWithPageAndSizeTest(@User(FOR_GENERATE_USER) UserModel[] users) {
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(users[0].username())
                .setPage(0)
                .setSize(1)
                .build();
        UsersResponse response = userdataGrpcClient.getPeople(request);
        step("Проверить ответ", () -> {
            step("Проверить, что в ответе список пользователей не 0",
                    () -> Assertions.assertNotEquals(0, response.getUsersCount()));
            step("Проверить, что в ответе нет запрашивающего пользователя",
                    () -> Assertions.assertEquals(0, response.getUsersList().stream()
                            .filter(grpcUser -> grpcUser.getUsername().equals(users[0].username()))
                            .count()
                    ));
            step("Проверить, что в ответе hasNext = true",
                    () -> Assertions.assertTrue(response.getHasNext()));
        });
    }
}
