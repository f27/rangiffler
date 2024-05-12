package guru.qa.rangiffler.test.gql.mutation.photo;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.GqlRequestFile;
import guru.qa.rangiffler.jupiter.annotation.Token;
import guru.qa.rangiffler.model.gql.GqlRequest;
import guru.qa.rangiffler.model.gql.response.GqlPhoto;
import guru.qa.rangiffler.test.gql.BaseGqlTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.qameta.allure.Allure.step;

@Feature("photo mutation")
@Story("CreatePhoto")
@DisplayName("CreatePhoto")
public class CreatePhotoTest extends BaseGqlTest {

    @Test
    @ApiLogin
    @DisplayName("Создание фотографии")
    void createPhotoTest(@Token String bearerToken,
                         @GqlRequestFile("gql/mutation/photo/createPhoto.json") GqlRequest request) throws IOException {
        final GqlPhoto gqlPhoto = gatewayApiClient.photoMutation(bearerToken, request);

        step("Проверить ответ", () -> {
            step("Проверить, что вернулся id фотографии", () ->
                    Assertions.assertNotNull(gqlPhoto.getData().getPhoto().getId()));
            step("Проверить, что вернулось описание фотографии", () ->
                    Assertions.assertEquals("Bober", gqlPhoto.getData().getPhoto().getDescription()));
            step("Проверить, что вернулся код страны фотографии", () ->
                    Assertions.assertEquals("sb", gqlPhoto.getData().getPhoto().getCountry().getCode()));
        });
    }
}
