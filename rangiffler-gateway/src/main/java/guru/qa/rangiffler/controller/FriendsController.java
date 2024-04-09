package guru.qa.rangiffler.controller;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;
import guru.qa.rangiffler.api.UserDataClient;
import guru.qa.rangiffler.model.friendship.FriendshipInput;
import guru.qa.rangiffler.model.user.UserModel;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class FriendsController {

    private final UserDataClient userDataClient;

    @Autowired
    public FriendsController(UserDataClient userDataClient) {
        this.userDataClient = userDataClient;
    }

    @SchemaMapping(typeName = "User", field = "friends")
    public Slice<UserModel> friends(UserModel user,
                                    @Argument int page,
                                    @Argument int size,
                                    @Argument @Nullable String searchQuery) {

        return userDataClient.getFriends(user.username(), searchQuery, page, size);
    }

    @SchemaMapping(typeName = "User", field = "outcomeInvitations")
    public Slice<UserModel> incomeInvitations(UserModel user,
                                              @Argument int page,
                                              @Argument int size,
                                              @Argument @Nullable String searchQuery) {
        return userDataClient.getOutcomeInvitations(user.username(), searchQuery, page, size);
    }

    @SchemaMapping(typeName = "User", field = "incomeInvitations")
    public Slice<UserModel> outcomeInvitations(UserModel user,
                                               @Argument int page,
                                               @Argument int size,
                                               @Argument @Nullable String searchQuery) {
        return userDataClient.getIncomeInvitations(user.username(), searchQuery, page, size);
    }

    @MutationMapping
    public UserModel friendship(@AuthenticationPrincipal Jwt principal,
                                @Argument @Valid FriendshipInput input,
                                @Nonnull DataFetchingEnvironment env) {
        String username = principal.getClaim("sub");
        checkSubQueries(env, 1, "friends", "incomeInvitations", "outcomeInvitations");
        return userDataClient.friendshipMutation(username, input);
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
