package guru.qa.rangiffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.model.UserModel;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.rangiffler.selenide.CustomConditions.user;

public class PeoplePage extends BaseAuthorizedPage<ProfilePage> {

    private final SelenideElement friendsTab = $(byTagAndText("button", "Friends"));
    private final SelenideElement allPeopleTab = $(byTagAndText("button", "All People"));
    private final SelenideElement outcomeInvitationsTab = $(byTagAndText("button", "Outcome invitations"));
    private final SelenideElement incomeInvitationsTab = $(byTagAndText("button", "Income invitations"));
    private final SelenideElement searchPeopleInput = $("input[placeholder='Search people']");
    private final SelenideElement peopleTable = $("table");
    private final ElementsCollection peopleRows = peopleTable.$("tbody").$$("tr");
    private final SelenideElement noUsersYetElement = $(byTagAndText("p", "There are no users yet"));

    @Step("Проверить, выбрать вкладку [Friends]")
    public PeoplePage goToFriends() {
        friendsTab.click();
        friendsTab.shouldHave(attribute("aria-selected", "true"));
        return this;
    }

    @Step("Проверить, выбрать вкладку [All People]")
    public PeoplePage goToAllPeople() {
        allPeopleTab.click();
        allPeopleTab.shouldHave(attribute("aria-selected", "true"));
        return this;
    }

    @Step("Проверить, выбрать вкладку [Outcome invitations]")
    public PeoplePage goToOutcomeInvitations() {
        outcomeInvitationsTab.click();
        outcomeInvitationsTab.shouldHave(attribute("aria-selected", "true"));
        return this;
    }

    @Step("Проверить, выбрать вкладку [Income invitations]")
    public PeoplePage goToIncomeInvitations() {
        incomeInvitationsTab.click();
        incomeInvitationsTab.shouldHave(attribute("aria-selected", "true"));
        return this;
    }

    @Step("Найти [{searchQuery}]")
    public PeoplePage search(String searchQuery) {
        searchPeopleInput.setValue(searchQuery).pressEnter();
        return this;
    }

    @Step("Очистить строку поиска")
    public PeoplePage clearSearchInput() {
        searchPeopleInput.clear();
        searchPeopleInput.pressEnter();
        return this;
    }

    @Step("Проверить, что людей не найдено")
    public PeoplePage checkNoUsersFound() {
        noUsersYetElement.shouldBe(visible);
        return this;
    }

    @Step("Должна быть строка с пользователем [{userModel}]")
    public PeoplePage userShouldBeVisible(UserModel userModel) {
        peopleRows.findBy(user(userModel)).shouldBe(visible);
        return this;
    }

    @Step("Проверить, что кнопка действия у человека [{action}]")
    public PeoplePage checkAction(UserModel userModel, String action) {
        peopleRows.findBy(user(userModel)).$$("td").get(4).shouldHave(text(action));
        return this;
    }

    @Step("Нажать у пользователя [{userModel}] кнопку действия [{action}]")
    public PeoplePage clickActionButtonForUser(UserModel userModel, String action) {
        peopleRows.findBy(user(userModel)).$$("td").get(4).$(byTagAndText("button", action)).click();
        return this;
    }
}
