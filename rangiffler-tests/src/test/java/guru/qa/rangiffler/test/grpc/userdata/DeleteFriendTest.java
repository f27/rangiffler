package guru.qa.rangiffler.test.grpc.userdata;

import guru.qa.grpc.rangiffler.grpc.FriendStatus;
import guru.qa.grpc.rangiffler.grpc.FriendshipRequest;
import guru.qa.grpc.rangiffler.grpc.GrpcUser;
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

import static guru.qa.rangiffler.db.entity.user.FriendshipStatus.PENDING;
import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;
import static io.qameta.allure.Allure.step;

@Feature("USERDATA")
@Story("DeleteFriend")
@DisplayName("DeleteFriend")
public class DeleteFriendTest extends BaseGrpcTest {
    private final UserdataRepository userdataRepository = new UserdataRepositoryHibernate();

    @Test
    @GenerateUser(friends = @Friend)
    @DisplayName("DeleteFriend: можно удалить друга")
    void deleteFriendTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        GrpcUser response = userdataGrpcBlockingStub.deleteFriend(request);

        step("Проверить статус друга в ответе",
                () -> Assertions.assertEquals(FriendStatus.NOT_FRIEND, response.getFriendStatus()));
        step("Проверить в БД у текущего пользователя приглашения",
                () -> {
                    UserEntity userEntity = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("Проверить исходящее приглашение",
                            () -> step("Проверить, что у текущего пользователя 0 исходящих приглашений",
                                    () -> Assertions.assertEquals(0, userEntity.getOutcomeInvitations().size())));
                    step("Проверить входящее приглашение",
                            () -> step("Проверить, что у текущего пользователя 0 входящих приглашений",
                                    () -> Assertions.assertEquals(0, userEntity.getOutcomeInvitations().size())));
                });
    }

    @Test
    @GenerateUser
    @DisplayName("DeleteFriend: нельзя удалить дружбу с собой")
    void deleteFriendMyselfTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.deleteFriend(request)
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
    @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_RECEIVED))
    @DisplayName("DeleteFriend: нельзя удалить не подтвержденного друга")
    void deleteFriendAlreadyAcceptedTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.deleteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.NOT_FOUND.withDescription("Accepted invitation not exist")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
        step("Проверить в БД у текущего пользователя приглашения",
                () -> {
                    UserEntity userEntity = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("Проверить исходящее приглашение",
                            () -> step("Проверить, что у текущего пользователя 0 исходящих приглашений",
                                    () -> Assertions.assertEquals(0, userEntity.getOutcomeInvitations().size())));

                    step("Проверить входящее приглашение", () -> {
                        step("Проверить, что у текущего пользователя 1 входящее приглашение",
                                () -> Assertions.assertEquals(1, userEntity.getIncomeInvitations().size()));
                        step("Проверить, что у входящего приглашения статус PENDING",
                                () -> Assertions.assertEquals(PENDING, userEntity.getIncomeInvitations().get(0).getStatus()));
                    });
                });
    }

    @Test
    @GenerateUser(friends = @Friend(status = FriendStatus.NOT_FRIEND))
    @DisplayName("DeleteFriend: нельзя удалить друга не отправлявшего приглашение")
    void deleteFriendNotReceivedNotSentTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.deleteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.NOT_FOUND.withDescription("Accepted invitation not exist")
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
    @DisplayName("DeleteFriend: нельзя удалить друга которому мы отправили приглашение")
    void deleteFriendWeSentTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.deleteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.NOT_FOUND.withDescription("Accepted invitation not exist")
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
    @GenerateUser(friends = @Friend)
    @DisplayName("DeleteFriend: если username неправильный должно вернуть NOT_FOUND")
    void deleteFriendIncorrectUsernameTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(".")
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.deleteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.NOT_FOUND.withDescription("User not found")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }

    @Test
    @GenerateUser(friends = @Friend)
    @DisplayName("DeleteFriend: если username пустой должно вернуть INVALID_ARGUMENT")
    void deleteFriendEmptyUsernameTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername("")
                .setTargetUserId(user.friends().get(0).id().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.deleteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Username can't be empty")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }

    @Test
    @GenerateUser
    @DisplayName("DeleteFriend: если targetUserId неправильный должно вернуть INVALID_ARGUMENT")
    void deleteFriendIncorrectTargetIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.id().toString())
                .setTargetUserId(".")
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.deleteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Bad UUID")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }

    @Test
    @GenerateUser
    @DisplayName("DeleteFriend: если targetUserId несуществующий должно вернуть NOT_FOUND")
    void deleteFriendNotExistingTargetIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.id().toString())
                .setTargetUserId(UUID.randomUUID().toString())
                .build();

        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.deleteFriend(request)
                    );
                    Assertions.assertEquals(
                            Status.NOT_FOUND.withDescription("User not found")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }
}
