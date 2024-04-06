package guru.qa.rangiffler.selenide.listener;

import com.codeborne.selenide.WebElementsCondition;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.selenide.condition.ExactPhotoCardsCondition;

import java.util.List;

public class CustomElementsConditions {

    public static WebElementsCondition exactPhotoCards(List<PhotoModel> expectedPhotos) {
        return new ExactPhotoCardsCondition(expectedPhotos);
    }

    public static WebElementsCondition exactPhotoCards(PhotoModel... expectedPhotos) {
        return new ExactPhotoCardsCondition(expectedPhotos);
    }
}
