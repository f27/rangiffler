package guru.qa.rangiffler.selenide;

import com.codeborne.selenide.WebElementCondition;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.selenide.condition.*;

public class CustomConditions {

    public static WebElementCondition imageAsData(String imageClassPath) {
        return new ImageAsDataCondition(imageClassPath);
    }

    public static WebElementCondition childElementWithText(String cssSelector, String text) {
        return new ChildElementWithTextCondition(cssSelector, text);
    }

    public static WebElementCondition cssValueGreaterThan(String propertyName, Double value) {
        return new CssValueGreaterThan(propertyName, value);
    }

    public static WebElementCondition photo(PhotoModel photo) {
        return new PhotoCondition(photo);
    }

    public static WebElementCondition user(UserModel user) {
        return new UserCondition(user);
    }
}
