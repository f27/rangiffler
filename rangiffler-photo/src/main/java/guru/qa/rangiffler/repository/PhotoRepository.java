package guru.qa.rangiffler.repository;

import guru.qa.rangiffler.entity.PhotoEntity;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<PhotoEntity, UUID> {

    Slice<PhotoEntity> findAllByUserIdInOrderByCreatedDateDesc(@Nonnull List<UUID> userIds, @Nonnull Pageable pageable);

    List<PhotoEntity> findAllByUserIdIn(@Nonnull List<UUID> userIds);

    void removeAllByUserId(@Nonnull UUID userId);

    Optional<PhotoEntity> findByUserIdAndId(@Nonnull UUID userId, @Nonnull UUID id);

}
