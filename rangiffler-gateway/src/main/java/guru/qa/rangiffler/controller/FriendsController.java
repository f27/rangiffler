package guru.qa.rangiffler.controller;

import graphql.schema.DataFetchingEnvironment;
import guru.qa.rangiffler.model.friendship.FriendshipInput;
import guru.qa.rangiffler.model.user.UserModel;
import guru.qa.rangiffler.service.GqlValidationService;
import guru.qa.rangiffler.service.UserdataService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

    private final UserdataService userdataService;
    private final GqlValidationService gqlValidationService;

    @Autowired
    public FriendsController(UserdataService userdataService, GqlValidationService gqlValidationService) {
        this.userdataService = userdataService;
        this.gqlValidationService = gqlValidationService;
    }

    @SchemaMapping(typeName = "User", field = "friends")
    public Slice<UserModel> friends(UserModel user,
                                    @Argument @NotNull int page,
                                    @Argument @NotNull int size,
                                    @Argument @Nullable String searchQuery) {
        return userdataService.getFriends(user.username(), searchQuery, page, size);
    }

    @SchemaMapping(typeName = "User", field = "outcomeInvitations")
    public Slice<UserModel> incomeInvitations(UserModel user,
                                              @Argument @NotNull int page,
                                              @Argument @NotNull int size,
                                              @Argument @Nullable String searchQuery) {
        return userdataService.getOutcomeInvitations(user.username(), searchQuery, page, size);
    }

    @SchemaMapping(typeName = "User", field = "incomeInvitations")
    public Slice<UserModel> outcomeInvitations(UserModel user,
                                               @Argument @NotNull int page,
                                               @Argument @NotNull int size,
                                               @Argument @Nullable String searchQuery) {
        return userdataService.getIncomeInvitations(user.username(), searchQuery, page, size);
    }

    @MutationMapping
    public UserModel friendship(@AuthenticationPrincipal Jwt principal,
                                @Argument @Valid FriendshipInput input,
                                @Nonnull DataFetchingEnvironment env) {
        String username = principal.getClaim("sub");
        gqlValidationService.checkSubQueries(env, 1, "friends", "incomeInvitations", "outcomeInvitations");
        return userdataService.friendshipMutation(username, input);
    }
}
