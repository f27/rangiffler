package guru.qa.rangiffler.api;

import guru.qa.grpc.rangiffler.grpc.*;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component

public class UserDataClient {

    @GrpcClient("userdataClient")
    private RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceBlockingStub;

    public @Nonnull User currentUser(@Nonnull String username) {
        Username request = Username.newBuilder()
                .setUsername(username)
                .build();

        return rangifflerUserdataServiceBlockingStub.getUser(request);
    }

    public @Nonnull UsersResponse getFriends(@Nonnull String username) {
        UsersRequest request = UsersRequest.newBuilder()
                .setUsername(username)
                .build();
        return rangifflerUserdataServiceBlockingStub.getFriends(request);
    }
}
