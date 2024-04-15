package guru.qa.rangiffler.model.gql;

import java.util.Map;

public record GqlRequest(
        String operationName,
        Map<String, Object> variables,
        String query
) {
}
