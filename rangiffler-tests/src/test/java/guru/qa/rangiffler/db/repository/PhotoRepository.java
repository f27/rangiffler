package guru.qa.rangiffler.db.repository;

import guru.qa.rangiffler.db.entity.photo.PhotoEntity;

import java.util.UUID;

public interface PhotoRepository {
    PhotoEntity create(PhotoEntity photo);

    void deleteById(UUID id);
}
