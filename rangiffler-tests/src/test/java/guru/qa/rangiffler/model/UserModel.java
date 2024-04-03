package guru.qa.rangiffler.model;

import guru.qa.grpc.rangiffler.grpc.FriendStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserModel {

    private UUID id;
    private UUID authId;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String avatar;
    private FriendStatus friendStatus;
    private List<UserModel> friends = new ArrayList<>();

    public void addFriend(UserModel friend) {
        friends.add(friend);
    }
}
