package guru.qa.rangiffler.jupiter.extension;

import guru.qa.grpc.rangiffler.grpc.*;
import guru.qa.rangiffler.grpc.GrpcChannelProvider;
import guru.qa.rangiffler.api.AuthApiClient;
import guru.qa.rangiffler.config.Config;
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
import io.qameta.allure.grpc.AllureGrpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static com.codeborne.selenide.Selenide.sleep;

public class ApiGenerateUserExtension extends AbstractGenerateUserExtension {

    protected static final Config CFG = Config.getInstance();
    private static final String DEFAULT_COUNTRY_CODE = "ru";
    private final AuthApiClient authClient = new AuthApiClient();
    private final RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub userdataGrpcBlockingStub =
            RangifflerUserdataServiceGrpc.newBlockingStub(GrpcChannelProvider.INSTANCE.channel(CFG.userdataGrpcAddress()))
                    .withInterceptors(new AllureGrpc());
    private final RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub photoGrpcBlockingStub =
            RangifflerPhotoServiceGrpc.newBlockingStub(GrpcChannelProvider.INSTANCE.channel(CFG.photoGrpcAddress()))
                    .withInterceptors(new AllureGrpc());
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

        GrpcUser updateUserRequest = GrpcUser.newBuilder()
                .setUsername(realUsername)
                .setFirstname(generateFirstname ? DataUtil.generateRandomFirstname() : "")
                .setLastname(generateLastname ? DataUtil.generateRandomLastname() : "")
                .setCountryCode(generateCountry ? CountryEnum.getRandom().getCode() : DEFAULT_COUNTRY_CODE)
                .setAvatar(avatar.isEmpty() ? "" : ImageUtil.getImageAsBase64(avatar))
                .build();

        GrpcUser updateUserResponse = null;
        for (int i = 1; i <= 30; i++) {
            try {
                updateUserResponse = userdataGrpcBlockingStub.updateUser(updateUserRequest);
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
                    UUID.fromString(photoGrpcBlockingStub.createPhoto(CreatePhotoRequest.newBuilder()
                            .setUserId(user.id().toString())
                            .setSrc(ImageUtil.getImageAsBase64(photo.image()))
                            .setCountryCode(photo.country().getCode())
                            .setDescription(photo.description())
                            .build()).getPhotoId()),
                    photo.country(),
                    photo.description(),
                    photo.image()
            );
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
                    FriendshipRequest inviteRequest = FriendshipRequest.newBuilder()
                            .setUsername(user.username())
                            .setTargetUserId(createdFriend.id().toString())
                            .build();
                    FriendshipRequest acceptInviteRequest = FriendshipRequest.newBuilder()
                            .setUsername(createdFriend.username())
                            .setTargetUserId(user.id().toString())
                            .build();
                    userdataGrpcBlockingStub.inviteFriend(inviteRequest);
                    userdataGrpcBlockingStub.acceptFriend(acceptInviteRequest);
                }
                case INVITATION_RECEIVED -> {
                    FriendshipRequest request = FriendshipRequest.newBuilder()
                            .setUsername(createdFriend.username())
                            .setTargetUserId(user.id().toString())
                            .build();
                    userdataGrpcBlockingStub.inviteFriend(request);
                }

                case INVITATION_SENT -> {
                    FriendshipRequest request = FriendshipRequest.newBuilder()
                            .setUsername(user.username())
                            .setTargetUserId(createdFriend.id().toString())
                            .build();
                    userdataGrpcBlockingStub.inviteFriend(request);
                }

            }
            user.addFriend(createdFriend);
        }
    }

    @Override
    public void deleteUser(UserModel user) {
        photoGrpcBlockingStub.deleteAllPhotos(DeleteAllPhotosRequest.newBuilder().setUserId(user.id().toString()).build());
        userdataGrpcBlockingStub.deleteUser(Username.newBuilder().setUsername(user.username()).build());
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
