package guru.qa.rangiffler.test.web.travel;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Friend;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.test.web.BaseWebTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Путешествия")
@Story("Пагинация фотографий")
@DisplayName("Пагинация фотографий")
public class PaginationTest extends BaseWebTest {

    @Test
    @ApiLogin(user = @GenerateUser(photos = {
            @Photo(description = "1"),
            @Photo(description = "2"),
            @Photo(description = "3"),
            @Photo(description = "4"),
            @Photo(description = "5"),
            @Photo(description = "6"),
            @Photo(description = "7"),
            @Photo(description = "8"),
            @Photo(description = "9"),
            @Photo(description = "10"),
    }))
    @DisplayName("Пагинация фотографий в [Only my travels] не должна быть если фотографий 10")
    void paginationShouldNotBeVisibleIfOnlyTenPhotosTest() {
        myTravelsPage
                .checkAmountOfVisiblePhotos(10)
                .checkNoPagination();
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = {
            @Photo(description = "1"),
            @Photo(description = "2"),
            @Photo(description = "3"),
            @Photo(description = "4"),
            @Photo(description = "5"),
            @Photo(description = "6"),
            @Photo(description = "7"),
            @Photo(description = "8"),
            @Photo(description = "9"),
            @Photo(description = "10"),
            @Photo(description = "11"),
    }))
    @DisplayName("Пагинация фотографий в [Only my travels] должна работать если фотографий больше 10")
    void paginationShouldNotBeVisibleIfOnlyOnePhotoTest() {
        myTravelsPage
                .checkAmountOfVisiblePhotos(10)
                .checkPreviousButtonIsVisibleButDisabled()
                .clickNext()
                .checkAmountOfVisiblePhotos(1)
                .checkNextButtonIsVisibleButDisabled()
                .clickPrevious()
                .checkAmountOfVisiblePhotos(10);
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = {
            @Photo(description = "1"),
            @Photo(description = "2"),
    }, friends = {
            @Friend(photos = @Photo(description = "3")),
            @Friend(photos = @Photo(description = "4")),
            @Friend(photos = @Photo(description = "5")),
            @Friend(photos = @Photo(description = "6")),
            @Friend(photos = @Photo(description = "7")),
            @Friend(photos = @Photo(description = "8")),
            @Friend(photos = @Photo(description = "9")),
            @Friend(photos = @Photo(description = "10")),
    }))
    @DisplayName("Пагинация фотографий в [With friends] не должна быть если фотографий 10")
    void paginationShouldNotBeVisibleIfOnlyTenPhotosInWithFriendsTest() {
        myTravelsPage
                .clickWithFriends()
                .checkAmountOfVisiblePhotos(10)
                .checkNoPagination();
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = {
            @Photo(description = "1"),
            @Photo(description = "2"),
    }, friends = {
            @Friend(photos = @Photo(description = "3")),
            @Friend(photos = @Photo(description = "4")),
            @Friend(photos = @Photo(description = "5")),
            @Friend(photos = @Photo(description = "6")),
            @Friend(photos = @Photo(description = "7")),
            @Friend(photos = @Photo(description = "8")),
            @Friend(photos = @Photo(description = "9")),
            @Friend(photos = @Photo(description = "10")),
            @Friend(photos = @Photo(description = "11")),
    }))
    @DisplayName("Пагинация фотографий в [With friends] должна работать если фотографий больше 10")
    void paginationShouldNotBeVisibleIfOnlyOnePhotoInWithFriendsTest() {
        myTravelsPage
                .clickWithFriends()
                .checkAmountOfVisiblePhotos(10)
                .checkPreviousButtonIsVisibleButDisabled()
                .clickNext()
                .checkAmountOfVisiblePhotos(1)
                .checkNextButtonIsVisibleButDisabled()
                .clickPrevious()
                .checkAmountOfVisiblePhotos(10);
    }
}
