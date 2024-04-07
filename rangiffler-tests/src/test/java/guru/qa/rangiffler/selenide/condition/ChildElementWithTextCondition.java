package guru.qa.rangiffler.selenide.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;

public class ChildElementWithTextCondition extends WebElementCondition {

    private final String cssSelector;
    private final String expectedText;

    public ChildElementWithTextCondition(String cssSelector, String expectedText) {
        super("child element '" + cssSelector + "' with text");
        this.expectedText = expectedText;
        this.cssSelector = cssSelector;
    }

    @Nonnull
    @Override
    public CheckResult check(Driver driver, WebElement element) {
        String actualText = element.findElement(By.cssSelector(cssSelector)).getText();
        return new CheckResult(expectedText.equals(actualText), actualText);
    }

    @Nonnull
    @Override
    public final String toString() {
        return String.format("%s \"%s\"", getName(), expectedText);
    }
}
