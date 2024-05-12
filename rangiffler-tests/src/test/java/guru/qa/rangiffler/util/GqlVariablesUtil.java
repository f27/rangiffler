package guru.qa.rangiffler.util;

import guru.qa.rangiffler.model.CountryEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GqlVariablesUtil {
    public static Map<String, Object> updatePhoto(UUID photoId, String description, CountryEnum countryEnum) {
        Map<String, String> country = new HashMap<>();
        country.put("code", countryEnum.getCode());
        Map<String, Object> input = new HashMap<>();
        input.put("id", photoId);
        input.put("description", description);
        input.put("country", country);
        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);
        return variables;
    }
}
