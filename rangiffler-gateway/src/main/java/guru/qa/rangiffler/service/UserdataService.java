package guru.qa.rangiffler.service;

import guru.qa.grpc.rangiffler.grpc.*;
import guru.qa.rangiffler.api.UserdataClient;
import guru.qa.rangiffler.model.friendship.FriendshipInput;
import guru.qa.rangiffler.model.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserdataService {

    private final UserdataClient userdataClient;

    @Autowired
    public UserdataService(UserdataClient userdataClient) {
        this.userdataClient = userdataClient;
    }

    public UserModel getUser(String username) {
        Username request = Username.newBuilder()
                .setUsername(username)
                .build();
        GrpcUser response = userdataClient.getUser(request);
        return UserModel.fromGrpcMessage(response);
    }

    public Slice<UserModel> getPeople(String username, String searchQuery, int page, int size) {
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(username)
                .setSearchQuery(searchQuery == null ? "" : searchQuery)
                .setPage(page)
                .setSize(size)
                .build();
        UsersResponse response = userdataClient.getPeople(request);
        return new SliceImpl<>(
                response.getUsersList().stream().map(UserModel::fromGrpcMessage).toList(),
                PageRequest.of(page, size),
                response.getHasNext());
    }

    public UserModel updateUser(String username, String firstname, String lastname, String avatar, String countryCode) {
        GrpcUser request = GrpcUser.newBuilder()
                .setUsername(username)
                .setFirstname(firstname)
                .setLastname(lastname)
                .setAvatar(avatar)
                .setCountryCode(countryCode)
                .build();
        GrpcUser response = userdataClient.updateUser(request);
        return UserModel.fromGrpcMessage(response);
    }

    public List<UUID> getFriendsIds(String username) {
        Username request = Username.newBuilder().setUsername(username).build();
        FriendsIdsResponse response = userdataClient.getFriendsIds(request);
        return response.getFriendsIdsList().stream().map(UUID::fromString).toList();
    }

    public Slice<UserModel> getFriends(String username, String searchQuery, int page, int size) {
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(username)
                .setSearchQuery(searchQuery == null ? "" : searchQuery)
                .setPage(page)
                .setSize(size)
                .build();
        UsersResponse response = userdataClient.getFriends(request);
        return new SliceImpl<>(
                response.getUsersList().stream().map(UserModel::fromGrpcMessage).toList(),
                PageRequest.of(page, size),
                response.getHasNext());
    }

    public Slice<UserModel> getOutcomeInvitations(String username, String searchQuery, int page, int size) {
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(username)
                .setSearchQuery(searchQuery == null ? "" : searchQuery)
                .setPage(page)
                .setSize(size)
                .build();
        UsersResponse response = userdataClient.getOutcomeInvitations(request);
        return new SliceImpl<>(
                response.getUsersList().stream().map(UserModel::fromGrpcMessage).toList(),
                PageRequest.of(page, size),
                response.getHasNext());
    }

    public Slice<UserModel> getIncomeInvitations(String username, String searchQuery, int page, int size) {
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(username)
                .setSearchQuery(searchQuery == null ? "" : searchQuery)
                .setPage(page)
                .setSize(size)
                .build();
        UsersResponse response = userdataClient.getIncomeInvitations(request);
        return new SliceImpl<>(
                response.getUsersList().stream().map(UserModel::fromGrpcMessage).toList(),
                PageRequest.of(page, size),
                response.getHasNext());
    }

    public UserModel friendshipMutation(String username, FriendshipInput input) {
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(username)
                .setTargetUserId(input.user().toString())
                .build();
        switch (input.action()) {
            case ADD -> {
                return UserModel.fromGrpcMessage(userdataClient.inviteFriend(request));
            }
            case ACCEPT -> {
                return UserModel.fromGrpcMessage(userdataClient.acceptFriend(request));
            }
            case REJECT -> {
                return UserModel.fromGrpcMessage(userdataClient.rejectFriend(request));
            }
            case DELETE -> {
                return UserModel.fromGrpcMessage(userdataClient.deleteFriend(request));
            }
        }
        throw new RuntimeException("Action '" + input.action() + "' not implemented");
    }
}
