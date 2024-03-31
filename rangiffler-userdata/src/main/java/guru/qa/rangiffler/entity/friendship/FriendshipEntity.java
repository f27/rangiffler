package guru.qa.rangiffler.entity.friendship;

import guru.qa.rangiffler.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@Entity
@IdClass(FriendshipId.class)
@Table(name = "friendship")
public class FriendshipEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private UserEntity requester;

    @Id
    @ManyToOne
    @JoinColumn(name = "addressee_id", nullable = false)
    private UserEntity addressee;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    @Column(nullable = false, columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        FriendshipEntity that = (FriendshipEntity) o;
        return getRequester() != null && Objects.equals(getRequester(), that.getRequester())
                && getAddressee()!= null && Objects.equals(getAddressee(), that.getAddressee());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
