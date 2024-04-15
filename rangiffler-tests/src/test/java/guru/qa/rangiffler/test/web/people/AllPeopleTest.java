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
@Story("Все люди")
@DisplayName("Все люди")
public class AllPeopleTest extends BaseWebTest {

    @BeforeEach
    void goToAllPeople() {
        myTravelsPage.getDrawer()
                .clickPeople();
        peoplePage
                .goToAllPeople();
    }

    @Test
    @ApiLogin
    @DisplayName("Все люди: не должно быть видно себя")
    void myUserShouldNotBeVisibleTest(@User UserModel user) {
        peoplePage
                .search(user.username())
                .checkNoUsersFound();
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(avatar = "img/avatar/1.jpg")))
    @DisplayName("Все люди: должен быть виден друг")
    void friendShouldBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .userShouldBeVisible(friend);
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_SENT)))
    @DisplayName("Все люди: должен быть виден друг, которому отправили приглашение")
    void invitedFriendShouldBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .userShouldBeVisible(friend);
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_RECEIVED)))
    @DisplayName("Все люди: должен быть виден друг, от которого пришло приглашение")
    void friendWhoInvitedMeShouldBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .userShouldBeVisible(friend);
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.NOT_FRIEND)))
    @DisplayName("Все люди: должен быть виден человек, который без статуса")
    void notFriendShouldBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .userShouldBeVisible(friend);
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(avatar = "img/avatar/1.jpg", generateFirstname = true)))
    @DisplayName("Все люди: должен быть виден друг если искать по имени")
    void friendByFirstnameShouldBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.firstname())
                .userShouldBeVisible(friend);
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(avatar = "img/avatar/1.jpg", generateLastname = true)))
    @DisplayName("Все люди: должен быть виден друг если искать по фамилии")
    void friendByLastnameShouldBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.lastname())
                .userShouldBeVisible(friend);
    }
}
