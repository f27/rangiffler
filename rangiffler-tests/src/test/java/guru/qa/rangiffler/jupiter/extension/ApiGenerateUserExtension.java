package guru.qa.rangiffler.jupiter.extension;

import guru.qa.grpc.rangiffler.grpc.FriendStatus;
import guru.qa.grpc.rangiffler.grpc.User;
import guru.qa.rangiffler.api.grpc.PhotoGrpcClient;
import guru.qa.rangiffler.api.grpc.UserdataGrpcClient;
import guru.qa.rangiffler.api.rest.AuthApiClient;
import guru.qa.rangiffler.db.entity.user.UserAuthEntity;
import guru.qa.rangiffler.db.repository.AuthRepository;
import guru.qa.rangiffler.db.repository.hibernate.AuthRepositoryHibernate;
import guru.qa.rangiffler.jupiter.annotation.Friend;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.util.DataUtil;
import guru.qa.rangiffler.util.ImageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static com.codeborne.selenide.Selenide.sleep;

public class ApiGenerateUserExtension extends AbstractGenerateUserExtension {

    private static final String DEFAULT_COUNTRY_CODE = "ru";

    private final AuthApiClient authClient = new AuthApiClient();
    private final UserdataGrpcClient userdataGrpcClient = new UserdataGrpcClient();
    private final PhotoGrpcClient photoGrpcClient = new PhotoGrpcClient();
    private final AuthRepository authRepository = new AuthRepositoryHibernate();

    @Override
    public UserModel createUser(String username,
                                String password,
                                boolean generateFirstname,
                                boolean generateLastname,
                                boolean generateCountry,
                                String avatar,
                                FriendStatus status) throws IOException {

        final String realUsername = username.isEmpty() ? DataUtil.generateRandomUsername() : username;
        final String realPassword = password.isEmpty() ? DataUtil.generateRandomPassword() : password;
        authClient.register(realUsername, realPassword);

        User updateUserRequest = User.newBuilder()
                .setUsername(realUsername)
                .setFirstname(generateFirstname ? DataUtil.generateRandomFirstname() : "")
                .setLastname(generateLastname ? DataUtil.generateRandomLastname() : "")
                .setCountryCode(generateCountry ? CountryEnum.getRandom().getCode() : DEFAULT_COUNTRY_CODE)
                .setAvatar(avatar.isEmpty() ? "" : ImageUtil.getImageAsBase64(avatar))
                .build();

        User updateUserResponse = null;
        for (int i = 1; i <= 30; i++) {
            try {
                updateUserResponse = userdataGrpcClient.updateUser(updateUserRequest);
                break;
            } catch (Throwable t) {
                sleep(200);
            }
        }
        if (updateUserResponse == null) {
            throw new RuntimeException("User not found in userdata db");
        }

        return new UserModel(
                UUID.fromString(updateUserResponse.getId()),
                null,
                updateUserResponse.getUsername(),
                realPassword,
                updateUserResponse.getFirstname(),
                updateUserResponse.getLastname(),
                avatar,
                CountryEnum.findByCode(updateUserResponse.getCountryCode()),
                status,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    @Override
    public void addPhotos(UserModel user, Photo[] photos) {
        for (Photo photo : photos) {
            PhotoModel photoModel = new PhotoModel(
                    null,
                    photo.country(),
                    photo.description(),
                    photo.image()
            );
            photoGrpcClient.createPhoto(user.id(), photoModel);
            user.addPhoto(photoModel);
        }
    }

    @Override
    public void addFriends(UserModel user, Friend[] friends) throws IOException {
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

            switch (createdFriend.friendStatus()) {
                case FRIEND -> {
                    userdataGrpcClient.inviteFriend(user.username(), createdFriend.id());
                    userdataGrpcClient.acceptFriend(createdFriend.username(), user.id());
                }
                case INVITATION_RECEIVED -> userdataGrpcClient.inviteFriend(createdFriend.username(), user.id());

                case INVITATION_SENT -> userdataGrpcClient.inviteFriend(user.username(), createdFriend.id());

            }
            user.addFriend(createdFriend);
        }
    }

    @Override
    public void deleteUser(UserModel user) {
        photoGrpcClient.deleteAllPhotos(user.id());
        userdataGrpcClient.deleteUser(user.username());
        UUID userAuthId;
        if (user.authId() == null) {
            final UserAuthEntity authEntity = authRepository.findByUsername(user.username()).orElseThrow();
            userAuthId = authEntity.getId();
        } else {
            userAuthId = user.authId();
        }
        authRepository.deleteById(userAuthId);
        for (UserModel friend : user.friends()) {
            deleteUser(friend);
        }
    }
}
