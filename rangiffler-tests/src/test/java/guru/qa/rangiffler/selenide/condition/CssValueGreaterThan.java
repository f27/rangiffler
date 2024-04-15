package guru.qa.rangiffler.selenide.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;

public class CssValueGreaterThan extends WebElementCondition {

    private final String propertyName;
    private final Double expectedValue;

    public CssValueGreaterThan(String propertyName, Double expectedValue) {
        super("css property '" + propertyName + "' with value greater than");
        this.propertyName = propertyName;
        this.expectedValue = expectedValue;
    }

    @Nonnull
    @Override
    public CheckResult check(Driver driver, WebElement element) {
        String actualValue = element.getCssValue(propertyName);
        if (actualValue == null || actualValue.isEmpty()) {
            return CheckResult.rejected("incorrect actual value", actualValue);
        }
        Double actualValueDouble = Double.parseDouble(actualValue);
        return new CheckResult(expectedValue < actualValueDouble, actualValueDouble);
    }

    @Nonnull
    @Override
    public final String toString() {
        return String.format("%s \"%s\"", getName(), expectedValue);
    }
}
