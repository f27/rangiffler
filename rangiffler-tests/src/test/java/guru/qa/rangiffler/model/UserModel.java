package guru.qa.rangiffler.model;

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
}
