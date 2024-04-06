package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.db.entity.photo.PhotoEntity;
import guru.qa.rangiffler.db.entity.user.Authority;
import guru.qa.rangiffler.db.entity.user.AuthorityEntity;
import guru.qa.rangiffler.db.entity.user.UserAuthEntity;
import guru.qa.rangiffler.db.entity.user.UserEntity;
import guru.qa.rangiffler.db.repository.AuthRepository;
import guru.qa.rangiffler.db.repository.PhotoRepository;
import guru.qa.rangiffler.db.repository.UserdataRepository;
import guru.qa.rangiffler.db.repository.hibernate.AuthRepositoryHibernate;
import guru.qa.rangiffler.db.repository.hibernate.PhotoRepositoryHibernate;
import guru.qa.rangiffler.db.repository.hibernate.UserdataRepositoryHibernate;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.util.DataUtil;
import guru.qa.rangiffler.util.ImageUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class DBGenerateUserExtension extends AbstractGenerateUserExtension {

    private static final String DEFAULT_COUNTRY_CODE = "ru";

    private final AuthRepository authRepository = new AuthRepositoryHibernate();
    private final UserdataRepository userdataRepository = new UserdataRepositoryHibernate();
    private final PhotoRepository photoRepository = new PhotoRepositoryHibernate();

    @Override
    public UserModel createUser(GenerateUser annotation) {
        final String password = annotation.password().isEmpty() ? DataUtil.generateRandomPassword() : annotation.password();
        UserAuthEntity userAuth = new UserAuthEntity();
        userAuth.setUsername(annotation.username().isEmpty() ? DataUtil.generateRandomUsername() : annotation.username());
        userAuth.setPassword(password);
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
            authRepository.delete(userAuth);
            throw t;
        }

        UserModel user = new UserModel();
        user.setId(userdata.getId());
        user.setAuthId(userAuth.getId());
        user.setUsername(userdata.getUsername());
        user.setPassword(password);
        user.setFirstname(userdata.getFirstname());
        user.setLastname(userdata.getLastname());
        user.setCountry(CountryEnum.findByCode(userdata.getCountryCode()));
        user.setAvatar(annotation.avatar());
        return user;
    }

    @Override
    public void addPhotos(UserModel user, Photo[] photos) {
        for (Photo photo : photos) {
            PhotoEntity photoEntity = new PhotoEntity();
            photoEntity.setUserId(user.getId());
            photoEntity.setCountryCode(photo.country().getCode());
            photoEntity.setDescription(photo.description());
            photoEntity.setPhoto(ImageUtil.getImageAsBase64(photo.image()).getBytes(StandardCharsets.UTF_8));
            photoRepository.create(photoEntity);

            PhotoModel photoModel = new PhotoModel();
            photoModel.setId(photoEntity.getId());
            photoModel.setCountry(CountryEnum.findByCode(photoEntity.getCountryCode()));
            photoModel.setDescription(photoEntity.getDescription());
            photoModel.setPhoto(photo.image());

            user.addPhoto(photoModel);
        }
    }

    @Override
    public void deleteUser(UserModel user) {
        for (PhotoModel photo : user.getPhotos()) {
            photoRepository.deleteById(photo.getId());
        }
        userdataRepository.deleteById(user.getId());
        authRepository.deleteById(user.getAuthId());
    }
}
