package guru.qa.rangiffler.test.grpc.geo;

import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.CountryModel;
import guru.qa.rangiffler.test.grpc.BaseGrpcTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;

@Feature("GEO")
@Story("GEO")
@DisplayName("GEO")
public class GeoGrpcTest extends BaseGrpcTest {

    @Test
    @DisplayName("Получаемая страна должна быть ожидаемой")
    void getCountryByCodeTest() {
        for (CountryEnum country : CountryEnum.values()) {
            step(country.toString(), () -> {
                CountryModel countryFromGrpc = GEO_GRPC_CLIENT.getCountryByCode(country.getCode());
                step("Проверить, что название страны совпадает с ожидаемой", () ->
                        Assertions.assertEquals(country.toString(), countryFromGrpc.name()));
                step("Проверить, что код страны совпадает с ожидаемым", () ->
                        Assertions.assertEquals(country.getCode(), countryFromGrpc.code()));
                step("Проверить, что id страны не пустой", () ->
                        Assertions.assertFalse(countryFromGrpc.id().toString().isEmpty()));
                step("Проверить, что флаг страны не пустой", () ->
                        Assertions.assertFalse(countryFromGrpc.name().isEmpty()));
            });
        }
    }

    @Test
    @DisplayName("Когда получаем список всех стран он не должен быть пустым")
    void getAllCountriesTest() {
        List<CountryModel> countriesFromGrpc = GEO_GRPC_CLIENT.getAllCountries();
        step("Проверить, что список не пуст", () -> Assertions.assertFalse(countriesFromGrpc.isEmpty()));
    }
}
