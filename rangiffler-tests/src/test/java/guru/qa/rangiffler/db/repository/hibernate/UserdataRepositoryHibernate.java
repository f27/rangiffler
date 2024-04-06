package guru.qa.rangiffler.db.repository.hibernate;

import guru.qa.rangiffler.db.DataBase;
import guru.qa.rangiffler.db.entity.user.UserEntity;
import guru.qa.rangiffler.db.jpa.EmfProvider;
import guru.qa.rangiffler.db.jpa.JpaService;
import guru.qa.rangiffler.db.jpa.ThreadLocalEntityManager;
import guru.qa.rangiffler.db.repository.UserdataRepository;

import java.util.Optional;
import java.util.UUID;

public class UserdataRepositoryHibernate extends JpaService implements UserdataRepository {

    public UserdataRepositoryHibernate() {
        super(new ThreadLocalEntityManager(EmfProvider.INSTANCE.emf(DataBase.USERDATA)));
    }

    @Override
    public UserEntity create(UserEntity user) {
        persist(user);
        return user;
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        String query = "SELECT u FROM UserEntity u where u.username=:username";
        return em.createQuery(query, UserEntity.class)
                .setParameter("username", username)
                .getResultStream().findAny();
    }

    @Override
    public void deleteById(UUID id) {
        removeById(UserEntity.class, id);
    }

    @Override
    public UserEntity save(UserEntity user) {
        merge(user);
        return user;
    }
}
