package guru.qa.rangiffler.api.grpc;

import guru.qa.grpc.rangiffler.grpc.RangifflerUserdataServiceGrpc;

public class UserdataGrpcClient extends GrpcClient {

    private final RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub blockingStub;

    public UserdataGrpcClient() {
        super(CFG.geoGrpcHost(), CFG.geoGrpcPort());
        this.blockingStub = RangifflerUserdataServiceGrpc.newBlockingStub(channel);
    }
}
