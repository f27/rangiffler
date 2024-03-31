package guru.qa.rangiffler.db.repository;

import guru.qa.rangiffler.db.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataRepository {

    UserEntity create(UserEntity user);

    Optional<UserEntity> findById(UUID id);

    void deleteById(UUID id);

}
