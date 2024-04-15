package guru.qa.rangiffler.test.grpc.geo;

import guru.qa.grpc.rangiffler.grpc.AllCountriesResponse;
import guru.qa.rangiffler.test.grpc.BaseGrpcTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

@Feature("GEO")
@Story("GetAllCountries")
@DisplayName("GetAllCountries")
public class GetAllCountriesTest extends BaseGrpcTest {

    @Test
    @DisplayName("Когда получаем список всех стран он не должен быть пустым")
    void getAllCountriesTest() {
        AllCountriesResponse response = geoGrpcClient.getAllCountries();
        step("Проверить, что список не пуст",
                () -> Assertions.assertNotEquals(0, response.getAllCountriesCount()));
    }
}
