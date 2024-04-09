package guru.qa.rangiffler.api.grpc;

import guru.qa.grpc.rangiffler.grpc.RangifflerPhotoServiceGrpc;

public class PhotoGrpcClient extends GrpcClient {

    private final RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub blockingStub;

    public PhotoGrpcClient() {
        super(CFG.photoGrpcHost(), CFG.photoGrpcPort());
        this.blockingStub = RangifflerPhotoServiceGrpc.newBlockingStub(channel);
    }
}
