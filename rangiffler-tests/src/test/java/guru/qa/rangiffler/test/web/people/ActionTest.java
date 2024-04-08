package guru.qa.rangiffler.test.web.people;

import com.codeborne.selenide.Selenide;
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
@Story("Действия")
@DisplayName("Действия")
public class ActionTest extends BaseWebTest {

    @BeforeEach
    void goToAllPeople() {
        myTravelsPage.getDrawer()
                .clickPeople();
        peoplePage
                .goToAllPeople();
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.NONE)))
    @DisplayName("Отправить приглашение в друзья")
    void sendInviteToFriendsTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .clickActionButtonForUser(friend, "Add")
                .checkAction(friend, "Waiting...");
        Selenide.refresh();
        peoplePage
                .goToOutcomeInvitations()
                .search(friend.username())
                .userShouldBeVisible(friend)
                .checkAction(friend, "Waiting...");
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.INCOME_INVITATION)))
    @DisplayName("Принять приглашение в друзья")
    void acceptInviteToFriendsTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .clickActionButtonForUser(friend, "Accept")
                .checkAction(friend, "Remove");
        Selenide.refresh();
        peoplePage
                .goToFriends()
                .search(friend.username())
                .userShouldBeVisible(friend)
                .checkAction(friend, "Remove");
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.INCOME_INVITATION)))
    @DisplayName("Отклонить приглашение в друзья")
    void declineInviteToFriendsTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .clickActionButtonForUser(friend, "Decline")
                .checkAction(friend, "Add");
        Selenide.refresh();
        peoplePage
                .goToAllPeople()
                .search(friend.username())
                .userShouldBeVisible(friend)
                .checkAction(friend, "Add");
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.FRIEND)))
    @DisplayName("Удалить человека из друзей")
    void deleteFriendTest() {

    }
}
