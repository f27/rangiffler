package guru.qa.rangiffler.db.repository;

import guru.qa.rangiffler.db.entity.user.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataRepository {

    UserEntity create(UserEntity user);

    Optional<UserEntity> findByUsername(String username);

    void deleteById(UUID id);

}
