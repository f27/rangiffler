package guru.qa.rangiffler.test.grpc.userdata;

import guru.qa.grpc.rangiffler.grpc.FriendStatus;
import guru.qa.grpc.rangiffler.grpc.FriendsIdsResponse;
import guru.qa.grpc.rangiffler.grpc.Username;
import guru.qa.rangiffler.jupiter.annotation.Friend;
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
@Story("GetFriendsIds")
@DisplayName("GetFriendsIds")
public class GetFriendsIdsTest extends BaseGrpcTest {

    @Test
    @GenerateUser
    @DisplayName("GetFriendsIds: правильный username, нет друзей, входящих и исходящих приглашений")
    void getFriendsIdsTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendsIdsResponse response = userdataGrpcClient.getFriendsIds(
                Username.newBuilder().setUsername(user.username()).build());

        step("Список id должен быть пуст",
                () -> Assertions.assertEquals(0, response.getFriendsIdsCount()));
    }

    @Test
    @GenerateUser(friends = @Friend)
    @DisplayName("GetFriendsIds: правильный username, есть друзья, нет входящих и исходящих приглашений")
    void getFriendsIdsWithFriendsTest(@User(FOR_GENERATE_USER) UserModel user) {
        UUID friendsId = user.friends().get(0).id();

        FriendsIdsResponse response = userdataGrpcClient.getFriendsIds(
                Username.newBuilder().setUsername(user.username()).build());

        step("Список должен содержать 1 id",
                () -> Assertions.assertEquals(1, response.getFriendsIdsCount()));
        step("Список id должен содержать id друга",
                () -> Assertions.assertEquals(friendsId, UUID.fromString(response.getFriendsIds(0))));
    }

    @Test
    @GenerateUser(friends = {
            @Friend,
            @Friend(status = FriendStatus.INVITATION_RECEIVED)
    })
    @DisplayName("GetFriendsIds: правильный username, есть друзья, входящие приглашения, нет исходящих приглашений")
    void getFriendsIdsWithFriendsAndIncomingInvitationsTest(@User(FOR_GENERATE_USER) UserModel user) {
        UUID friendsId = user.friends().get(0).id();

        FriendsIdsResponse response = userdataGrpcClient.getFriendsIds(
                Username.newBuilder().setUsername(user.username()).build());

        step("Список должен содержать 1 id",
                () -> Assertions.assertEquals(1, response.getFriendsIdsCount()));
        step("Список id должен содержать id друга",
                () -> Assertions.assertEquals(friendsId, UUID.fromString(response.getFriendsIds(0))));
    }

    @Test
    @GenerateUser(friends = {
            @Friend,
            @Friend(status = FriendStatus.INVITATION_SENT)
    })
    @DisplayName("GetFriendsIds: правильный username, есть друзья, исходящие приглашения, нет входящих приглашений")
    void getFriendsIdsWithFriendsAndOutcomingInvitationsTest(@User(FOR_GENERATE_USER) UserModel user) {
        UUID friendsId = user.friends().get(0).id();

        FriendsIdsResponse response = userdataGrpcClient.getFriendsIds(
                Username.newBuilder().setUsername(user.username()).build());

        step("Список должен содержать 1 id",
                () -> Assertions.assertEquals(1, response.getFriendsIdsCount()));
        step("Список id должен содержать id друга",
                () -> Assertions.assertEquals(friendsId, UUID.fromString(response.getFriendsIds(0))));
    }

    @Test
    @GenerateUser(friends = {
            @Friend,
            @Friend(status = FriendStatus.INVITATION_SENT),
            @Friend(status = FriendStatus.INVITATION_RECEIVED)
    })
    @DisplayName("GetFriendsIds: правильный username, есть друзья, входящие и исходящие приглашения")
    void getFriendsIdsWithFriendsAndIncomingAndOutcomingInvitationsTest(@User(FOR_GENERATE_USER) UserModel user) {
        UUID friendsId = user.friends().get(0).id();

        FriendsIdsResponse response = userdataGrpcClient.getFriendsIds(
                Username.newBuilder().setUsername(user.username()).build());

        step("Список должен содержать 1 id",
                () -> Assertions.assertEquals(1, response.getFriendsIdsCount()));
        step("Список id должен содержать id друга",
                () -> Assertions.assertEquals(friendsId, UUID.fromString(response.getFriendsIds(0))));
    }

    @Test
    @GenerateUser(friends = {
            @Friend(status = FriendStatus.INVITATION_RECEIVED)
    })
    @DisplayName("GetFriendsIds: правильный username, есть входящие приглашения, нет исходящих приглашений и друзей")
    void getFriendsIdsWithAndIncomingInvitationsTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendsIdsResponse response = userdataGrpcClient.getFriendsIds(
                Username.newBuilder().setUsername(user.username()).build());

        step("Список id должен быть пуст",
                () -> Assertions.assertEquals(0, response.getFriendsIdsCount()));
    }

    @Test
    @GenerateUser(friends = {
            @Friend(status = FriendStatus.INVITATION_SENT)
    })
    @DisplayName("GetFriendsIds: правильный username, есть исходящие приглашения, нет входящих приглашений и друзей")
    void getFriendsIdsWithOutcomingInvitationsTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendsIdsResponse response = userdataGrpcClient.getFriendsIds(
                Username.newBuilder().setUsername(user.username()).build());

        step("Список id должен быть пуст",
                () -> Assertions.assertEquals(0, response.getFriendsIdsCount()));
    }

    @Test
    @GenerateUser(friends = {
            @Friend(status = FriendStatus.INVITATION_SENT),
            @Friend(status = FriendStatus.INVITATION_RECEIVED)
    })
    @DisplayName("GetFriendsIds: правильный username, есть входящие и исходящие приглашения, нет друзей")
    void getFriendsIdsWithIncomingAndOutcomingInvitationsTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendsIdsResponse response = userdataGrpcClient.getFriendsIds(
                Username.newBuilder().setUsername(user.username()).build());

        step("Список id должен быть пуст",
                () -> Assertions.assertEquals(0, response.getFriendsIdsCount()));
    }

    @Test
    @GenerateUser
    @DisplayName("GetFriendsIds: неправильный username. Должен вернуть NOT_FOUND")
    void getFriendsIdsIncorrectUsernameTest() {
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> userdataGrpcClient.getFriendsIds(Username.newBuilder().setUsername("").build())
        );
        Assertions.assertEquals(
                Status.NOT_FOUND.withDescription("User not found").asRuntimeException().getMessage(),
                e.getMessage());
    }
}
