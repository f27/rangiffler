package guru.qa.rangiffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rangiffler.db.entity.Authority;
import guru.qa.rangiffler.db.entity.AuthorityEntity;
import guru.qa.rangiffler.db.entity.UserAuthEntity;
import guru.qa.rangiffler.db.entity.UserEntity;
import guru.qa.rangiffler.db.repository.hibernate.AuthRepositoryHibernate;
import guru.qa.rangiffler.db.repository.hibernate.UserdataRepositoryHibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class LoginTest extends BaseWebTest {

    private UserdataRepositoryHibernate userdataRepositoryHibernate = new UserdataRepositoryHibernate();
    private AuthRepositoryHibernate authRepositoryHibernate = new AuthRepositoryHibernate();

    private UserAuthEntity userAuth;
    private UserEntity user;

    @BeforeEach
    void createUser() {
        userAuth = new UserAuthEntity();
        userAuth.setUsername("valentin_0");
        userAuth.setPassword("12345");
        userAuth.setEnabled(true);
        userAuth.setAccountNonExpired(true);
        userAuth.setAccountNonLocked(true);
        userAuth.setCredentialsNonExpired(true);

        AuthorityEntity[] authorities = Arrays.stream(Authority.values()).map(
                authority -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(authority);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);
        userAuth.addAuthorities(authorities);

        user = new UserEntity();
        user.setUsername(userAuth.getUsername());
        user.setCountryCode("ru");

        authRepositoryHibernate.create(userAuth);
        userdataRepositoryHibernate.create(user);
    }

    @AfterEach
    void removeUser() {
        authRepositoryHibernate.deleteById(userAuth.getId());
        userdataRepositoryHibernate.deleteById(user.getId());
    }

    @Test
    void successfulLoginTest() {
        Selenide.open("/");
        welcomePage.clickLoginButton();
        loginPage
                .setUsername("valentin_0")
                .setPassword("12345")
                .clickSignIn();
        myTravelsPage
                .checkSuccessfullyAuthorized();
    }
}
