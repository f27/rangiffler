package guru.qa.rangiffler.service;

import guru.qa.grpc.rangiffler.grpc.*;
import guru.qa.rangiffler.entity.friendship.FriendshipEntity;
import guru.qa.rangiffler.entity.friendship.FriendshipStatus;
import guru.qa.rangiffler.entity.user.UserEntity;
import guru.qa.rangiffler.repository.UserdataRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        responseObserver.onNext(UserEntity.toGrpcMessage(
                userdataRepository.getByUsername(request.getUsername()),
                FriendStatus.NOT_FRIEND));
        responseObserver.onCompleted();
    }

    @Override
    public void getUsers(UsersRequest request, StreamObserver<UsersResponse> responseObserver) {
        String username = request.getUsername();

        UserEntity currentUser = userdataRepository.getByUsername(username);

        List<UserEntity> currentUserFriends = currentUser.getFriends();
        List<UserEntity> currentUserInvitationSent = currentUser.getInvitationSentUsers();
        List<UserEntity> currentUserInvitationReceived = currentUser.getInvitationReceivedUsers();

        Slice<UserEntity> users;
        if (request.getSearchQuery().isEmpty()) {
            users = userdataRepository.findByUsernameNot(username,
                    PageRequest.of(request.getPage(),
                            request.getSize()));
        } else {
            users = userdataRepository.findByUsernameNotAndSearchQuery(username,
                    PageRequest.of(request.getPage(),
                            request.getSize()),
                    request.getSearchQuery());
        }

        List<User> usersWithStatus = new ArrayList<>();

        for (UserEntity user : users) {
            if (currentUserFriends.contains(user)) {
                usersWithStatus.add(UserEntity.toGrpcMessage(user, FriendStatus.FRIEND));
            } else if (currentUserInvitationSent.contains(user)) {
                usersWithStatus.add(UserEntity.toGrpcMessage(user, FriendStatus.INVITATION_SENT));
            } else if (currentUserInvitationReceived.contains(user)) {
                usersWithStatus.add(UserEntity.toGrpcMessage(user, FriendStatus.INVITATION_RECEIVED));
            } else {
                usersWithStatus.add(UserEntity.toGrpcMessage(user, FriendStatus.NOT_FRIEND));
            }
        }

        UsersResponse response = UsersResponse.newBuilder()
                .addAllUsers(usersWithStatus)
                .setHasNext(users.hasNext())
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

        if (currentUser.getId().equals(UUID.fromString(request.getTargetUserId()))) {
            throw new RuntimeException("Target user should not be same");
        }

        Optional<UserEntity> targetUserOptional = userdataRepository.findById(UUID.fromString(request.getTargetUserId()));

        UserEntity targetUser;
        if (targetUserOptional.isPresent()) {
            targetUser = targetUserOptional.get();
        } else {
            throw new RuntimeException("Target user not found");
        }

        if (targetUser.findIncomeInvitation(currentUser).isPresent()) {
            throw new RuntimeException("Invitation already exist");
        }

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

        if (currentUser.getId().equals(UUID.fromString(request.getTargetUserId()))) {
            throw new RuntimeException("Target user should not be same");
        }

        Optional<UserEntity> targetUserOptional = userdataRepository.findById(UUID.fromString(request.getTargetUserId()));

        UserEntity targetUser;
        if (targetUserOptional.isPresent()) {
            targetUser = targetUserOptional.get();
        } else {
            throw new RuntimeException("Target user not found");
        }

        Optional<FriendshipEntity> incomeInvitationOptional = currentUser.findIncomeInvitation(targetUser);

        if (incomeInvitationOptional.isEmpty()) {
            throw new RuntimeException("Invitation not exist");
        }

        if (incomeInvitationOptional.get().getStatus().equals(FriendshipStatus.ACCEPTED)) {
            throw new RuntimeException("Invitation already accepted");
        }

        incomeInvitationOptional.get().setStatus(FriendshipStatus.ACCEPTED);

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

        if (currentUser.getId().equals(UUID.fromString(request.getTargetUserId()))) {
            throw new RuntimeException("Target user should not be same");
        }

        Optional<UserEntity> targetUserOptional = userdataRepository.findById(UUID.fromString(request.getTargetUserId()));

        UserEntity targetUser;
        if (targetUserOptional.isPresent()) {
            targetUser = targetUserOptional.get();
        } else {
            throw new RuntimeException("Target user not found");
        }

        Optional<FriendshipEntity> incomeInvitationOptional = currentUser.findIncomeInvitation(targetUser);

        if (incomeInvitationOptional.isEmpty()) {
            throw new RuntimeException("Invitation not exist");
        }

        if (incomeInvitationOptional.get().getStatus().equals(FriendshipStatus.ACCEPTED)) {
            throw new RuntimeException("Invitation already accepted");
        }

        currentUser.removeIncomeInvitation(incomeInvitationOptional.get());

        userdataRepository.save(currentUser);
        responseObserver.onNext(UserEntity.toGrpcMessage(targetUser, FriendStatus.NOT_FRIEND));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteFriend(FriendshipRequest request, StreamObserver<User> responseObserver) {
        UserEntity currentUser = userdataRepository.getByUsername(request.getUsername());

        if (currentUser.getId().equals(UUID.fromString(request.getTargetUserId()))) {
            throw new RuntimeException("Target user should not be same");
        }

        Optional<UserEntity> targetUserOptional = userdataRepository.findById(UUID.fromString(request.getTargetUserId()));

        UserEntity targetUser;
        if (targetUserOptional.isPresent()) {
            targetUser = targetUserOptional.get();
        } else {
            throw new RuntimeException("Target user not found");
        }

        Optional<FriendshipEntity> incomeInvitationOptional = currentUser.findIncomeInvitation(targetUser);
        Optional<FriendshipEntity> outcomeInvitationOptional = currentUser.findOutcomeInvitation(targetUser);

        if (incomeInvitationOptional.isEmpty() || outcomeInvitationOptional.isEmpty()) {
            throw new RuntimeException("Invitation not exist");
        }

        if (!incomeInvitationOptional.get().getStatus().equals(FriendshipStatus.ACCEPTED) ||
                !outcomeInvitationOptional.get().getStatus().equals(FriendshipStatus.ACCEPTED)) {
            throw new RuntimeException("Invitation not accepted");
        }

        currentUser.removeIncomeInvitation(incomeInvitationOptional.get());
        currentUser.removeOutcomeInvitation(outcomeInvitationOptional.get());

        userdataRepository.save(currentUser);
        responseObserver.onNext(UserEntity.toGrpcMessage(targetUser, FriendStatus.NOT_FRIEND));
        responseObserver.onCompleted();
    }
}
