package guru.qa.rangiffler.db.repository;

import guru.qa.rangiffler.db.entity.photo.PhotoEntity;

import java.util.List;
import java.util.UUID;

public interface PhotoRepository {

    PhotoEntity create(PhotoEntity photo);

    void deleteByUserId(UUID id);

    List<PhotoEntity> findByUserId(UUID userId);

}
