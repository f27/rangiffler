package guru.qa.rangiffler.test.grpc.userdata;

import guru.qa.grpc.rangiffler.grpc.FriendStatus;
import guru.qa.grpc.rangiffler.grpc.UsersRequest;
import guru.qa.grpc.rangiffler.grpc.UsersResponse;
import guru.qa.rangiffler.jupiter.annotation.Friend;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.grpc.BaseGrpcTest;
import guru.qa.rangiffler.util.DataUtil;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;
import static io.qameta.allure.Allure.step;

@Feature("USERDATA")
@Story("GetIncomeInvitations")
@DisplayName("GetIncomeInvitations")
public class GetIncomeInvitationsTest extends BaseGrpcTest {

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_RECEIVED))
    @DisplayName("GetIncomeInvitations: правильный username, без searchQuery, page, size")
    @Description("Должен вернуть список пользователей без запрашивающего и с пользователем от кого получено приглашение, hasNext = false")
    void getIncomeInvitationsWithCorrectUsernameTest(@User(FOR_GENERATE_USER) UserModel user) {
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(user.username())
                .build();

        UsersResponse response = userdataGrpcClient.getIncomeInvitations(request);

        step("В списке пользователей не должно быть запрашивающего",
                () -> Assertions.assertNull(response.getUsersList().stream()
                        .filter(grpcUser -> user.username().equals(grpcUser.getUsername()))
                        .findAny().orElse(null)
                ));
        step("Список пользователей должен содержать пользователя от кого получено приглашение",
                () -> Assertions.assertEquals(user.friends().get(0).username(), response.getUsers(0).getUsername()));
        step("hasNext должен быть false",
                () -> Assertions.assertFalse(response.getHasNext()));
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_RECEIVED))
    @DisplayName("GetIncomeInvitations: правильный username, с указанием searchQuery(username существующего пользователя), без page, size")
    @Description("Должен вернуть список пользователей без запрашивающего и с пользователем от кого получено приглашение, hasNext = false")
    void getIncomeInvitationsWithCorrectUsernameAndSearchQueryUsernameTest(@User(FOR_GENERATE_USER) UserModel user) {
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(user.username())
                .setSearchQuery(user.friends().get(0).username())
                .build();

        UsersResponse response = userdataGrpcClient.getIncomeInvitations(request);

        step("В списке пользователей не должно быть запрашивающего",
                () -> Assertions.assertNull(response.getUsersList().stream()
                        .filter(grpcUser -> user.username().equals(grpcUser.getUsername()))
                        .findAny().orElse(null)
                ));
        step("Список пользователей должен содержать пользователя от кого получено приглашение",
                () -> Assertions.assertEquals(user.friends().get(0).username(), response.getUsers(0).getUsername()));
        step("hasNext должен быть false",
                () -> Assertions.assertFalse(response.getHasNext()));
    }

    @Test
    @GenerateUser(friends = @Friend(generateFirstname = true, status = FriendStatus.INVITATION_RECEIVED))
    @DisplayName("GetIncomeInvitations: правильный username, с указанием searchQuery(firstname существующего пользователя), без page, size")
    @Description("Должен вернуть список пользователей без запрашивающего и с пользователем от кого получено приглашение, hasNext = false")
    void getIncomeInvitationsWithCorrectUsernameAndSearchQueryFirstnameTest(@User(FOR_GENERATE_USER) UserModel user) {
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(user.username())
                .setSearchQuery(user.friends().get(0).firstname())
                .build();

        UsersResponse response = userdataGrpcClient.getIncomeInvitations(request);

        step("В списке пользователей не должно быть запрашивающего",
                () -> Assertions.assertNull(response.getUsersList().stream()
                        .filter(grpcUser -> user.username().equals(grpcUser.getUsername()))
                        .findAny().orElse(null)
                ));
        step("Список пользователей должен содержать пользователя от кого получено приглашение",
                () -> Assertions.assertEquals(user.friends().get(0).username(), response.getUsers(0).getUsername()));
        step("hasNext должен быть false",
                () -> Assertions.assertFalse(response.getHasNext()));
    }

    @Test
    @GenerateUser(friends = @Friend(generateLastname = true, status = FriendStatus.INVITATION_RECEIVED))
    @DisplayName("GetIncomeInvitations: правильный username, с указанием searchQuery(lastname существующего пользователя), без page, size")
    @Description("Должен вернуть список пользователей без запрашивающего и с пользователем от кого получено приглашение, hasNext = false")
    void getIncomeInvitationsWithCorrectUsernameAndSearchQueryLastnameTest(@User(FOR_GENERATE_USER) UserModel user) {
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(user.username())
                .setSearchQuery(user.friends().get(0).lastname())
                .build();

        UsersResponse response = userdataGrpcClient.getIncomeInvitations(request);

        step("В списке пользователей не должно быть запрашивающего",
                () -> Assertions.assertNull(response.getUsersList().stream()
                        .filter(grpcUser -> user.username().equals(grpcUser.getUsername()))
                        .findAny().orElse(null)
                ));
        step("Список пользователей должен содержать пользователя от кого получено приглашение",
                () -> Assertions.assertEquals(user.friends().get(0).username(), response.getUsers(0).getUsername()));
        step("hasNext должен быть false",
                () -> Assertions.assertFalse(response.getHasNext()));
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_RECEIVED))
    @DisplayName("GetIncomeInvitations: правильный username, с указанием searchQuery(случайная длинная строка), без page, size")
    @Description("Должен вернуть пустой список пользователей, hasNext = false")
    void getIncomeInvitationsWithCorrectUsernameAndSearchQueryRandomLongStringTest(@User(FOR_GENERATE_USER) UserModel user) {
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(user.username())
                .setSearchQuery(DataUtil.generateStringWithLength(255))
                .build();

        UsersResponse response = userdataGrpcClient.getIncomeInvitations(request);

        step("Список пользователей должен быть пустым",
                () -> Assertions.assertEquals(0, response.getUsersCount()));
        step("hasNext должен быть false",
                () -> Assertions.assertFalse(response.getHasNext()));
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_RECEIVED))
    @DisplayName("GetIncomeInvitations: правильный username, с указанием searchQuery(username существующего пользователя), page, size")
    @Description("Должен вернуть список пользователей без запрашивающего и с пользователем от кого получено приглашение")
    void getIncomeInvitationsWithCorrectUsernameAndSearchQueryUsernameAndPageableTest(@User(FOR_GENERATE_USER) UserModel user) {
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(user.username())
                .setSearchQuery(user.friends().get(0).username())
                .setPage(0)
                .setSize(1)
                .build();

        UsersResponse response = userdataGrpcClient.getIncomeInvitations(request);

        step("В списке пользователей не должно быть запрашивающего",
                () -> Assertions.assertNull(response.getUsersList().stream()
                        .filter(grpcUser -> user.username().equals(grpcUser.getUsername()))
                        .findAny().orElse(null)
                ));
        step("Список пользователей должен содержать пользователя от кого получено приглашение",
                () -> Assertions.assertEquals(user.friends().get(0).username(), response.getUsers(0).getUsername()));
    }

    @Test
    @GenerateUser(friends = {
            @Friend(status = FriendStatus.INVITATION_RECEIVED),
            @Friend(status = FriendStatus.INVITATION_RECEIVED)
    })
    @DisplayName("GetIncomeInvitations: правильный username, без searchQuery, с указанием page, size")
    @Description("Должен вернуть список пользователей без запрашивающего и размером не 0, hasNext = true")
    void getIncomeInvitationsWithCorrectUsernameAndPageableTest(@User(FOR_GENERATE_USER) UserModel user) {
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(user.username())
                .setPage(0)
                .setSize(1)
                .build();

        UsersResponse response = userdataGrpcClient.getIncomeInvitations(request);

        step("В списке пользователей не должно быть запрашивающего",
                () -> Assertions.assertNull(response.getUsersList().stream()
                        .filter(grpcUser -> user.username().equals(grpcUser.getUsername()))
                        .findAny().orElse(null)
                ));
        step("Список пользователей должен быть не пустым",
                () -> Assertions.assertNotEquals(0, response.getUsersCount()));
        step("hasNext должен быть true",
                () -> Assertions.assertTrue(response.getHasNext()));
    }

    @Test
    @GenerateUser
    @DisplayName("GetIncomeInvitations: неправильный username, без searchQuery, page, size. Должен вернуть NOT_FOUND")
    void getIncomeInvitationsWithIncorrectTest() {
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername("")
                .build();

        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> userdataGrpcClient.getIncomeInvitations(request)
        );
        Assertions.assertEquals(
                Status.NOT_FOUND.withDescription("User not found").asRuntimeException().getMessage(),
                e.getMessage());
    }
}
