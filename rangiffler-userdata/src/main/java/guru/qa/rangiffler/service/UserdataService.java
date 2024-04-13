package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.grpc.*;
import guru.qa.rangiffler.entity.friendship.FriendshipEntity;
import guru.qa.rangiffler.entity.friendship.FriendshipStatus;
import guru.qa.rangiffler.entity.user.UserEntity;
import guru.qa.rangiffler.repository.UserdataRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.UUID;

@GrpcService
public class UserdataService extends RangifflerUserdataServiceGrpc.RangifflerUserdataServiceImplBase {

    private final UserdataRepository userdataRepository;

    @Autowired
    public UserdataService(UserdataRepository userdataRepository) {
        this.userdataRepository = userdataRepository;
    }

    @Override
    public void getUser(Username request, StreamObserver<GrpcUser> responseObserver) {
        try {
            responseObserver.onNext(UserEntity.toGrpcMessage(
                    getCurrentUser(request.getUsername()),
                    FriendStatus.NOT_FRIEND));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getPeople(UsersRequest request, StreamObserver<UsersResponse> responseObserver) {
        try {
            String username = request.getUsername();
            UserEntity currentUser = getCurrentUser(username);
            boolean isPageable = request.getSize() != 0;

            UsersResponse response;
            if (isPageable) {
                Slice<UserEntity> usersSlice;
                if (request.getSearchQuery().isEmpty()) {
                    usersSlice = userdataRepository.findByUsernameNot(username,
                            PageRequest.of(request.getPage(),
                                    request.getSize()));
                } else {
                    usersSlice = userdataRepository.findByUsernameNotAndSearchQuery(username,
                            PageRequest.of(request.getPage(),
                                    request.getSize()),
                            request.getSearchQuery());
                }
                List<GrpcUser> usersWithStatus = usersSlice
                        .map(friend -> UserEntity.toGrpcMessage(friend, currentUser.getStatus(friend)))
                        .toList();
                response = UsersResponse.newBuilder()
                        .addAllUsers(usersWithStatus)
                        .setHasNext(usersSlice.hasNext())
                        .build();
            } else {
                List<UserEntity> usersList;
                if (request.getSearchQuery().isEmpty()) {
                    usersList = userdataRepository.findByUsernameNot(username);
                } else {
                    usersList = userdataRepository.findByUsernameNotAndSearchQuery(username, request.getSearchQuery());
                }
                List<GrpcUser> usersWithStatus = usersList.stream()
                        .map(friend -> UserEntity.toGrpcMessage(friend, currentUser.getStatus(friend)))
                        .toList();
                response = UsersResponse.newBuilder()
                        .addAllUsers(usersWithStatus)
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
            UserEntity currentUser = getCurrentUser(request.getUsername());
            boolean isPageable = request.getSize() != 0;
            String searchQuery = request.getSearchQuery();

            UsersResponse.Builder responseBuilder;
            if (isPageable) {
                Slice<UserEntity> users;
                if (searchQuery.isEmpty()) {
                    users = userdataRepository.findFriends(
                            currentUser,
                            PageRequest.of(request.getPage(), request.getSize())
                    );
                } else {
                    users = userdataRepository.findFriends(
                            currentUser,
                            PageRequest.of(request.getPage(), request.getSize()),
                            searchQuery
                    );
                }
                responseBuilder = UsersResponse.newBuilder();
                users.forEach(userEntity -> {
                    GrpcUser user = UserEntity.toGrpcMessage(userEntity, FriendStatus.FRIEND);
                    responseBuilder.addUsers(user);
                });
                responseBuilder.setHasNext(users.hasNext());
            } else {
                List<UserEntity> users;
                if (searchQuery.isEmpty()) {
                    users = userdataRepository.findFriends(
                            currentUser
                    );
                } else {
                    users = userdataRepository.findFriends(
                            currentUser,
                            searchQuery
                    );
                }
                responseBuilder = UsersResponse.newBuilder();
                users.forEach(userEntity -> {
                    GrpcUser user = UserEntity.toGrpcMessage(userEntity, FriendStatus.FRIEND);
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
    public void getIncomeInvitations(UsersRequest request, StreamObserver<UsersResponse> responseObserver) {
        try {
            UserEntity currentUser = getCurrentUser(request.getUsername());
            boolean isPageable = request.getSize() != 0;
            String searchQuery = request.getSearchQuery();

            UsersResponse.Builder responseBuilder;
            if (isPageable) {
                Slice<UserEntity> users;
                if (searchQuery.isEmpty()) {
                    users = userdataRepository.findIncomeInvitations(
                            currentUser,
                            PageRequest.of(request.getPage(), request.getSize())
                    );
                } else {
                    users = userdataRepository.findIncomeInvitations(
                            currentUser,
                            PageRequest.of(request.getPage(), request.getSize()),
                            searchQuery
                    );
                }
                responseBuilder = UsersResponse.newBuilder();
                users.forEach(userEntity -> {
                    GrpcUser user = UserEntity.toGrpcMessage(userEntity, FriendStatus.INVITATION_RECEIVED);
                    responseBuilder.addUsers(user);
                });
                responseBuilder.setHasNext(users.hasNext());
            } else {
                List<UserEntity> users;
                if (searchQuery.isEmpty()) {
                    users = userdataRepository.findIncomeInvitations(
                            currentUser
                    );
                } else {
                    users = userdataRepository.findIncomeInvitations(
                            currentUser,
                            searchQuery
                    );
                }
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
            UserEntity currentUser = getCurrentUser(request.getUsername());
            boolean isPageable = request.getSize() != 0;
            String searchQuery = request.getSearchQuery();

            UsersResponse.Builder responseBuilder;
            if (isPageable) {
                Slice<UserEntity> users;
                if (searchQuery.isEmpty()) {
                    users = userdataRepository.findOutcomeInvitations(
                            currentUser,
                            PageRequest.of(request.getPage(), request.getSize())
                    );
                } else {
                    users = userdataRepository.findOutcomeInvitations(
                            currentUser,
                            PageRequest.of(request.getPage(), request.getSize()),
                            searchQuery
                    );
                }
                responseBuilder = UsersResponse.newBuilder();
                users.forEach(userEntity -> {
                    GrpcUser user = UserEntity.toGrpcMessage(userEntity, FriendStatus.INVITATION_SENT);
                    responseBuilder.addUsers(user);
                });
                responseBuilder.setHasNext(users.hasNext());
            } else {
                List<UserEntity> users;
                if (searchQuery.isEmpty()) {
                    users = userdataRepository.findOutcomeInvitations(
                            currentUser
                    );
                } else {
                    users = userdataRepository.findOutcomeInvitations(
                            currentUser,
                            searchQuery
                    );
                }
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
            UserEntity currentUser = getCurrentUser(request.getUsername());

            FriendsIdsResponse.Builder responseBuilder = FriendsIdsResponse.newBuilder();
            responseBuilder.addAllFriendsIds(
                    currentUser.getFriends().stream()
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
    public void updateCurrentUser(GrpcUser request, StreamObserver<GrpcUser> responseObserver) {
        try {
            UserEntity currentUser = getCurrentUser(request.getUsername());
            currentUser.setFirstname(request.getFirstname());
            currentUser.setLastname(request.getLastname());
            currentUser.setAvatar(request.getAvatar().getBytes());
            currentUser.setCountryCode(request.getCountryCode());
            responseObserver.onNext(UserEntity.toGrpcMessage(userdataRepository.save(currentUser), FriendStatus.NOT_FRIEND));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void addFriend(FriendshipRequest request, StreamObserver<GrpcUser> responseObserver) {
        try {
            UserEntity currentUser = getCurrentUser(request.getUsername());
            UserEntity targetUser = getCorrectTargetUser(currentUser.getId(), UUID.fromString(request.getTargetUserId()));
            checkCanSendInvitation(currentUser, targetUser);
            FriendshipEntity targetUserFriendship = new FriendshipEntity();
            targetUserFriendship.setRequester(currentUser);
            targetUserFriendship.setAddressee(targetUser);
            targetUserFriendship.setStatus(FriendshipStatus.PENDING);
            targetUser.addIncomeInvitation(targetUserFriendship);
            responseObserver.onNext(UserEntity.toGrpcMessage(userdataRepository.save(targetUser), FriendStatus.INVITATION_SENT));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void acceptFriend(FriendshipRequest request, StreamObserver<GrpcUser> responseObserver) {
        try {
            UserEntity currentUser = getCurrentUser(request.getUsername());
            UserEntity targetUser = getCorrectTargetUser(currentUser.getId(), UUID.fromString(request.getTargetUserId()));
            FriendshipEntity incomeInvitation = getPendingIncomeInvitation(currentUser, targetUser);
            incomeInvitation.setStatus(FriendshipStatus.ACCEPTED);
            FriendshipEntity outcomeInvitation = new FriendshipEntity();
            outcomeInvitation.setRequester(currentUser);
            outcomeInvitation.setAddressee(targetUser);
            outcomeInvitation.setStatus(FriendshipStatus.ACCEPTED);
            currentUser.addOutcomeInvitation(outcomeInvitation);
            userdataRepository.save(currentUser);
            responseObserver.onNext(UserEntity.toGrpcMessage(targetUser, FriendStatus.FRIEND));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void rejectFriend(FriendshipRequest request, StreamObserver<GrpcUser> responseObserver) {
        try {
            UserEntity currentUser = getCurrentUser(request.getUsername());
            UserEntity targetUser = getCorrectTargetUser(currentUser.getId(), UUID.fromString(request.getTargetUserId()));
            FriendshipEntity incomeInvitation = getPendingIncomeInvitation(currentUser, targetUser);
            currentUser.removeIncomeInvitation(incomeInvitation);
            userdataRepository.save(currentUser);
            responseObserver.onNext(UserEntity.toGrpcMessage(targetUser, FriendStatus.NOT_FRIEND));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void deleteFriend(FriendshipRequest request, StreamObserver<GrpcUser> responseObserver) {
        try {
            UserEntity currentUser = getCurrentUser(request.getUsername());
            UserEntity targetUser = getCorrectTargetUser(currentUser.getId(), UUID.fromString(request.getTargetUserId()));
            FriendshipEntity incomeInvitation = currentUser.findIncomeInvitation(targetUser)
                    .orElseThrow(() -> new RuntimeException("Invitation not exist"));
            FriendshipEntity outcomeInvitation = currentUser.findOutcomeInvitation(targetUser)
                    .orElseThrow(() -> new RuntimeException("Invitation not exist"));
            currentUser.removeIncomeInvitation(incomeInvitation);
            currentUser.removeOutcomeInvitation(outcomeInvitation);
            userdataRepository.save(currentUser);
            responseObserver.onNext(UserEntity.toGrpcMessage(targetUser, FriendStatus.NOT_FRIEND));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Username request, StreamObserver<Empty> responseObserver) {
        userdataRepository.deleteByUsername(request.getUsername());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    private UserEntity getCurrentUser(String username) {
        UserEntity user = userdataRepository.getByUsername(username);
        if (user == null) {
            throw Status.NOT_FOUND.withDescription("User not found").asRuntimeException();
        }
        return user;
    }

    private void checkCanSendInvitation(UserEntity currentUser, UserEntity targetUser) {
        if (targetUser.findIncomeInvitation(currentUser).isPresent()) {
            throw Status.ALREADY_EXISTS.withDescription("Invitation already exist").asRuntimeException();
        }
        if (currentUser.findIncomeInvitation(targetUser).isPresent()) {
            throw Status.ALREADY_EXISTS.withDescription("Invitation already exist").asRuntimeException();
        }
    }

    private UserEntity getCorrectTargetUser(UUID currentUserId, UUID targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw Status.INVALID_ARGUMENT.withDescription("Target user should not be same").asRuntimeException();
        }
        return userdataRepository.findById(targetUserId)
                .orElseThrow(() -> Status.NOT_FOUND.withDescription("Target user not found").asRuntimeException());
    }

    private FriendshipEntity getPendingIncomeInvitation(UserEntity currentUser, UserEntity targetUser) {
        FriendshipEntity incomeInvitation = currentUser.findIncomeInvitation(targetUser)
                .orElseThrow(() -> Status.NOT_FOUND.withDescription("Invitation not exist").asRuntimeException());

        if (incomeInvitation.getStatus().equals(FriendshipStatus.ACCEPTED)) {
            throw Status.ALREADY_EXISTS.withDescription("Invitation already accepted").asRuntimeException();
        }
        return incomeInvitation;
    }
}
