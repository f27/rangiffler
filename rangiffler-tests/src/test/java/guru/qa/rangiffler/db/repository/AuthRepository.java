package guru.qa.rangiffler.db.repository;

import guru.qa.rangiffler.db.entity.UserAuthEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthRepository {

    UserAuthEntity create(UserAuthEntity user);

    Optional<UserAuthEntity> findById(UUID id);

    void deleteById(UUID id);
}
