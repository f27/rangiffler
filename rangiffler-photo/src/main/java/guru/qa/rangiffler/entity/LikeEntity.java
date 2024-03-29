package guru.qa.rangiffler.entity;

import guru.qa.grpc.rangiffler.grpc.Like;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@SecondaryTable(name = "photo_like", pkJoinColumns = @PrimaryKeyJoinColumn(name = "like_id"))
@Table(name = "`like`")
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    @ManyToOne
    @JoinColumn(name = "photo_id", table = "photo_like")
    private PhotoEntity photo;

    public static Like toGrpcMessage(LikeEntity entity) {
        return Like.newBuilder()
                .setUserId(entity.getUserId().toString())
                .build();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        LikeEntity that = (LikeEntity) o;
        return getUserId() != null && Objects.equals(getUserId(), that.getUserId())
                && getPhoto() != null && Objects.equals(getPhoto(), that.getPhoto());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
