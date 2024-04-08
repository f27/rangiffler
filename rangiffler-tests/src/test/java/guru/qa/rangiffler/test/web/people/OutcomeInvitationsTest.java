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
@Story("Исходящие приглашения в друзья")
@DisplayName("Исходящие приглашения в друзья")
public class OutcomeInvitationsTest extends BaseWebTest {

    @BeforeEach
    void goToOutcomeInvitations() {
        myTravelsPage.getDrawer()
                .clickPeople();
        peoplePage
                .goToOutcomeInvitations();
    }

    @Test
    @ApiLogin
    @DisplayName("Исходящие приглашения в друзья: не должно быть видно себя")
    void myUserShouldNotBeVisibleTest(@User UserModel user) {
        peoplePage
                .search(user.username())
                .checkNoUsersFound();
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(avatar = "img/avatar/1.jpg")))
    @DisplayName("Исходящие приглашения в друзья: не должен быть виден друг")
    void friendShouldNotBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .checkNoUsersFound();
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.OUTCOME_INVITATION)))
    @DisplayName("Исходящие приглашения в друзья: должен быть виден друг, которому отправили приглашение")
    void invitedFriendShouldBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .userShouldBeVisible(friend);
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.INCOME_INVITATION)))
    @DisplayName("Исходящие приглашения в друзья: не должен быть виден друг, от которого пришло приглашение")
    void friendWhoInvitedMeShouldNotBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .checkNoUsersFound();
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.NONE)))
    @DisplayName("Исходящие приглашения в друзья: не должен быть виден человек, который без статуса")
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
            status = FriendStatus.OUTCOME_INVITATION)
    ))
    @DisplayName("Исходящие приглашения в друзья: должен быть виден друг, которому отправил приглашение, если искать по имени")
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
            status = FriendStatus.OUTCOME_INVITATION)
    ))
    @DisplayName("Исходящие приглашения в друзья: должен быть виден друг, которому отправил приглашение, если искать по фамилии")
    void friendByLastnameShouldBeVisibleTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.lastname())
                .userShouldBeVisible(friend);
    }
}
