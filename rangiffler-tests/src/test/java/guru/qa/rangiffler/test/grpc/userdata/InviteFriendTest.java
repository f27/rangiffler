package guru.qa.rangiffler.test.grpc.userdata;

import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.grpc.BaseGrpcTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;
import static io.qameta.allure.Allure.step;

@Feature("USERDATA")
@Story("InviteFriend")
@DisplayName("InviteFriend")
public class InviteFriendTest extends BaseGrpcTest {

    @Test
    @GenerateUser
    @GenerateUser
    @DisplayName("InviteFriend: можно пригласить в друзья другого пользователя")
    void inviteFriendTest(@User(FOR_GENERATE_USER) UserModel[] users) {
        UserModel firstUser = users[0];
        UserModel secondUser = users[1];

        step("Проверить статус друга в ответе");
        step("Проверить в БД, что у текущего пользователя появилось исходящее приглашение");

    }

    @Test
    @DisplayName("InviteFriend: нельзя пригласить себя в друзья")
    void inviteFriendMyselfTest() {

    }

    @Test
    @DisplayName("InviteFriend: нельзя пригласить в друзья пользователя от которого уже получили приглашение")
    void inviteFriendAlreadyReceivedInvitationTest() {

    }

    @Test
    @DisplayName("InviteFriend: нельзя пригласить в друзья пользователя которому уже отправили пришлашение")
    void inviteFriendAlreadySentInvitationTest() {

    }

    @Test
    @DisplayName("InviteFriend: нельзя пригласить в друзья друга")
    void inviteFriendAlreadyAcceptedFriendshipTest() {

    }

    @Test
    @DisplayName("InviteFriend: если username неправильный, то должно вернуть NOT_FOUND")
    void inviteFriendIncorrectUsernameTest() {

    }

    @Test
    @DisplayName("InviteFriend: если targetUserId некорректный, то должно вернуть INVALID_ARGUMENT")
    void inviteFriendIncorrectTargetIdTest() {

    }

    @Test
    @DisplayName("InviteFriend: если targetUserId несуществующий, то должно вернуть NOT_FOUND")
    void inviteFriendNotExistingTargetIdTest() {

    }
}
