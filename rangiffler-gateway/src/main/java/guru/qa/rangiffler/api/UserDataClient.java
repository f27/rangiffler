package guru.qa.rangiffler.api;

import guru.qa.grpc.rangiffler.grpc.*;
import guru.qa.rangiffler.model.friendship.FriendshipAction;
import guru.qa.rangiffler.model.friendship.FriendshipInput;
import guru.qa.rangiffler.model.user.UpdateUserInput;
import guru.qa.rangiffler.model.user.UserModel;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;


@Component
public class UserDataClient {

    @GrpcClient("userdataClient")
    private RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceBlockingStub;

    public @Nonnull UserModel currentUser(@Nonnull String username) {
        Username request = Username.newBuilder()
                .setUsername(username)
                .build();

        User response = rangifflerUserdataServiceBlockingStub.getUser(request);
        return UserModel.fromGrpcMessage(response);
    }

    public @Nonnull Slice<UserModel> getUsers(@Nonnull String username,
                                              @Nullable String searchQuery,
                                              int page,
                                              int size) {
        UsersRequest request = createUsersRequestFromParams(username, searchQuery, page, size);
        return usersReasponseToUsersSlice(rangifflerUserdataServiceBlockingStub.getUsers(request), page, size);
    }

    public @Nonnull Slice<UserModel> getFriends(@Nonnull String username,
                                                @Nullable String searchQuery,
                                                int page,
                                                int size) {
        UsersRequest request = createUsersRequestFromParams(username, searchQuery, page, size);
        return usersReasponseToUsersSlice(rangifflerUserdataServiceBlockingStub.getFriends(request), page, size);
    }

    public @Nonnull Slice<UserModel> getOutcomeInvitations(@Nonnull String username,
                                                           @Nullable String searchQuery,
                                                           int page,
                                                           int size) {
        UsersRequest request = createUsersRequestFromParams(username, searchQuery, page, size);
        return usersReasponseToUsersSlice(rangifflerUserdataServiceBlockingStub.getOutcomeInvitations(request), page, size);
    }

    public @Nonnull Slice<UserModel> getIncomeInvitations(@Nonnull String username,
                                                          @Nullable String searchQuery,
                                                          int page,
                                                          int size) {
        UsersRequest request = createUsersRequestFromParams(username, searchQuery, page, size);
        return usersReasponseToUsersSlice(rangifflerUserdataServiceBlockingStub.getIncomeInvitations(request), page, size);
    }

    public @Nonnull UserModel updateCurrentUser(@Nonnull String username,
                                                @Nonnull UpdateUserInput user) {
        User request = User.newBuilder()
                .setUsername(username)
                .setFirstname(user.firstname())
                .setLastname(user.surname())
                .setAvatar(user.avatar())
                .setCountryCode(user.location().code())
                .build();
        return UserModel.fromGrpcMessage(rangifflerUserdataServiceBlockingStub.updateCurrentUser(request));
    }

    public @Nonnull UserModel friendshipMutation(@Nonnull String username,
                                                 @Nonnull FriendshipInput input) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(username)
                .setTargetUserId(input.user().toString())
                .build();

        if (input.action().equals(FriendshipAction.ADD)) {
            return UserModel.fromGrpcMessage(rangifflerUserdataServiceBlockingStub.addFriend(request));
        } else if (input.action().equals(FriendshipAction.ACCEPT)) {
            return UserModel.fromGrpcMessage(rangifflerUserdataServiceBlockingStub.acceptFriend(request));
        } else if (input.action().equals(FriendshipAction.REJECT)) {
            return UserModel.fromGrpcMessage(rangifflerUserdataServiceBlockingStub.rejectFriend(request));
        } else if (input.action().equals(FriendshipAction.DELETE)) {
            return UserModel.fromGrpcMessage(rangifflerUserdataServiceBlockingStub.deleteFriend(request));
        }
        throw new RuntimeException("Action '" + input.action() + "' not implemented");
    }

    private UsersRequest createUsersRequestFromParams(@Nonnull String username,
                                                      @Nullable String searchQuery,
                                                      int page,
                                                      int size) {
        UsersRequest.Builder requestBuilder = UsersRequest.newBuilder()
                .setUsername(username);
        if (searchQuery != null) {
            requestBuilder.setSearchQuery(searchQuery);
        }
        return requestBuilder
                .setPage(page)
                .setSize(size)
                .build();
    }

    private @Nonnull Slice<UserModel> usersReasponseToUsersSlice(@Nonnull UsersResponse response, int page, int size) {
        return new SliceImpl<>(
                response.getUsersList().stream().map(UserModel::fromGrpcMessage).toList(),
                PageRequest.of(page, size),
                response.getHasNext());
    }
}
