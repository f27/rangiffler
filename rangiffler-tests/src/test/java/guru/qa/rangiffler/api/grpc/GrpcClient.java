package guru.qa.rangiffler.api.grpc;

import guru.qa.rangiffler.config.Config;
import io.grpc.Channel;


public abstract class GrpcClient {

    protected static final Config CFG = Config.getInstance();
    protected final Channel channel;

    public GrpcClient(Channel channel) {
        this.channel = channel;
    }
}
