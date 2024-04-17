package guru.qa.rangiffler.controller;

import graphql.schema.DataFetchingEnvironment;
import guru.qa.rangiffler.model.user.UpdateUserInput;
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
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    private final UserdataService userdataService;
    private final GqlValidationService gqlValidationService;

    @Autowired
    public UserController(UserdataService userdataService, GqlValidationService gqlValidationService) {
        this.userdataService = userdataService;
        this.gqlValidationService = gqlValidationService;
    }

    @QueryMapping
    public UserModel user(@AuthenticationPrincipal Jwt principal,
                          @Nonnull DataFetchingEnvironment env) {
        String username = principal.getClaim("sub");
        gqlValidationService.checkSubQueries(env, 1, "friends", "incomeInvitations", "outcomeInvitations");
        return userdataService.getUser(username);
    }

    @QueryMapping
    public Slice<UserModel> users(@AuthenticationPrincipal Jwt principal,
                                  @Argument @NotNull int page,
                                  @Argument @NotNull int size,
                                  @Argument @Nullable String searchQuery,
                                  @Nonnull DataFetchingEnvironment env) {
        String username = principal.getClaim("sub");
        gqlValidationService.checkSubQueries(env, 1, "friends", "incomeInvitations", "outcomeInvitations");
        return userdataService.getPeople(username, searchQuery, page, size);
    }

    @MutationMapping
    public UserModel user(@AuthenticationPrincipal Jwt principal,
                          @Argument @Valid UpdateUserInput input,
                          @Nonnull DataFetchingEnvironment env) {
        String username = principal.getClaim("sub");
        gqlValidationService.checkSubQueries(env, 1, "friends", "incomeInvitations", "outcomeInvitations");
        return userdataService.updateUser(username,
                input.firstname(), input.surname(), input.avatar(), input.location().code());
    }
}
