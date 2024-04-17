package guru.qa.rangiffler.grpc;

import guru.qa.rangiffler.model.grpc.GrpcAddress;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum GrpcChannelProvider {
    INSTANCE;

    private final Map<GrpcAddress, ManagedChannel> store = new ConcurrentHashMap<>();

    public Channel channel(GrpcAddress address) {
        return store.computeIfAbsent(address, k -> ManagedChannelBuilder.forAddress(address.host(), address.port())
                .usePlaintext()
                .build());
    }

    public Collection<ManagedChannel> storedChannels() {
        return store.values();
    }
}
