package guru.qa.rangiffler.test.gql.mutation.photo;

import guru.qa.rangiffler.jupiter.annotation.*;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.model.gql.GqlRequest;
import guru.qa.rangiffler.model.gql.response.GqlPhoto;
import guru.qa.rangiffler.test.gql.BaseGqlTest;
import guru.qa.rangiffler.util.GqlVariablesUtil;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static io.qameta.allure.Allure.step;

@Feature("photo mutation")
@Story("UpdatePhoto")
@DisplayName("UpdatePhoto")
public class UpdatePhotoTest extends BaseGqlTest {

    @Test
    @ApiLogin(user = @GenerateUser(photos = @Photo))
    @DisplayName("Обновить фотографию")
    void updatePhotoTest(@User UserModel user,
                         @Token String bearerToken,
                         @GqlRequestFile("gql/mutation/photo/updatePhoto.json") GqlRequest template) throws IOException {
        String newDescription = "Хорошее животное";
        CountryEnum countryEnum = CountryEnum.POLAND;
        Map<String, Object> variables = GqlVariablesUtil.updatePhoto(user.photos().get(0).id(), newDescription, countryEnum);
        GqlRequest request = new GqlRequest(
                template.operationName(),
                variables,
                template.query()
        );
        final GqlPhoto gqlPhoto = gatewayApiClient.photoMutation(bearerToken, request);

        step("Проверить ответ", () -> {
            step("Проверить, что вернулся тот же id фотографии", () ->
                    Assertions.assertEquals(user.photos().get(0).id(), gqlPhoto.getData().getPhoto().getId()));
            step("Проверить, что вернулось новое описание фотографии", () ->
                    Assertions.assertEquals(newDescription, gqlPhoto.getData().getPhoto().getDescription()));
            step("Проверить, что вернулся код страны фотографии", () ->
                    Assertions.assertEquals(countryEnum.getCode(), gqlPhoto.getData().getPhoto().getCountry().getCode()));
        });
    }
}
