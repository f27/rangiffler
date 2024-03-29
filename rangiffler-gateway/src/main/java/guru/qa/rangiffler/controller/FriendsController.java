package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.api.UserDataClient;
import guru.qa.rangiffler.model.friendship.FriendshipInput;
import guru.qa.rangiffler.model.user.UserModel;
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
                                @Argument @Valid FriendshipInput input) {
        String username = principal.getClaim("sub");
        return userDataClient.friendshipMutation(username, input);
    }
}
