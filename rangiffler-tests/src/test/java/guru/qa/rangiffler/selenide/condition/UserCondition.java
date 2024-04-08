package guru.qa.rangiffler.selenide.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import guru.qa.rangiffler.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.util.List;

public class UserCondition extends WebElementCondition {

    private final UserModel expectedUser;

    public UserCondition(UserModel expectedUser) {
        super("user");
        this.expectedUser = new UserModel(
                null,
                null,
                expectedUser.username(),
                null,
                expectedUser.firstname() == null ? "" : expectedUser.firstname(),
                expectedUser.lastname() == null ? "" : expectedUser.lastname(),
                expectedUser.avatar() == null ? "" : expectedUser.avatar(),
                expectedUser.country(),
                null,
                null,
                null
        );
    }

    @Nonnull
    @Override
    public CheckResult check(Driver driver, WebElement element) {
        boolean avatarCheckPassed;
        if (expectedUser.avatar().isEmpty()) {
            avatarCheckPassed = element.findElement(By.cssSelector("th svg[data-testid=PersonIcon]")).isDisplayed();
        } else {
            avatarCheckPassed = expectedUser.getAvatarAsBase64().equals(
                    element.findElement(By.cssSelector("th img")).getAttribute("src")
            );
        }

        List<WebElement> tds = element.findElements(By.cssSelector("td"));
        final String actualUsername = tds.get(0).getText();
        final String actualFirstname = tds.get(1).getText();
        final String actualLastname = tds.get(2).getText();
        final String actualCountry = tds.get(3).getText();

        final String message = String.format("avatar, username: %s, name: %s, surname: %s, location: %s",
                actualUsername,
                actualFirstname,
                actualLastname,
                actualCountry
        );

        final boolean checkPassed = avatarCheckPassed
                && expectedUser.username().equals(actualUsername)
                && expectedUser.firstname().equals(actualFirstname)
                && expectedUser.lastname().equals(actualLastname)
                && expectedUser.country().toString().equals(actualCountry);
        return new CheckResult(checkPassed, message);
    }

    @Nonnull
    @Override
    public final String toString() {
        return String.format("%s \"avatar, username: %s, name: %s, surname: %s, location: %s\"",
                getName(),
                expectedUser.username(),
                expectedUser.firstname(),
                expectedUser.lastname(),
                expectedUser.country()
        );
    }
}
