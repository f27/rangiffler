package guru.qa.rangiffler.service;

import guru.qa.rangiffler.entity.friendship.FriendshipEntity;
import guru.qa.rangiffler.entity.friendship.FriendshipStatus;
import guru.qa.rangiffler.entity.user.UserEntity;
import guru.qa.rangiffler.repository.UserdataRepository;
import io.grpc.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserdataService {

    private final UserdataRepository userdataRepository;

    @Autowired
    public UserdataService(UserdataRepository userdataRepository) {
        this.userdataRepository = userdataRepository;
    }

    @Transactional(readOnly = true)
    public UserEntity getUser(String username) {
        UserEntity user = userdataRepository.getByUsername(username);
        if (user == null) {
            throw Status.NOT_FOUND.withDescription("User not found").asRuntimeException();
        }
        return user;
    }

    @Transactional(readOnly = true)
    public Slice<UserEntity> getPeople(String username, Pageable pageable, String searchQuery) {
        if (searchQuery.isEmpty()) {
            return userdataRepository.findByUsernameNot(username, pageable);
        }
        return userdataRepository.findByUsernameNotAndSearchQuery(username, pageable, searchQuery);
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getPeople(String username, String searchQuery) {
        if (searchQuery.isEmpty()) {
            return userdataRepository.findByUsernameNot(username);
        }
        return userdataRepository.findByUsernameNotAndSearchQuery(username, searchQuery);
    }

    @Transactional(readOnly = true)
    public Slice<UserEntity> getFriends(String username, Pageable pageable, String searchQuery) {
        UserEntity user = getUser(username);
        if (searchQuery.isEmpty()) {
            return userdataRepository.findFriends(user, pageable);
        }
        return userdataRepository.findFriends(user, pageable, searchQuery);
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getFriends(String username, String searchQuery) {
        UserEntity user = getUser(username);
        if (searchQuery.isEmpty()) {
            return userdataRepository.findFriends(user);
        }
        return userdataRepository.findFriends(user, searchQuery);
    }

    @Transactional(readOnly = true)
    public Slice<UserEntity> getIncomeInvitations(String username, Pageable pageable, String searchQuery) {
        UserEntity user = getUser(username);
        if (searchQuery.isEmpty()) {
            return userdataRepository.findIncomeInvitations(user, pageable);
        }
        return userdataRepository.findIncomeInvitations(user, pageable, searchQuery);
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getIncomeInvitations(String username, String searchQuery) {
        UserEntity user = getUser(username);
        if (searchQuery.isEmpty()) {
            return userdataRepository.findIncomeInvitations(user);
        }
        return userdataRepository.findIncomeInvitations(user, searchQuery);
    }

    @Transactional(readOnly = true)
    public Slice<UserEntity> getOutcomeInvitations(String username, Pageable pageable, String searchQuery) {
        UserEntity user = getUser(username);
        if (searchQuery.isEmpty()) {
            return userdataRepository.findOutcomeInvitations(user, pageable);
        }
        return userdataRepository.findOutcomeInvitations(user, pageable, searchQuery);
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getOutcomeInvitations(String username, String searchQuery) {
        UserEntity user = getUser(username);
        if (searchQuery.isEmpty()) {
            return userdataRepository.findOutcomeInvitations(user);
        }
        return userdataRepository.findOutcomeInvitations(user, searchQuery);
    }

    @Transactional
    public UserEntity updateUser(String username, String firstname, String lastname,
                                 String avatar, String countryCode) {
        UserEntity user = getUser(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setAvatar(avatar.getBytes());
        user.setCountryCode(countryCode);
        return userdataRepository.save(user);
    }

    @Transactional
    public UserEntity inviteFriend(String username, UUID targetId) {
        UserEntity currentUser = getUser(username);
        UserEntity targetUser = getCorrectTargetUser(currentUser.getId(), targetId);
        checkCanSendInvitation(currentUser, targetUser);
        FriendshipEntity friendship = new FriendshipEntity();
        friendship.setRequester(currentUser);
        friendship.setAddressee(targetUser);
        friendship.setStatus(FriendshipStatus.PENDING);
        targetUser.addIncomeInvitation(friendship);
        return userdataRepository.save(targetUser);
    }

    @Transactional
    public UserEntity acceptFriend(String username, UUID targetId) {
        UserEntity currentUser = getUser(username);
        UserEntity targetUser = getCorrectTargetUser(currentUser.getId(), targetId);

        FriendshipEntity initialInvitation = getPendingIncomeInvitation(currentUser, targetUser);
        initialInvitation.setStatus(FriendshipStatus.ACCEPTED);

        FriendshipEntity newInvitation = new FriendshipEntity();
        newInvitation.setRequester(currentUser);
        newInvitation.setAddressee(targetUser);
        newInvitation.setStatus(FriendshipStatus.ACCEPTED);
        currentUser.addOutcomeInvitation(newInvitation);
        userdataRepository.save(currentUser);
        return targetUser;
    }

    @Transactional
    public UserEntity rejectFriend(String username, UUID targetId) {
        UserEntity currentUser = getUser(username);
        UserEntity targetUser = getCorrectTargetUser(currentUser.getId(), targetId);
        FriendshipEntity invitation = getPendingIncomeInvitation(currentUser, targetUser);
        targetUser.removeOutcomeInvitation(invitation);
        return userdataRepository.save(targetUser);
    }

    @Transactional
    public UserEntity deleteFriend(String username, UUID targetId) {
        UserEntity currentUser = getUser(username);
        UserEntity targetUser = getCorrectTargetUser(currentUser.getId(), targetId);
        FriendshipEntity incomeInvitation = currentUser.findIncomeInvitation(targetUser)
                .orElseThrow(() -> Status.NOT_FOUND.withDescription("Accepted invitation not exist").asRuntimeException());
        FriendshipEntity outcomeInvitation = currentUser.findOutcomeInvitation(targetUser)
                .orElseThrow(() -> Status.NOT_FOUND.withDescription("Accepted invitation not exist").asRuntimeException());
        targetUser.removeOutcomeInvitation(incomeInvitation);
        targetUser.removeIncomeInvitation(outcomeInvitation);
        return userdataRepository.save(targetUser);
    }

    @Transactional
    public void deleteUser(String username) {
        userdataRepository.deleteByUsername(username);
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
