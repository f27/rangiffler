package guru.qa.rangiffler.service;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;
import jakarta.annotation.Nonnull;

import java.util.List;

public class GqlValidation {

    public static void checkSubQueries(@Nonnull DataFetchingEnvironment env, int depth, @Nonnull String... queryKeys) {
        for (String queryKey : queryKeys) {
            List<SelectedField> selectors = env.getSelectionSet().getFieldsGroupedByResultKey().get(queryKey);
            if (selectors != null && selectors.size() > depth) {
                throw new RuntimeException("Can`t fetch over " + depth + " " + queryKey + " sub-queries");
            }
        }
    }
}
