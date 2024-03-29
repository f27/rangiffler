package guru.qa.rangiffler.controller;

import graphql.schema.DataFetchingEnvironment;
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
        return userDataClient.currentUser(username);
    }

    @QueryMapping
    public Slice<UserModel> users(@AuthenticationPrincipal Jwt principal,
                                  @Argument int page,
                                  @Argument int size,
                                  @Argument @Nullable String searchQuery) {
        String username = principal.getClaim("sub");
        return userDataClient.getUsers(username, searchQuery, page, size);
    }

    @MutationMapping
    public UserModel user(@AuthenticationPrincipal Jwt principal,
                          @Argument @Valid UpdateUserInput input) {
        String username = principal.getClaim("sub");
        return userDataClient.updateCurrentUser(username, input);
    }
}
