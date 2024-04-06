package guru.qa.rangiffler.model;

import guru.qa.grpc.rangiffler.grpc.FriendStatus;

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
}
