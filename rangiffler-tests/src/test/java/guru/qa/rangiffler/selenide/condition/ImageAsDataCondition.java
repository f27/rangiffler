package guru.qa.rangiffler.selenide.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.logevents.SelenideLogger;
import guru.qa.rangiffler.selenide.listener.AllureImageAsDataListener;
import guru.qa.rangiffler.util.ImageUtil;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;

public class ImageAsDataCondition extends WebElementCondition {

    private final String expectedImageData;

    public ImageAsDataCondition(String imageClassPath) {
        super("image data");
        this.expectedImageData = ImageUtil.getImageAsBase64(imageClassPath);
        SelenideLogger.addListener(AllureImageAsDataListener.NAME, new AllureImageAsDataListener());
    }

    @Nonnull
    @Override
    public CheckResult check(Driver driver, WebElement element) {
        String src = element.getAttribute("src");
        final String actualImageData = src == null ? "" : src;
        return new CheckResult(expectedImageData.equals(actualImageData), actualImageData);
    }

    @Nonnull
    @Override
    public final String toString() {
        return String.format("%s \"%s\"", getName(), expectedImageData);
    }
}
