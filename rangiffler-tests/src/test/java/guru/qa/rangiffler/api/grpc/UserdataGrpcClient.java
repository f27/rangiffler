package guru.qa.rangiffler.api.grpc;

import guru.qa.grpc.rangiffler.grpc.*;
import io.qameta.allure.grpc.AllureGrpc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class UserdataGrpcClient extends GrpcClient {

    private final RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub blockingStub;

    public UserdataGrpcClient() {
        super(GrpcChannelProvider.INSTANCE.channel(CFG.userdataGrpcAddress()));
        this.blockingStub = RangifflerUserdataServiceGrpc.newBlockingStub(channel).withInterceptors(new AllureGrpc());
    }

    public GrpcUser getUser(Username request) {
        return blockingStub.getUser(request);
    }

    public UsersResponse getPeople(UsersRequest request) {
        return blockingStub.getPeople(request);
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

    public GrpcUser updateUser(GrpcUser user) {
        return blockingStub.updateCurrentUser(user);
    }

    public GrpcUser inviteFriend(String fromUsername, UUID toId) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(fromUsername)
                .setTargetUserId(toId.toString())
                .build();
        return blockingStub.addFriend(request);
    }

    public GrpcUser acceptFriend(String fromUsername, UUID toId) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(fromUsername)
                .setTargetUserId(toId.toString())
                .build();
        return blockingStub.acceptFriend(request);
    }

    public GrpcUser rejectFriend(String fromUsername, UUID toId) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(fromUsername)
                .setTargetUserId(toId.toString())
                .build();
        return blockingStub.rejectFriend(request);
    }

    public GrpcUser deleteFriend(String fromUsername, UUID toId) {
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