package guru.qa.rangiffler.api.grpc;

import guru.qa.grpc.rangiffler.grpc.*;
import io.qameta.allure.grpc.AllureGrpc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class UserdataGrpcClient extends GrpcClient {

    private final RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub blockingStub;

    public UserdataGrpcClient() {
        super(CFG.userdataGrpcHost(), CFG.userdataGrpcPort());
        this.blockingStub = RangifflerUserdataServiceGrpc.newBlockingStub(channel).withInterceptors(new AllureGrpc());
    }

    public User getUser(String username) {
        Username request = Username.newBuilder()
                .setUsername(username)
                .build();
        return blockingStub.getUser(request);
    }

    public UsersResponse getUsers(String username,
                                  String searchQuery,
                                  int page,
                                  int size) {
        UsersRequest request = createUsersRequestFromParams(username, searchQuery, page, size);
        return blockingStub.getUsers(request);
    }

    public UsersResponse getFriends(String username,
                                    String searchQuery,
                                    int page,
                                    int size) {
        UsersRequest request = createUsersRequestFromParams(username, searchQuery, page, size);
        return blockingStub.getFriends(request);
    }

    public UsersResponse getIncomeInvitations(String username,
                                              String searchQuery,
                                              int page,
                                              int size) {
        UsersRequest request = createUsersRequestFromParams(username, searchQuery, page, size);
        return blockingStub.getIncomeInvitations(request);
    }

    public UsersResponse getOutcomeInvitations(String username,
                                               String searchQuery,
                                               int page,
                                               int size) {
        UsersRequest request = createUsersRequestFromParams(username, searchQuery, page, size);
        return blockingStub.getOutcomeInvitations(request);
    }

    public FriendsIdsResponse getFriendsIds(String username) {
        Username request = Username.newBuilder()
                .setUsername(username)
                .build();
        return blockingStub.getFriendsIds(request);
    }

    public User updateUser(User user) {
        return blockingStub.updateCurrentUser(user);
    }

    public User inviteFriend(String fromUsername, UUID toId) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(fromUsername)
                .setTargetUserId(toId.toString())
                .build();
        return blockingStub.addFriend(request);
    }

    public User acceptFriend(String fromUsername, UUID toId) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(fromUsername)
                .setTargetUserId(toId.toString())
                .build();
        return blockingStub.acceptFriend(request);
    }

    public User rejectFriend(String fromUsername, UUID toId) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(fromUsername)
                .setTargetUserId(toId.toString())
                .build();
        return blockingStub.rejectFriend(request);
    }

    public User deleteFriend(String fromUsername, UUID toId) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(fromUsername)
                .setTargetUserId(toId.toString())
                .build();
        return blockingStub.rejectFriend(request);
    }

    public void deleteUser(String username) {
        Username request = Username.newBuilder()
                .setUsername(username)
                .build();
        blockingStub.deleteUser(request);
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
}