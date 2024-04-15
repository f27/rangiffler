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

import static guru.qa.rangiffler.db.entity.user.FriendshipStatus.ACCEPTED;
import static guru.qa.rangiffler.db.entity.user.FriendshipStatus.PENDING;
import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;
import static io.qameta.allure.Allure.step;

@Feature("USERDATA")
@Story("AcceptFriend")
@DisplayName("AcceptFriend")
public class AcceptFriendTest extends BaseGrpcTest {
    private final UserdataRepository userdataRepository = new UserdataRepositoryHibernate();

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_RECEIVED))
    @DisplayName("AcceptFriend: можно подтвердить от пользователя отправившего приглашение")
    void acceptFriendTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        GrpcUser response = userdataGrpcClient.acceptFriend(request);

        step("Проверить статус друга в ответе",
                () -> Assertions.assertEquals(FriendStatus.FRIEND, response.getFriendStatus()));
        step("Проверить в БД у текущего пользователя приглашения",
                () -> {
                    UserEntity userEntity = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("Проверить исходящее приглашение", () -> {
                        step("Проверить, что у текущего пользователя 1 исходящее приглашение",
                                () -> Assertions.assertEquals(1, userEntity.getOutcomeInvitations().size()));
                        FriendshipEntity outcomeInvitation = userEntity.getOutcomeInvitations().get(0);
                        step("Проверить, что отправитель приглашения текущий пользователь",
                                () -> Assertions.assertEquals(user.id(), outcomeInvitation.getRequester().getId()));
                        step("Проверить, что получатель приглашения второй пользователь",
                                () -> Assertions.assertEquals(user.friends().get(0).id(), outcomeInvitation.getAddressee().getId()));
                        step("Проверить, что статус приглашения ACCEPTED",
                                () -> Assertions.assertEquals(ACCEPTED, outcomeInvitation.getStatus()));
                    });
                    step("Проверить входящее приглашение", () -> {
                        step("Проверить, что у текущего пользователя 1 входящее приглашение",
                                () -> Assertions.assertEquals(1, userEntity.getOutcomeInvitations().size()));
                        FriendshipEntity incomeInvitation = userEntity.getIncomeInvitations().get(0);
                        step("Проверить, что отправитель приглашения второй пользователь",
                                () -> Assertions.assertEquals(user.friends().get(0).id(), incomeInvitation.getRequester().getId()));
                        step("Проверить, что получатель приглашения текущий пользователь",
                                () -> Assertions.assertEquals(user.id(), incomeInvitation.getAddressee().getId()));
                        step("Проверить, что статус приглашения ACCEPTED",
                                () -> Assertions.assertEquals(ACCEPTED, incomeInvitation.getStatus()));
                    });
                });
    }

    @Test
    @GenerateUser
    @DisplayName("InviteFriend: нельзя подтвердить приглашение от себя")
    void acceptFriendMyselfTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.acceptFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Target user should not be same")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
        step("Проверить в БД у текущего пользователя приглашения",
                () -> {
                    UserEntity userEntity = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("Проверить, что у текущего пользователя 0 исходящих приглашений",
                            () -> Assertions.assertEquals(0, userEntity.getOutcomeInvitations().size()));
                    step("Проверить, что у текущего пользователя 0 входящих приглашений",
                            () -> Assertions.assertEquals(0, userEntity.getIncomeInvitations().size()));
                });
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.FRIEND))
    @DisplayName("InviteFriend: нельзя подтвердить уже подтвержденное приглашение")
    void acceptFriendAlreadyAcceptedTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.acceptFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.ALREADY_EXISTS.withDescription("Invitation already accepted")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
        step("Проверить в БД у текущего пользователя приглашения",
                () -> {
                    UserEntity userEntity = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("Проверить исходящее приглашение", () -> {
                        step("Проверить, что у текущего пользователя 1 исходящее приглашение",
                                () -> Assertions.assertEquals(1, userEntity.getOutcomeInvitations().size()));
                        step("Проверить, что у исходящего приглашения статус ACCEPTED",
                                () -> Assertions.assertEquals(ACCEPTED, userEntity.getOutcomeInvitations().get(0).getStatus()));
                    });

                    step("Проверить входящее приглашение", () -> {
                        step("Проверить, что у текущего пользователя 1 входящее приглашение",
                                () -> Assertions.assertEquals(1, userEntity.getIncomeInvitations().size()));
                        step("Проверить, что у входящего приглашения статус ACCEPTED",
                                () -> Assertions.assertEquals(ACCEPTED, userEntity.getIncomeInvitations().get(0).getStatus()));
                    });
                });
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.NOT_FRIEND))
    @DisplayName("InviteFriend: нельзя подтвердить от пользователя не отправлявшего приглашение")
    void acceptFriendNotReceivedNotSentTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.acceptFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.NOT_FOUND.withDescription("Invitation not exist")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
        step("Проверить в БД у текущего пользователя приглашения",
                () -> {
                    UserEntity userEntity = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("Проверить исходящее приглашение",
                            () -> step("Проверить, что у текущего пользователя 0 исходящих приглашений",
                                    () -> Assertions.assertEquals(0, userEntity.getOutcomeInvitations().size())));

                    step("Проверить входящее приглашение",
                            () -> step("Проверить, что у текущего пользователя 0 входящих приглашений",
                                    () -> Assertions.assertEquals(0, userEntity.getIncomeInvitations().size())));

                });
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_SENT))
    @DisplayName("InviteFriend: нельзя подтвердить от пользователя которому мы отправили приглашение")
    void acceptFriendWeSentTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.acceptFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.NOT_FOUND.withDescription("Invitation not exist")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
        step("Проверить в БД у текущего пользователя приглашения",
                () -> {
                    UserEntity userEntity = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("Проверить исходящее приглашение", () -> {
                        step("Проверить, что у текущего пользователя 1 исходящее приглашение",
                                () -> Assertions.assertEquals(1, userEntity.getOutcomeInvitations().size()));
                        step("Проверить, что у исходящего приглашения статус PENDING",
                                () -> Assertions.assertEquals(PENDING, userEntity.getOutcomeInvitations().get(0).getStatus()));
                    });

                    step("Проверить входящее приглашение",
                            () -> step("Проверить, что у текущего пользователя 0 входящих приглашение",
                                    () -> Assertions.assertEquals(0, userEntity.getIncomeInvitations().size())));
                });
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.NOT_FRIEND))
    @DisplayName("InviteFriend: если username неправильный должно вернуть NOT_FOUND")
    void acceptFriendIncorrectUsernameTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(".")
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.acceptFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.NOT_FOUND.withDescription("User not found")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.NOT_FRIEND))
    @DisplayName("InviteFriend: если username пустой должно вернуть INVALID_ARGUMENT")
    void acceptFriendEmptyUsernameTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername("")
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.acceptFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Username can't be empty")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }

    @Test
    @GenerateUser
    @DisplayName("InviteFriend: если targetUserId неправильный должно вернуть INVALID_ARGUMENT")
    void acceptFriendIncorrectTargetIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.id().toString())
                .setTargetUserId(".")
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.acceptFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Bad UUID")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }

    @Test
    @GenerateUser
    @DisplayName("InviteFriend: если targetUserId несуществующий должно вернуть NOT_FOUND")
    void acceptFriendNotExistingTargetIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.id().toString())
                .setTargetUserId(UUID.randomUUID().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcClient.acceptFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.NOT_FOUND.withDescription("User not found")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }
}
