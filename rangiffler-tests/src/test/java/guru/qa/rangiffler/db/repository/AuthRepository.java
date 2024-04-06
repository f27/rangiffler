package guru.qa.rangiffler.db.repository;

import guru.qa.rangiffler.db.entity.UserAuthEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthRepository {

    UserAuthEntity create(UserAuthEntity user);

    Optional<UserAuthEntity> findByUsername(String username);

    void deleteById(UUID id);

    void delete(UserAuthEntity user);

}
