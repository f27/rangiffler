package guru.qa.rangiffler.test.web.people;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Friend;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.FriendStatus;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Люди")
@Story("Друзья")
@DisplayName("Друзья")
public class FriendsTest extends BaseWebTest {

    @BeforeEach
    void goToFriends() {
        myTravelsPage.getDrawer()
                .clickPeople();
        peoplePage
                .goToFriends();
    }

    @Test
    @ApiLogin
    @DisplayName("Друзья: не должно быть видно себя")
    void myUserShouldNotBeVisibleTest(@User UserModel user) {
        peoplePage
                .search(user.username())
                .checkNoUsersFound();
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(avatar = "img/avatar/1.jpg")))
    @DisplayName("Друзья: должен быть виден друг")
    void friendShouldBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .userShouldBeVisible(friend);
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.OUTCOME_INVITATION)))
    @DisplayName("Друзья: не должен быть виден друг, которому отправили приглашение")
    void invitedFriendShouldNotBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .checkNoUsersFound();
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.INCOME_INVITATION)))
    @DisplayName("Друзья: не должен быть виден друг, от которого пришло приглашение")
    void friendWhoInvitedMeShouldNotBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .checkNoUsersFound();
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.NONE)))
    @DisplayName("Друзья: не должен быть виден человек, который без статуса")
    void notFriendShouldNotBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .checkNoUsersFound();
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(avatar = "img/avatar/1.jpg", generateFirstname = true)))
    @DisplayName("Друзья: должен быть виден друг если искать по имени")
    void friendByFirstnameShouldBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.firstname())
                .userShouldBeVisible(friend);
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(avatar = "img/avatar/1.jpg", generateLastname = true)))
    @DisplayName("Друзья: должен быть виден друг если искать по фамилии")
    void friendByLastnameShouldBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.lastname())
                .userShouldBeVisible(friend);
    }
}
