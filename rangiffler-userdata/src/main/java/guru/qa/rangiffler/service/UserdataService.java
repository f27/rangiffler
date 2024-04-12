package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.grpc.*;
import guru.qa.rangiffler.entity.friendship.FriendshipEntity;
import guru.qa.rangiffler.entity.friendship.FriendshipStatus;
import guru.qa.rangiffler.entity.user.UserEntity;
import guru.qa.rangiffler.repository.UserdataRepository;
import io.grpc.Status;
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
    public void getUser(Username request, StreamObserver<User> responseObserver) {
        try {
            responseObserver.onNext(UserEntity.toGrpcMessage(
                    userdataRepository.getByUsername(request.getUsername()),
                    FriendStatus.NOT_FRIEND));
            responseObserver.onCompleted();
        } catch (Throwable t) {
            responseObserver.onError(Status.NOT_FOUND.withDescription("User not found").asRuntimeException());
        }
    }

    @Override
    public void getUsers(UsersRequest request, StreamObserver<UsersResponse> responseObserver) {
        String username = request.getUsername();

        UserEntity currentUser = userdataRepository.getByUsername(username);

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

        List<User> usersWithStatus = usersSlice
                .map(friend -> UserEntity.toGrpcMessage(friend, currentUser.getStatus(friend)))
                .toList();

        UsersResponse response = UsersResponse.newBuilder()
                .addAllUsers(usersWithStatus)
                .setHasNext(usersSlice.hasNext())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getFriends(UsersRequest request, StreamObserver<UsersResponse> responseObserver) {
        UserEntity requester = userdataRepository.getByUsername(request.getUsername());
        boolean isPageable = request.getSize() != 0;
        String searchQuery = request.getSearchQuery();

        Slice<UserEntity> users;
        if (isPageable) {
            if (searchQuery.isEmpty()) {
                users = userdataRepository.findFriends(
                        requester,
                        PageRequest.of(request.getPage(), request.getSize())
                );
            } else {
                users = userdataRepository.findFriends(
                        requester,
                        PageRequest.of(request.getPage(), request.getSize()),
                        searchQuery
                );
            }
        } else {
            if (searchQuery.isEmpty()) {
                users = userdataRepository.findFriends(
                        requester
                );
            } else {
                users = userdataRepository.findFriends(
                        requester,
                        searchQuery
                );
            }
        }

        UsersResponse.Builder responseBuilder = UsersResponse.newBuilder();
        users.forEach(userEntity -> {
            User user = UserEntity.toGrpcMessage(userEntity, FriendStatus.FRIEND);
            responseBuilder.addUsers(user);
        });
        responseBuilder.setHasNext(users.hasNext());

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getIncomeInvitations(UsersRequest request, StreamObserver<UsersResponse> responseObserver) {
        UserEntity requester = userdataRepository.getByUsername(request.getUsername());
        boolean isPageable = request.getSize() != 0;
        String searchQuery = request.getSearchQuery();

        Slice<UserEntity> users;
        if (isPageable) {
            if (searchQuery.isEmpty()) {
                users = userdataRepository.findIncomeInvitations(
                        requester,
                        PageRequest.of(request.getPage(), request.getSize())
                );
            } else {
                users = userdataRepository.findIncomeInvitations(
                        requester,
                        PageRequest.of(request.getPage(), request.getSize()),
                        searchQuery
                );
            }
        } else {
            if (searchQuery.isEmpty()) {
                users = userdataRepository.findIncomeInvitations(
                        requester
                );
            } else {
                users = userdataRepository.findIncomeInvitations(
                        requester,
                        searchQuery
                );
            }
        }

        UsersResponse.Builder responseBuilder = UsersResponse.newBuilder();
        users.forEach(userEntity -> {
            User user = UserEntity.toGrpcMessage(userEntity, FriendStatus.INVITATION_RECEIVED);
            responseBuilder.addUsers(user);
        });
        responseBuilder.setHasNext(users.hasNext());

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getOutcomeInvitations(UsersRequest request, StreamObserver<UsersResponse> responseObserver) {
        UserEntity requester = userdataRepository.getByUsername(request.getUsername());
        boolean isPageable = request.getSize() != 0;
        String searchQuery = request.getSearchQuery();

        Slice<UserEntity> users;
        if (isPageable) {
            if (searchQuery.isEmpty()) {
                users = userdataRepository.findOutcomeInvitations(
                        requester,
                        PageRequest.of(request.getPage(), request.getSize())
                );
            } else {
                users = userdataRepository.findOutcomeInvitations(
                        requester,
                        PageRequest.of(request.getPage(), request.getSize()),
                        searchQuery
                );
            }
        } else {
            if (searchQuery.isEmpty()) {
                users = userdataRepository.findOutcomeInvitations(
                        requester
                );
            } else {
                users = userdataRepository.findOutcomeInvitations(
                        requester,
                        searchQuery
                );
            }
        }

        UsersResponse.Builder responseBuilder = UsersResponse.newBuilder();
        users.forEach(userEntity -> {
            User user = UserEntity.toGrpcMessage(userEntity, FriendStatus.INVITATION_SENT);
            responseBuilder.addUsers(user);
        });
        responseBuilder.setHasNext(users.hasNext());

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getFriendsIds(Username request, StreamObserver<FriendsIdsResponse> responseObserver) {
        UserEntity currentUser = userdataRepository.getByUsername(request.getUsername());
        FriendsIdsResponse.Builder responseBuilder = FriendsIdsResponse.newBuilder();
        responseBuilder.addAllFriendsIds(
                currentUser.getFriends().stream()
                        .map(user -> user.getId().toString())
                        .toList()
        );
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateCurrentUser(User request, StreamObserver<User> responseObserver) {
        UserEntity currentUser = userdataRepository.getByUsername(request.getUsername());
        currentUser.setFirstname(request.getFirstname());
        currentUser.setLastname(request.getLastname());
        currentUser.setAvatar(request.getAvatar().getBytes());
        currentUser.setCountryCode(request.getCountryCode());
        responseObserver.onNext(UserEntity.toGrpcMessage(userdataRepository.save(currentUser), FriendStatus.NOT_FRIEND));
        responseObserver.onCompleted();
    }

    @Override
    public void addFriend(FriendshipRequest request, StreamObserver<User> responseObserver) {
        UserEntity currentUser = userdataRepository.getByUsername(request.getUsername());
        UserEntity targetUser = getCorrectTargetUser(currentUser.getId(), UUID.fromString(request.getTargetUserId()));
        checkCanSendInvitation(currentUser, targetUser);

        FriendshipEntity targetUserFriendship = new FriendshipEntity();
        targetUserFriendship.setRequester(currentUser);
        targetUserFriendship.setAddressee(targetUser);
        targetUserFriendship.setStatus(FriendshipStatus.PENDING);

        targetUser.addIncomeInvitation(targetUserFriendship);

        responseObserver.onNext(UserEntity.toGrpcMessage(userdataRepository.save(targetUser), FriendStatus.INVITATION_SENT));
        responseObserver.onCompleted();
    }

    @Override
    public void acceptFriend(FriendshipRequest request, StreamObserver<User> responseObserver) {
        UserEntity currentUser = userdataRepository.getByUsername(request.getUsername());
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
    }

    @Override
    public void rejectFriend(FriendshipRequest request, StreamObserver<User> responseObserver) {
        UserEntity currentUser = userdataRepository.getByUsername(request.getUsername());
        UserEntity targetUser = getCorrectTargetUser(currentUser.getId(), UUID.fromString(request.getTargetUserId()));
        FriendshipEntity incomeInvitation = getPendingIncomeInvitation(currentUser, targetUser);

        currentUser.removeIncomeInvitation(incomeInvitation);

        userdataRepository.save(currentUser);
        responseObserver.onNext(UserEntity.toGrpcMessage(targetUser, FriendStatus.NOT_FRIEND));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteFriend(FriendshipRequest request, StreamObserver<User> responseObserver) {
        UserEntity currentUser = userdataRepository.getByUsername(request.getUsername());
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
    }

    @Override
    @Transactional
    public void deleteUser(Username request, StreamObserver<Empty> responseObserver) {
        userdataRepository.deleteByUsername(request.getUsername());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    private void checkCanSendInvitation(UserEntity currentUser, UserEntity targetUser) {
        if (targetUser.findIncomeInvitation(currentUser).isPresent()) {
            throw new RuntimeException("Invitation already exist");
        }
        if (currentUser.findIncomeInvitation(currentUser).isPresent()) {
            throw new RuntimeException("Invitation already exist");
        }
    }

    private UserEntity getCorrectTargetUser(UUID currentUserId, UUID targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new RuntimeException("Target user should not be same");
        }
        return userdataRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));
    }

    private FriendshipEntity getPendingIncomeInvitation(UserEntity currentUser, UserEntity targetUser) {
        FriendshipEntity incomeInvitation = currentUser.findIncomeInvitation(targetUser)
                .orElseThrow(() -> new RuntimeException("Invitation not exist"));

        if (incomeInvitation.getStatus().equals(FriendshipStatus.ACCEPTED)) {
            throw new RuntimeException("Invitation already accepted");
        }
        return incomeInvitation;
    }
}
