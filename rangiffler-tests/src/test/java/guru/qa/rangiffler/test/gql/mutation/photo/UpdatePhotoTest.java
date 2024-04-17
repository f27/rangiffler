package guru.qa.rangiffler.test.gql.mutation.photo;

import guru.qa.rangiffler.db.entity.photo.PhotoEntity;
import guru.qa.rangiffler.db.repository.PhotoRepository;
import guru.qa.rangiffler.db.repository.hibernate.PhotoRepositoryHibernate;
import guru.qa.rangiffler.jupiter.annotation.*;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.model.gql.GqlRequest;
import guru.qa.rangiffler.model.gql.response.GqlPhoto;
import guru.qa.rangiffler.test.gql.BaseGqlTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.qameta.allure.Allure.step;

@Feature("photo mutation")
@Story("UpdatePhoto")
@DisplayName("UpdatePhoto")
public class UpdatePhotoTest extends BaseGqlTest {
    private final PhotoRepository photoRepository = new PhotoRepositoryHibernate();

    @Test
    @ApiLogin(user = @GenerateUser(photos = @Photo))
    @DisplayName("Обновить фотографию")
    void updatePhotoTest(@User UserModel user,
                         @Token String bearerToken,
                         @GqlRequestFile("gql/mutation/photo/updatePhoto.json") GqlRequest template) throws IOException {
        String newDescription = "Хорошее животное";
        String newCountryCode = "pl";
        Map<String, String> country = new HashMap<>();
        country.put("code", newCountryCode);
        Map<String, Object> input = new HashMap<>();
        input.put("id", user.photos().get(0).id());
        input.put("description", newDescription);
        input.put("country", country);
        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);
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
                    Assertions.assertEquals(newCountryCode, gqlPhoto.getData().getPhoto().getCountry().getCode()));
        });
        step("Проверить запись в БД", () -> {
            PhotoEntity photoEntity = photoRepository.findByUserId(user.id()).get(0);
            step("Проверить id фотографии", () ->
                    Assertions.assertEquals(user.photos().get(0).id(), photoEntity.getId()));
            step("Проверить описание фотографии", () ->
                    Assertions.assertEquals(newDescription, photoEntity.getDescription()));
            step("Проверить код страны фотографии", () ->
                    Assertions.assertEquals(newCountryCode, photoEntity.getCountryCode()));
        });
    }
}
