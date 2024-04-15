package guru.qa.rangiffler.selenide.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import guru.qa.rangiffler.model.PhotoModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;

public class PhotoCondition extends WebElementCondition {

    private final PhotoModel expectedPhoto;

    public PhotoCondition(PhotoModel expectedPhoto) {
        super("photo");
        this.expectedPhoto = expectedPhoto;
    }

    @Nonnull
    @Override
    public CheckResult check(Driver driver, WebElement element) {
        final String actualImage = element.findElement(By.cssSelector("img.photo-card__image")).getAttribute("src");
        final String actualDescription = element.findElement(By.cssSelector("p.photo-card__content")).getText();
        final String actualCountryName = element.findElement(By.cssSelector("h3.MuiTypography-root")).getText();

        String message = String.format("image, country: %s, description: %s", actualCountryName, actualDescription);

        boolean checkPassed = expectedPhoto.getPhotoAsBase64().equals(actualImage)
                && expectedPhoto.description().equals(actualDescription)
                && expectedPhoto.country().toString().equals(actualCountryName);
        return new CheckResult(checkPassed, message);
    }

    @Nonnull
    @Override
    public final String toString() {
        return String.format("%s \"country: %s, description: %s\"", getName(), expectedPhoto.country(), expectedPhoto.description());
    }
}
