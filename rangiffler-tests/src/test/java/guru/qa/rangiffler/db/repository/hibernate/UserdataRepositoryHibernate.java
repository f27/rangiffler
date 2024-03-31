package guru.qa.rangiffler.db.repository.hibernate;

import guru.qa.rangiffler.db.DataBase;
import guru.qa.rangiffler.db.entity.UserEntity;
import guru.qa.rangiffler.db.jpa.EmfProvider;
import guru.qa.rangiffler.db.jpa.JpaService;
import guru.qa.rangiffler.db.repository.UserdataRepository;

import java.util.Optional;
import java.util.UUID;

public class UserdataRepositoryHibernate extends JpaService implements UserdataRepository {

    public UserdataRepositoryHibernate() {
        super(EmfProvider.INSTANCE.emf(DataBase.USERDATA).createEntityManager());
    }

    @Override
    public UserEntity create(UserEntity user) {
        persist(user);
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return Optional.ofNullable(getById(UserEntity.class, id));
    }

    @Override
    public void deleteById(UUID id) {
        UserEntity toBeDeleted = findById(id).get();
        remove(toBeDeleted);
    }
}
