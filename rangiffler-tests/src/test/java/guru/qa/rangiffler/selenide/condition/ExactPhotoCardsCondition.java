package guru.qa.rangiffler.selenide.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.CollectionSource;
import guru.qa.rangiffler.model.PhotoModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.rejected;
import static java.lang.System.lineSeparator;
import static java.util.Collections.unmodifiableList;

public class ExactPhotoCardsCondition extends WebElementsCondition {
    private final List<PhotoModel> expectedPhotos;

    public ExactPhotoCardsCondition(PhotoModel... expectedPhotos) {
        this(Arrays.asList(expectedPhotos));
    }

    public ExactPhotoCardsCondition(List<PhotoModel> expectedPhotos) {
        if (expectedPhotos.isEmpty()) {
            throw new IllegalArgumentException("No expected photos given");
        }
        this.expectedPhotos = unmodifiableList(expectedPhotos);
    }

    @Nonnull
    @Override
    public CheckResult check(Driver driver, List<WebElement> elements) {
        if (elements.size() != expectedPhotos.size()) {
            String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedPhotos.size(), elements.size());
            return rejected(message, elements);
        }

        List<PhotoModel> tempPhotos = new ArrayList<>(expectedPhotos);

        for (WebElement element : elements) {
            final String actualImage = element.findElement(By.cssSelector("img.photo-card__image")).getAttribute("src");
            final String actualDescription = element.findElement(By.cssSelector("p.photo-card__content")).getText();
            final String actualCountryName = element.findElement(By.cssSelector("h3.MuiTypography-root")).getText();

            tempPhotos.stream()
                    .filter(photoModel -> photoModel.getPhotoAsBase64().equals(actualImage))
                    .filter(photoModel -> photoModel.description().equals(actualDescription))
                    .filter(photoModel -> photoModel.country().toString().equals(actualCountryName))
                    .findAny().ifPresent(tempPhotos::remove);

            if (tempPhotos.isEmpty()) {
                return CheckResult.accepted();
            }
        }

        return CheckResult.rejected("Incorrect photo cards content", elements);
    }

    @Override
    public void fail(CollectionSource collection, CheckResult lastCheckResult, @Nullable Exception cause, long timeoutMs) {
        throw new UIAssertionError(collection.driver(),
                lastCheckResult.message() +
                        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
                        lineSeparator() + "Collection: " + collection.description(),
                null, null
        );
    }

    @Override
    public String toString() {
        return "Exact photo cards";
    }
}
