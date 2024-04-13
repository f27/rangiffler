package guru.qa.rangiffler.entity.user;

import guru.qa.grpc.rangiffler.grpc.FriendStatus;
import guru.qa.grpc.rangiffler.grpc.GrpcUser;
import guru.qa.rangiffler.entity.friendship.FriendshipEntity;
import guru.qa.rangiffler.entity.friendship.FriendshipStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column()
    private String firstname;

    @Column()
    private String lastname;

    @Column()
    private byte[] avatar;

    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @OneToMany(mappedBy = "requester", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendshipEntity> outcomeInvitations;

    @OneToMany(mappedBy = "addressee", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendshipEntity> incomeInvitations;

    public static GrpcUser toGrpcMessage(UserEntity entity, FriendStatus status) {
        GrpcUser.Builder builder = GrpcUser.newBuilder()
                .setId(entity.getId().toString())
                .setUsername(entity.getUsername())
                .setCountryCode(entity.getCountryCode())
                .setFriendStatus(status);

        if (entity.getFirstname() != null) {
            builder.setFirstname(entity.getFirstname());
        }
        if (entity.getLastname() != null) {
            builder.setLastname(entity.getLastname());
        }
        if (entity.getAvatar() != null) {
            builder.setAvatar(new String(entity.getAvatar(), StandardCharsets.UTF_8));
        }

        return builder.build();
    }

    @PreRemove
    private void removeInvites() {
        outcomeInvitations.forEach(friendship -> friendship.getAddressee().removeIncomeInvitation(friendship));
        incomeInvitations.forEach(friendship -> friendship.getRequester().removeOutcomeInvitation(friendship));
    }

    public FriendStatus getStatus(UserEntity friend) {
        if (getFriends().contains(friend)) {
            return FriendStatus.FRIEND;
        } else if (getInvitationSentUsers().contains(friend)) {
            return FriendStatus.INVITATION_SENT;
        } else if (getInvitationReceivedUsers().contains(friend)) {
            return FriendStatus.INVITATION_RECEIVED;
        }
        return FriendStatus.NOT_FRIEND;
    }

    public List<UserEntity> getFriends() {
        return outcomeInvitations.stream()
                .filter(friendshipEntity -> friendshipEntity.getStatus() == FriendshipStatus.ACCEPTED)
                .map(FriendshipEntity::getAddressee)
                .toList();
    }

    public List<UserEntity> getInvitationSentUsers() {
        return outcomeInvitations.stream()
                .filter(friendshipEntity -> friendshipEntity.getStatus() == FriendshipStatus.PENDING)
                .map(FriendshipEntity::getAddressee)
                .toList();
    }

    public List<UserEntity> getInvitationReceivedUsers() {
        return incomeInvitations.stream()
                .filter(friendshipEntity -> friendshipEntity.getStatus() == FriendshipStatus.PENDING)
                .map(FriendshipEntity::getRequester)
                .toList();
    }

    public Optional<FriendshipEntity> findIncomeInvitation(UserEntity user) {
        return incomeInvitations.stream()
                .filter(friendshipEntity -> friendshipEntity.getRequester().equals(user))
                .findFirst();
    }

    public Optional<FriendshipEntity> findOutcomeInvitation(UserEntity user) {
        return outcomeInvitations.stream()
                .filter(friendshipEntity -> friendshipEntity.getAddressee().equals(user))
                .findFirst();
    }

    public void addIncomeInvitation(FriendshipEntity friendship) {
        incomeInvitations.add(friendship);
    }

    public void addOutcomeInvitation(FriendshipEntity friendship) {
        outcomeInvitations.add(friendship);
    }

    public void removeIncomeInvitation(FriendshipEntity friendship) {
        incomeInvitations.remove(friendship);
    }

    public void removeOutcomeInvitation(FriendshipEntity friendship) {
        outcomeInvitations.remove(friendship);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UserEntity that = (UserEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId())
                && getUsername() != null && Objects.equals(getUsername(), that.getUsername());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
