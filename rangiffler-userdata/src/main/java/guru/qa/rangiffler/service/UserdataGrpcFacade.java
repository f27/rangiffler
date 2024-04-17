package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.grpc.*;
import guru.qa.rangiffler.entity.user.UserEntity;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.UUID;

import static guru.qa.rangiffler.service.Validation.validate;

@GrpcService
public class UserdataGrpcFacade extends RangifflerUserdataServiceGrpc.RangifflerUserdataServiceImplBase {

    private final UserdataService userdataService;

    @Autowired
    public UserdataGrpcFacade(UserdataService userdataService) {
        this.userdataService = userdataService;
    }

    @Override
    public void getUser(Username request, StreamObserver<GrpcUser> responseObserver) {
        try {
            validate(request);
            responseObserver.onNext(UserEntity.toGrpcMessage(
                    userdataService.getUser(request.getUsername()),
                    FriendStatus.NOT_FRIEND));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getPeople(UsersRequest request, StreamObserver<UsersResponse> responseObserver) {
        try {
            validate(request);
            String username = request.getUsername();
            UserEntity currentUser = userdataService.getUser(username);
            boolean isPageable = request.getSize() != 0;
            String searchQuery = request.getSearchQuery();

            UsersResponse response;
            if (isPageable) {
                Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
                Slice<UserEntity> usersSlice = userdataService.getPeople(username, pageable, searchQuery);
                List<GrpcUser> grpcUsersList = usersSlice
                        .map(friend -> UserEntity.toGrpcMessage(friend, currentUser.getStatus(friend)))
                        .toList();
                response = UsersResponse.newBuilder()
                        .addAllUsers(grpcUsersList)
                        .setHasNext(usersSlice.hasNext())
                        .build();
            } else {
                List<GrpcUser> grpcUsersList = userdataService.getPeople(username, searchQuery).stream()
                        .map(friend -> UserEntity.toGrpcMessage(friend, currentUser.getStatus(friend)))
                        .toList();
                response = UsersResponse.newBuilder()
                        .addAllUsers(grpcUsersList)
                        .setHasNext(false)
                        .build();
            }
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getFriends(UsersRequest request, StreamObserver<UsersResponse> responseObserver) {
        try {
            validate(request);
            String username = request.getUsername();
            boolean isPageable = request.getSize() != 0;
            String searchQuery = request.getSearchQuery();

            UsersResponse.Builder responseBuilder;
            if (isPageable) {
                Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
                Slice<UserEntity> friends = userdataService.getFriends(username, pageable, searchQuery);
                responseBuilder = UsersResponse.newBuilder();
                friends.forEach(userEntity -> {
                    GrpcUser friend = UserEntity.toGrpcMessage(userEntity, FriendStatus.FRIEND);
                    responseBuilder.addUsers(friend);
                });
                responseBuilder.setHasNext(friends.hasNext());
            } else {
                List<UserEntity> friends = userdataService.getFriends(username, searchQuery);
                responseBuilder = UsersResponse.newBuilder();
                friends.forEach(userEntity -> {
                    GrpcUser friend = UserEntity.toGrpcMessage(userEntity, FriendStatus.FRIEND);
                    responseBuilder.addUsers(friend);
                });
                responseBuilder.setHasNext(false);
            }
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getIncomeInvitations(UsersRequest request, StreamObserver<UsersResponse> responseObserver) {
        try {
            validate(request);
            String username = request.getUsername();
            boolean isPageable = request.getSize() != 0;
            String searchQuery = request.getSearchQuery();

            UsersResponse.Builder responseBuilder;
            if (isPageable) {
                Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
                Slice<UserEntity> users = userdataService.getIncomeInvitations(username, pageable, searchQuery);
                responseBuilder = UsersResponse.newBuilder();
                users.forEach(userEntity -> {
                    GrpcUser user = UserEntity.toGrpcMessage(userEntity, FriendStatus.INVITATION_RECEIVED);
                    responseBuilder.addUsers(user);
                });
                responseBuilder.setHasNext(users.hasNext());
            } else {
                List<UserEntity> users = userdataService.getIncomeInvitations(username, searchQuery);
                responseBuilder = UsersResponse.newBuilder();
                users.forEach(userEntity -> {
                    GrpcUser user = UserEntity.toGrpcMessage(userEntity, FriendStatus.INVITATION_RECEIVED);
                    responseBuilder.addUsers(user);
                });
                responseBuilder.setHasNext(false);
            }
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getOutcomeInvitations(UsersRequest request, StreamObserver<UsersResponse> responseObserver) {
        try {
            validate(request);
            String username = request.getUsername();
            boolean isPageable = request.getSize() != 0;
            String searchQuery = request.getSearchQuery();

            UsersResponse.Builder responseBuilder;
            if (isPageable) {
                Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
                Slice<UserEntity> users = userdataService.getOutcomeInvitations(username, pageable, searchQuery);
                responseBuilder = UsersResponse.newBuilder();
                users.forEach(userEntity -> {
                    GrpcUser user = UserEntity.toGrpcMessage(userEntity, FriendStatus.INVITATION_SENT);
                    responseBuilder.addUsers(user);
                });
                responseBuilder.setHasNext(users.hasNext());
            } else {
                List<UserEntity> users = userdataService.getOutcomeInvitations(username, searchQuery);
                responseBuilder = UsersResponse.newBuilder();
                users.forEach(userEntity -> {
                    GrpcUser user = UserEntity.toGrpcMessage(userEntity, FriendStatus.INVITATION_SENT);
                    responseBuilder.addUsers(user);
                });
                responseBuilder.setHasNext(false);
            }
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getFriendsIds(Username request, StreamObserver<FriendsIdsResponse> responseObserver) {
        try {
            validate(request);
            FriendsIdsResponse.Builder responseBuilder = FriendsIdsResponse.newBuilder();
            responseBuilder.addAllFriendsIds(
                    userdataService.getUser(request.getUsername()).getFriends().stream()
                            .map(user -> user.getId().toString())
                            .toList()
            );
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateUser(GrpcUser request, StreamObserver<GrpcUser> responseObserver) {
        try {
            validate(request);
            UserEntity user = userdataService.updateUser(
                    request.getUsername(),
                    request.getFirstname(),
                    request.getLastname(),
                    request.getAvatar(),
                    request.getCountryCode()
            );
            responseObserver.onNext(UserEntity.toGrpcMessage(user, FriendStatus.NOT_FRIEND));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void inviteFriend(FriendshipRequest request, StreamObserver<GrpcUser> responseObserver) {
        try {
            validate(request);
            responseObserver.onNext(UserEntity.toGrpcMessage(
                    userdataService.inviteFriend(request.getUsername(), UUID.fromString(request.getTargetUserId())),
                    FriendStatus.INVITATION_SENT));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void acceptFriend(FriendshipRequest request, StreamObserver<GrpcUser> responseObserver) {
        try {
            validate(request);
            responseObserver.onNext(UserEntity.toGrpcMessage(userdataService.acceptFriend(
                            request.getUsername(), UUID.fromString(request.getTargetUserId())),
                    FriendStatus.FRIEND));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void rejectFriend(FriendshipRequest request, StreamObserver<GrpcUser> responseObserver) {
        try {
            validate(request);
            responseObserver.onNext(UserEntity.toGrpcMessage(userdataService.rejectFriend(
                            request.getUsername(), UUID.fromString(request.getTargetUserId())),
                    FriendStatus.NOT_FRIEND));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void deleteFriend(FriendshipRequest request, StreamObserver<GrpcUser> responseObserver) {
        try {
            validate(request);
            responseObserver.onNext(UserEntity.toGrpcMessage(userdataService.deleteFriend(
                            request.getUsername(), UUID.fromString(request.getTargetUserId())),
                    FriendStatus.NOT_FRIEND));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void deleteUser(Username request, StreamObserver<Empty> responseObserver) {
        try {
            validate(request);
            userdataService.deleteUser(request.getUsername());
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }
}
