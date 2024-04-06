package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.db.entity.Authority;
import guru.qa.rangiffler.db.entity.AuthorityEntity;
import guru.qa.rangiffler.db.entity.UserAuthEntity;
import guru.qa.rangiffler.db.entity.UserEntity;
import guru.qa.rangiffler.db.repository.AuthRepository;
import guru.qa.rangiffler.db.repository.UserdataRepository;
import guru.qa.rangiffler.db.repository.hibernate.AuthRepositoryHibernate;
import guru.qa.rangiffler.db.repository.hibernate.UserdataRepositoryHibernate;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.util.DataUtil;
import guru.qa.rangiffler.util.ImageUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class DBGenerateUserExtension extends AbstractGenerateUserExtension {

    private final AuthRepository authRepository = new AuthRepositoryHibernate();
    private final UserdataRepository userdataRepository = new UserdataRepositoryHibernate();

    private static final String DEFAULT_COUNTRY_CODE = "ru";

    @Override
    public UserModel createUser(GenerateUser annotation) {
        UserAuthEntity userAuth = new UserAuthEntity();
        userAuth.setUsername(annotation.username().isEmpty() ? DataUtil.generateRandomUsername() : annotation.username());
        userAuth.setPassword(annotation.password().isEmpty() ? DataUtil.generateRandomPassword() : annotation.password());
        userAuth.setEnabled(true);
        userAuth.setAccountNonExpired(true);
        userAuth.setAccountNonLocked(true);
        userAuth.setCredentialsNonExpired(true);
        userAuth.setAuthorities(new ArrayList<>(Arrays.stream(Authority.values())
                .map(a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    ae.setUser(userAuth);
                    return ae;
                }).toList()));

        UserEntity userdata = new UserEntity();
        userdata.setCountryCode(annotation.generateCountry() ? DataUtil.generateRandomCountry().getCode() : DEFAULT_COUNTRY_CODE);
        userdata.setUsername(userAuth.getUsername());
        userdata.setFirstname(annotation.generateFirstname() ? DataUtil.generateRandomFirstname() : null);
        userdata.setLastname(annotation.generateLastname() ? DataUtil.generateRandomLastname() : null);
        userdata.setAvatar(annotation.avatar().isEmpty() ? null : ImageUtil.getImageAsBase64(annotation.avatar()).getBytes());

        authRepository.create(userAuth);
        try {
            userdataRepository.create(userdata);
        } catch (Throwable t) {
            authRepository.deleteById(userAuth.getId());
            throw t;
        }

        UserModel user = new UserModel();
        user.setId(userdata.getId());
        user.setAuthId(userAuth.getId());
        user.setUsername(userdata.getUsername());
        user.setPassword(userAuth.getPassword());
        user.setFirstname(userdata.getFirstname());
        user.setLastname(userdata.getLastname());
        user.setCountry(CountryEnum.findByCode(userdata.getCountryCode()));
        user.setAvatar(annotation.avatar());
        return user;
    }

    @Override
    public void deleteUserById(UUID id, UUID authId) {
        userdataRepository.deleteById(id);
        authRepository.deleteById(authId);
    }
}
