package guru.qa.rangiffler.api.grpc;

import guru.qa.grpc.rangiffler.grpc.*;
import io.qameta.allure.grpc.AllureGrpc;

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

    public UsersResponse getFriends(UsersRequest request) {
        return blockingStub.getFriends(request);
    }

    public UsersResponse getIncomeInvitations(UsersRequest request) {
        return blockingStub.getIncomeInvitations(request);
    }

    public UsersResponse getOutcomeInvitations(UsersRequest request) {
        return blockingStub.getOutcomeInvitations(request);
    }

    public FriendsIdsResponse getFriendsIds(Username request) {
        return blockingStub.getFriendsIds(request);
    }

    public GrpcUser updateUser(GrpcUser request) {
        return blockingStub.updateUser(request);
    }

    public GrpcUser inviteFriend(FriendshipRequest request) {
        return blockingStub.inviteFriend(request);
    }

    public GrpcUser acceptFriend(FriendshipRequest request) {
        return blockingStub.acceptFriend(request);
    }

    public GrpcUser rejectFriend(FriendshipRequest request) {
        return blockingStub.rejectFriend(request);
    }

    public GrpcUser deleteFriend(FriendshipRequest request) {
        return blockingStub.deleteFriend(request);
    }

    public void deleteUser(Username request) {
        blockingStub.deleteUser(request);
    }
}