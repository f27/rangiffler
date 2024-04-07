package guru.qa.rangiffler.entity;

import guru.qa.grpc.rangiffler.grpc.GetPhotosResponse;
import guru.qa.grpc.rangiffler.grpc.Like;
import guru.qa.grpc.rangiffler.grpc.Likes;
import guru.qa.grpc.rangiffler.grpc.PhotoResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.domain.Slice;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "photo")
public class PhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @Column()
    private String description;

    @Column()
    private byte[] photo;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "photo_like",
            joinColumns = {
                    @JoinColumn(name = "photo_id", nullable = false)
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "like_id", nullable = false)
            })
    private List<LikeEntity> likes = new ArrayList<>();

    public static PhotoResponse toGrpcMessage(PhotoEntity photo, UUID currentUserId) {
        List<Like> likes = photo.getLikes()
                .stream()
                .map(LikeEntity::toGrpcMessage)
                .toList();
        Likes likesMessage = Likes.newBuilder()
                .setTotal(likes.size())
                .addAllLikes(likes)
                .build();
        return PhotoResponse.newBuilder()
                .setCanEdit(photo.userId.equals(currentUserId))
                .setPhotoId(photo.getId().toString())
                .setSrc(new String(photo.getPhoto(), StandardCharsets.UTF_8))
                .setCountryCode(photo.getCountryCode())
                .setDescription(photo.getDescription())
                .setCreationDate(String.valueOf(photo.getCreatedDate()))
                .setLikes(likesMessage)
                .build();
    }

    public static GetPhotosResponse toGrpcMessage(Slice<PhotoEntity> photos, UUID currentUserId) {
        return GetPhotosResponse.newBuilder()
                .addAllPhotos(photos.map(entity -> toGrpcMessage(entity, currentUserId)).toList())
                .setHasNext(photos.hasNext())
                .build();
    }

    public void addLike(UUID userId) {
        likes.stream()
                .filter(entity -> entity.getUserId().equals(userId))
                .findAny()
                .ifPresentOrElse((entity) -> likes.remove(entity), () -> {
                    LikeEntity like = new LikeEntity();
                    like.setUserId(userId);
                    likes.add(like);
                });
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PhotoEntity that = (PhotoEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}