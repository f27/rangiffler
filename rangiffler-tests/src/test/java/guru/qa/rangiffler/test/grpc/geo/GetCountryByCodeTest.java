package guru.qa.rangiffler.test.grpc.geo;

import guru.qa.grpc.rangiffler.grpc.CountryCode;
import guru.qa.grpc.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.test.grpc.BaseGrpcTest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

@Feature("GEO")
@Story("GetCountryByCode")
@DisplayName("GetCountryByCode")
public class GetCountryByCodeTest extends BaseGrpcTest {

    @Test
    @DisplayName("Получаемая страна должна быть ожидаемой")
    void getCountryByCodeTest() {
        for (CountryEnum country : CountryEnum.values()) {
            step(country.toString(), () -> {
                CountryResponse response = geoGrpcBlockingStub.getCountryByCode(
                        CountryCode.newBuilder().setCode(country.getCode()).build());
                step("Проверить, что название страны совпадает с ожидаемой", () ->
                        Assertions.assertEquals(country.toString(), response.getName()));
                step("Проверить, что код страны совпадает с ожидаемым", () ->
                        Assertions.assertEquals(country.getCode(), response.getCode()));
                step("Проверить, что id страны не пустой", () ->
                        Assertions.assertFalse(response.getId().isEmpty()));
                step("Проверить, что флаг страны не пустой", () ->
                        Assertions.assertFalse(response.getFlag().isEmpty()));
            });
        }
    }

    @Test
    @DisplayName("Получение страны с несуществующим кодом должно возвращать NOT_FOUND")
    void getCountryByNotExistingCodeTest() {
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> geoGrpcBlockingStub.getCountryByCode(CountryCode.newBuilder().setCode("NOT_EXISTING_CODE").build()));
        Assertions.assertEquals(
                Status.NOT_FOUND.withDescription("Country not found").asRuntimeException().getMessage(),
                e.getMessage());
    }
}
