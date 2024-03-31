package guru.qa.rangiffler.entity.friendship;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class FriendshipId implements Serializable {

    private UUID requester, addressee;
}
