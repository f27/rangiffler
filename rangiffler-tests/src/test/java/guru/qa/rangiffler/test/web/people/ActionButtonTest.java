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
@Story("Кнопка действия")
@DisplayName("Кнопка действия")
public class ActionButtonTest extends BaseWebTest {

    @BeforeEach
    void goToAllPeople() {
        myTravelsPage.getDrawer()
                .clickPeople();
        peoplePage
                .goToAllPeople();
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.FRIEND)))
    @DisplayName("Кнопка действия у друга должна быть [Remove]")
    void friendShouldHaveRemoveButtonTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .checkAction(friend, "Remove");
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.NONE)))
    @DisplayName("Кнопка действия у человека (не друга) должна быть [Add]")
    void notFriendShouldHaveAddButtonTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .checkAction(friend, "Add");
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.OUTCOME_INVITATION)))
    @DisplayName("Кнопка действия у друга, которому отправили приглашение, должна быть [Waiting...]")
    void invitedFriendShouldHaveWaitingButtonTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .checkAction(friend, "Waiting...");
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.INCOME_INVITATION)))
    @DisplayName("Кнопки действия у друга, которому отправили приглашение, должны быть [ACCEPT DECLINE]")
    void friendWhoInvitedMeShouldHaveAcceptAndDeclineButtonsTest(@User UserModel user) {
        UserModel friend = user.friends().get(0);
        peoplePage
                .search(friend.username())
                .checkAction(friend, "ACCEPT DECLINE");
    }
}
