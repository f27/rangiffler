package guru.qa.rangiffler.model;

import guru.qa.grpc.rangiffler.grpc.FriendStatus;
import guru.qa.rangiffler.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record UserModel(
        UUID id,
        UUID authId,
        String username,
        String password,
        String firstname,
        String lastname,
        String avatar,
        CountryEnum country,
        FriendStatus friendStatus,
        List<UserModel> friends,
        List<PhotoModel> photos
) {

    public void addFriend(UserModel friend) {
        friends.add(friend);
    }

    public void addPhoto(PhotoModel photo) {
        photos.add(photo);
    }

    public List<PhotoModel> photosWithAcceptedFriends() {
        List<PhotoModel> photoWithFriends = new ArrayList<>(photos);
        photoWithFriends.addAll(friends.stream()
                .filter(userModel -> userModel.friendStatus.equals(FriendStatus.FRIEND))
                .map(userModel -> userModel.photos)
                .flatMap(List::stream)
                .toList());
        return photoWithFriends;
    }

    public String getAvatarAsBase64() {
        if (avatar.isEmpty()) {
            return "";
        }
        return ImageUtil.getImageAsBase64(avatar);
    }

    @Override
    public String toString() {
        return username;
    }

    public String toKafkaJson() {
        return "{\"username\":\"" + username + "\"}";
    }
}
