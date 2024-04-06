package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.db.entity.photo.PhotoEntity;
import guru.qa.rangiffler.db.entity.user.*;
import guru.qa.rangiffler.db.repository.AuthRepository;
import guru.qa.rangiffler.db.repository.PhotoRepository;
import guru.qa.rangiffler.db.repository.UserdataRepository;
import guru.qa.rangiffler.db.repository.hibernate.AuthRepositoryHibernate;
import guru.qa.rangiffler.db.repository.hibernate.PhotoRepositoryHibernate;
import guru.qa.rangiffler.db.repository.hibernate.UserdataRepositoryHibernate;
import guru.qa.rangiffler.jupiter.annotation.Friend;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.FriendStatus;
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
    public UserModel createUser(String username,
                                String password,
                                boolean generateFirstname,
                                boolean generateLastname,
                                boolean generateCountry,
                                String avatar,
                                FriendStatus status) {
        final String realPassword = password.isEmpty() ? DataUtil.generateRandomPassword() : password;
        UserAuthEntity userAuth = new UserAuthEntity();
        userAuth.setUsername(username.isEmpty() ? DataUtil.generateRandomUsername() : username);
        userAuth.setPassword(realPassword);
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
        userdata.setCountryCode(generateCountry ? DataUtil.generateRandomCountry().getCode() : DEFAULT_COUNTRY_CODE);
        userdata.setUsername(userAuth.getUsername());
        userdata.setFirstname(generateFirstname ? DataUtil.generateRandomFirstname() : null);
        userdata.setLastname(generateLastname ? DataUtil.generateRandomLastname() : null);
        userdata.setAvatar(avatar.isEmpty() ? null : ImageUtil.getImageAsBase64(avatar).getBytes());

        authRepository.create(userAuth);
        try {
            userdataRepository.create(userdata);
        } catch (Throwable t) {
            authRepository.delete(userAuth);
            throw t;
        }

        return new UserModel(
                userdata.getId(),
                userAuth.getId(),
                userdata.getUsername(),
                realPassword,
                userdata.getFirstname(),
                userdata.getLastname(),
                avatar,
                CountryEnum.findByCode(userdata.getCountryCode()),
                status,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    @Override
    public void addPhotos(UserModel user, Photo[] photos) {
        for (Photo photo : photos) {
            PhotoEntity photoEntity = new PhotoEntity();
            photoEntity.setUserId(user.id());
            photoEntity.setCountryCode(photo.country().getCode());
            photoEntity.setDescription(photo.description());
            photoEntity.setPhoto(ImageUtil.getImageAsBase64(photo.image()).getBytes(StandardCharsets.UTF_8));
            photoRepository.create(photoEntity);

            PhotoModel photoModel = new PhotoModel(
                    photoEntity.getId(),
                    CountryEnum.findByCode(photoEntity.getCountryCode()),
                    photoEntity.getDescription(),
                    photo.image()
            );

            user.addPhoto(photoModel);
        }
    }

    @Override
    public void addFriends(UserModel user, Friend[] friends) {
        for (Friend friend : friends) {
            UserModel createdFriend = createUser(
                    friend.username(),
                    friend.password(),
                    friend.generateFirstname(),
                    friend.generateLastname(),
                    friend.generateCountry(),
                    friend.avatar(),
                    friend.status()
            );
            addPhotos(createdFriend, friend.photos());
            if (createdFriend.friendStatus() != FriendStatus.NONE) {
                UserEntity mainUser = userdataRepository.findByUsername(user.username()).orElseThrow();
                UserEntity friendUser = userdataRepository.findByUsername(createdFriend.username()).orElseThrow();
                if (createdFriend.friendStatus().equals(FriendStatus.FRIEND)) {
                    FriendshipEntity outcomeFriendship = new FriendshipEntity();
                    outcomeFriendship.setRequester(mainUser);
                    outcomeFriendship.setAddressee(friendUser);
                    outcomeFriendship.setStatus(FriendshipStatus.ACCEPTED);

                    FriendshipEntity incomeFriendship = new FriendshipEntity();
                    incomeFriendship.setRequester(friendUser);
                    incomeFriendship.setAddressee(mainUser);
                    incomeFriendship.setStatus(FriendshipStatus.ACCEPTED);

                    mainUser.addIncomeInvitation(incomeFriendship);
                    mainUser.addOutcomeInvitation(outcomeFriendship);
                    userdataRepository.save(mainUser);
                } else if (createdFriend.friendStatus().equals(FriendStatus.INCOME_INVITATION)) {
                    FriendshipEntity incomeFriendship = new FriendshipEntity();
                    incomeFriendship.setRequester(friendUser);
                    incomeFriendship.setAddressee(mainUser);
                    incomeFriendship.setStatus(FriendshipStatus.PENDING);

                    mainUser.addIncomeInvitation(incomeFriendship);
                    userdataRepository.save(mainUser);
                } else if (createdFriend.friendStatus().equals(FriendStatus.OUTCOME_INVITATION)) {
                    FriendshipEntity outcomeFriendship = new FriendshipEntity();
                    outcomeFriendship.setRequester(mainUser);
                    outcomeFriendship.setAddressee(friendUser);
                    outcomeFriendship.setStatus(FriendshipStatus.PENDING);

                    mainUser.addOutcomeInvitation(outcomeFriendship);
                    userdataRepository.save(mainUser);
                }
            }
            user.addFriend(createdFriend);
        }
    }

    @Override
    public void deleteUser(UserModel user) {
        photoRepository.deleteByUserId(user.id());
        userdataRepository.deleteById(user.id());
        authRepository.deleteById(user.authId());
        for (UserModel friend : user.friends()) {
            deleteUser(friend);
        }
    }
}
