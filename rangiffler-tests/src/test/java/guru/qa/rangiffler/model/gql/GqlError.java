package guru.qa.rangiffler.model.gql;

import java.util.List;
import java.util.Map;

public record GqlError(
        String message,
        List<Map<String, Integer>> locations,
        List<String> path,
        Map<String, String> extensions
) {
}
