package guru.qa.rangiffler.db.entity.user;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class FriendshipId implements Serializable {

    private UUID requester, addressee;
}
