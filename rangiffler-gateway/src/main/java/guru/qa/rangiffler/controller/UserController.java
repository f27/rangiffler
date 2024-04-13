package guru.qa.rangiffler.controller;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;
import guru.qa.rangiffler.api.UserDataClient;
import guru.qa.rangiffler.model.user.UpdateUserInput;
import guru.qa.rangiffler.model.user.UserModel;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController {

    private final UserDataClient userDataClient;

    @Autowired
    public UserController(UserDataClient userDataClient) {
        this.userDataClient = userDataClient;
    }

    @QueryMapping
    public UserModel user(@AuthenticationPrincipal Jwt principal,
                          @Nonnull DataFetchingEnvironment env) {
        String username = principal.getClaim("sub");
        checkSubQueries(env, 1, "friends", "incomeInvitations", "outcomeInvitations");
        return userDataClient.currentUser(username);
    }

    @QueryMapping
    public Slice<UserModel> users(@AuthenticationPrincipal Jwt principal,
                                  @Argument int page,
                                  @Argument int size,
                                  @Argument @Nullable String searchQuery,
                                  @Nonnull DataFetchingEnvironment env) {
        String username = principal.getClaim("sub");
        checkSubQueries(env, 1, "friends", "incomeInvitations", "outcomeInvitations");
        return userDataClient.getPeople(username, searchQuery, page, size);
    }

    @MutationMapping
    public UserModel user(@AuthenticationPrincipal Jwt principal,
                          @Argument @Valid UpdateUserInput input,
                          @Nonnull DataFetchingEnvironment env) {
        String username = principal.getClaim("sub");
        checkSubQueries(env, 1, "friends", "incomeInvitations", "outcomeInvitations");
        return userDataClient.updateCurrentUser(username, input);
    }

    private void checkSubQueries(@Nonnull DataFetchingEnvironment env, int depth, @Nonnull String... queryKeys) {
        for (String queryKey : queryKeys) {
            List<SelectedField> selectors = env.getSelectionSet().getFieldsGroupedByResultKey().get(queryKey);
            if (selectors != null && selectors.size() > depth) {
                throw new RuntimeException("Can`t fetch over " + depth + " " + queryKey + " sub-queries");
            }
        }
    }
}
