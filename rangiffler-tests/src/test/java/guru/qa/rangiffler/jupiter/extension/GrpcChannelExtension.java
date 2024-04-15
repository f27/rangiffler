package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.api.grpc.GrpcChannelProvider;
import io.grpc.ManagedChannel;

public class GrpcChannelExtension implements SuiteExtension {
    @Override
    public void afterSuite() {
        GrpcChannelProvider.INSTANCE.storedChannels().forEach(
                ManagedChannel::shutdownNow
        );
    }
}
