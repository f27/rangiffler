package guru.qa.rangiffler.test.gql.mutation.deletePhoto;

import guru.qa.rangiffler.jupiter.annotation.*;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.model.gql.GqlRequest;
import guru.qa.rangiffler.model.gql.response.GqlDeletePhoto;
import guru.qa.rangiffler.test.gql.BaseGqlTest;
import guru.qa.rangiffler.util.GqlVariablesUtil;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.qameta.allure.Allure.step;

@Feature("photo mutation")
@Story("DeletePhoto")
@DisplayName("DeletePhoto")
public class DeletePhotoTest extends BaseGqlTest {

    @Test
    @ApiLogin(user = @GenerateUser(photos = @Photo))
    @DisplayName("Удаление фотографии")
    void deletePhotoTest(@User UserModel user,
                         @Token String bearerToken,
                         @GqlRequestFile("gql/mutation/deletePhoto/deletePhoto.json") GqlRequest template) throws IOException {
        GqlRequest request = new GqlRequest(
                template.operationName(),
                GqlVariablesUtil.deletePhoto(user.photos().get(0).id()),
                template.query()
        );

        final GqlDeletePhoto gqlDeletePhoto = gatewayApiClient.deletePhotoMutation(bearerToken, request);

        step("Проверить ответ", () -> Assertions.assertTrue(gqlDeletePhoto.getData().isDeletePhoto()));
    }
}
