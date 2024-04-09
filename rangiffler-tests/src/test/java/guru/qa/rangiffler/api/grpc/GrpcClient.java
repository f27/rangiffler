package guru.qa.rangiffler.api.grpc;

import guru.qa.rangiffler.config.Config;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;


public abstract class GrpcClient {

    protected static final Config CFG = Config.getInstance();
    protected final Channel channel;

    public GrpcClient(String host, int port) {
        this.channel  = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }
}
