package guru.qa.rangiffler.test.grpc.userdata;

import guru.qa.grpc.rangiffler.grpc.FriendStatus;
import guru.qa.grpc.rangiffler.grpc.FriendshipRequest;
import guru.qa.grpc.rangiffler.grpc.GrpcUser;
import guru.qa.rangiffler.db.entity.user.FriendshipEntity;
import guru.qa.rangiffler.db.entity.user.UserEntity;
import guru.qa.rangiffler.db.repository.UserdataRepository;
import guru.qa.rangiffler.db.repository.hibernate.UserdataRepositoryHibernate;
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
@Story("InviteFriend")
@DisplayName("InviteFriend")
public class InviteFriendTest extends BaseGrpcTest {
    private final UserdataRepository userdataRepository = new UserdataRepositoryHibernate();

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.NOT_FRIEND))
    @DisplayName("InviteFriend: можно пригласить в друзья другого пользователя")
    void inviteFriendTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        GrpcUser response = userdataGrpcClient.inviteFriend(request);

        step("Проверить статус друга в ответе",
                () -> Assertions.assertEquals(FriendStatus.INVITATION_SENT, response.getFriendStatus()));
        step("Проверить в БД, что у текущего пользователя появилось исходящее приглашение",
                () -> {
                    UserEntity userEntity = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("Проверить, что у текущего пользователя 1 исходящее приглашение",
                            () -> Assertions.assertEquals(1, userEntity.getOutcomeInvitations().size()));
                    FriendshipEntity invitation = userEntity.getOutcomeInvitations().get(0);
                    step("Проверить, что отправитель приглашения текущий пользователь",
                            () -> Assertions.assertEquals(user.id(), invitation.getRequester().getId()));
                    step("Проверить, что получатель приглашения второй пользователь",
                            () -> Assertions.assertEquals(user.friends().get(0).id(), invitation.getAddressee().getId()));
                });
    }

    @Test
    @GenerateUser
    @DisplayName("InviteFriend: нельзя пригласить себя в друзья")
    void inviteFriendMyselfTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.inviteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Target user should not be same")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_RECEIVED))
    @DisplayName("InviteFriend: нельзя пригласить в друзья пользователя от которого уже получили приглашение")
    void inviteFriendAlreadyReceivedInvitationTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.inviteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.ALREADY_EXISTS.withDescription("Invitation already exist")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_SENT))
    @DisplayName("InviteFriend: нельзя пригласить в друзья пользователя которому уже отправили приглашение")
    void inviteFriendAlreadySentInvitationTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.inviteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.ALREADY_EXISTS.withDescription("Invitation already exist")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.FRIEND))
    @DisplayName("InviteFriend: нельзя пригласить в друзья друга")
    void inviteFriendAlreadyAcceptedFriendshipTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.inviteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.ALREADY_EXISTS.withDescription("Invitation already exist")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.NOT_FRIEND))
    @DisplayName("InviteFriend: если username неправильный, то должно вернуть NOT_FOUND")
    void inviteFriendIncorrectUsernameTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(".")
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.inviteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.NOT_FOUND.withDescription("User not found")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.NOT_FRIEND))
    @DisplayName("InviteFriend: если username пустой, то должно вернуть INVALID_ARGUMENT")
    void inviteFriendEmptyUsernameTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername("")
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.inviteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Username can't be empty")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.NOT_FRIEND))
    @DisplayName("InviteFriend: если targetUserId некорректный, то должно вернуть INVALID_ARGUMENT")
    void inviteFriendIncorrectTargetIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(".")
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.inviteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Bad UUID")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.NOT_FRIEND))
    @DisplayName("InviteFriend: если targetUserId несуществующий, то должно вернуть NOT_FOUND")
    void inviteFriendNotExistingTargetIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(UUID.randomUUID().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.inviteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.NOT_FOUND.withDescription("Target user not found")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }
}
