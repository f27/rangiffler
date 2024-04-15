package guru.qa.rangiffler.api;

import guru.qa.grpc.rangiffler.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;


@Component
public class UserdataClient {

    @GrpcClient("userdataClient")
    private RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceBlockingStub;

    public GrpcUser getUser(Username request) {
        return rangifflerUserdataServiceBlockingStub.getUser(request);
    }

    public UsersResponse getPeople(UsersRequest request) {
        return rangifflerUserdataServiceBlockingStub.getPeople(request);
    }

    public UsersResponse getFriends(UsersRequest request) {
        return rangifflerUserdataServiceBlockingStub.getFriends(request);
    }

    public UsersResponse getOutcomeInvitations(UsersRequest request) {
        return rangifflerUserdataServiceBlockingStub.getOutcomeInvitations(request);
    }

    public UsersResponse getIncomeInvitations(UsersRequest request) {
        return rangifflerUserdataServiceBlockingStub.getIncomeInvitations(request);
    }

    public GrpcUser updateUser(GrpcUser request) {
        return rangifflerUserdataServiceBlockingStub.updateUser(request);
    }

    public FriendsIdsResponse getFriendsIds(Username request) {
        return rangifflerUserdataServiceBlockingStub.getFriendsIds(request);
    }

    public GrpcUser inviteFriend(FriendshipRequest request) {
        return rangifflerUserdataServiceBlockingStub.inviteFriend(request);
    }

    public GrpcUser acceptFriend(FriendshipRequest request) {
        return rangifflerUserdataServiceBlockingStub.acceptFriend(request);
    }

    public GrpcUser rejectFriend(FriendshipRequest request) {
        return rangifflerUserdataServiceBlockingStub.rejectFriend(request);
    }

    public GrpcUser deleteFriend(FriendshipRequest request) {
        return rangifflerUserdataServiceBlockingStub.deleteFriend(request);
    }
}
