package guru.qa.rangiffler.selenide;

import com.codeborne.selenide.WebElementCondition;
import guru.qa.rangiffler.selenide.condition.ChildElementWithTextCondition;
import guru.qa.rangiffler.selenide.condition.ImageAsDataCondition;

public class CustomConditions {

    public static WebElementCondition imageAsData(String imageClassPath) {
        return new ImageAsDataCondition(imageClassPath);
    }

    public static WebElementCondition childElementWithText(String cssSelector, String text) {
        return new ChildElementWithTextCondition(cssSelector, text);
    }
}
