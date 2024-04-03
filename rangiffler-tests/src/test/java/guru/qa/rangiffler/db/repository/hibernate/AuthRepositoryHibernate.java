package guru.qa.rangiffler.db.repository.hibernate;

import guru.qa.rangiffler.db.DataBase;
import guru.qa.rangiffler.db.entity.UserAuthEntity;
import guru.qa.rangiffler.db.jpa.EmfProvider;
import guru.qa.rangiffler.db.jpa.JpaService;
import guru.qa.rangiffler.db.repository.AuthRepository;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

public class AuthRepositoryHibernate extends JpaService implements AuthRepository {

    private final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public AuthRepositoryHibernate() {
        super(EmfProvider.INSTANCE.emf(DataBase.AUTH).createEntityManager());
    }

    @Override
    public UserAuthEntity create(UserAuthEntity user) {
        String notEncodedPassword = user.getPassword();
        user.setPassword(pe.encode(notEncodedPassword));
        persist(user);
        user.setPassword(notEncodedPassword);
        return user;
    }

    @Override
    public Optional<UserAuthEntity> findByUsername(String username) {
        String query = "SELECT u FROM UserAuthEntity u where u.username=:username";
        return em.createQuery(query, UserAuthEntity.class)
                .setParameter("username", username)
                .getResultStream().findAny();
    }

    @Override
    public void deleteById(UUID id) {
        removeById(UserAuthEntity.class, id);
    }
}
