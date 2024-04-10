package guru.qa.rangiffler.test.web.people;

import guru.qa.grpc.rangiffler.grpc.FriendStatus;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Friend;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Люди")
@Story("Входящие приглашения в друзья")
@DisplayName("Входящие приглашения в друзья")
public class IncomeInvitationsTest extends BaseWebTest {

    @BeforeEach
    void goToIncomeInvitations() {
        myTravelsPage.getDrawer()
                .clickPeople();
        peoplePage
                .goToIncomeInvitations();
    }

    @Test
    @ApiLogin
    @DisplayName("Входящие приглашения в друзья: не должно быть видно себя")
    void myUserShouldNotBeVisibleTest(@User UserModel user) {
        peoplePage
                .search(user.username())
                .checkNoUsersFound();
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(avatar = "img/avatar/1.jpg")))
    @DisplayName("Входящие приглашения в друзья: не должен быть виден друг")
    void friendShouldNotBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .checkNoUsersFound();
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_SENT)))
    @DisplayName("Входящие приглашения в друзья: не должен быть виден друг, которому отправили приглашение")
    void invitedFriendShouldNotBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .checkNoUsersFound();
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_RECEIVED)))
    @DisplayName("Входящие приглашения в друзья: должен быть виден друг, от которого пришло приглашение")
    void friendWhoInvitedMeShouldBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .userShouldBeVisible(friend);
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.NOT_FRIEND)))
    @DisplayName("Входящие приглашения в друзья: не должен быть виден человек, который без статуса")
    void notFriendShouldNotBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .checkNoUsersFound();
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(
            avatar = "img/avatar/1.jpg",
            generateFirstname = true,
            status = FriendStatus.INVITATION_RECEIVED)
    ))
    @DisplayName("Входящие приглашения в друзья: должен быть виден друг, который отправил приглашение, если искать по имени")
    void friendByFirstnameShouldBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.firstname())
                .userShouldBeVisible(friend);
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(
            avatar = "img/avatar/1.jpg",
            generateLastname = true,
            status = FriendStatus.INVITATION_RECEIVED)
    ))
    @DisplayName("Входящие приглашения в друзья: должен быть виден друг если искать по фамилии")
    void friendByLastnameShouldBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.lastname())
                .userShouldBeVisible(friend);
    }
}
