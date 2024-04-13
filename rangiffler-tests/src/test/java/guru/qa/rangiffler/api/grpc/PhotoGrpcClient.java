package guru.qa.rangiffler.api.grpc;

import guru.qa.grpc.rangiffler.grpc.*;
import io.qameta.allure.grpc.AllureGrpc;

public class PhotoGrpcClient extends GrpcClient {

    private final RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub blockingStub;

    public PhotoGrpcClient() {
        super(GrpcChannelProvider.INSTANCE.channel(CFG.photoGrpcAddress()));
        this.blockingStub = RangifflerPhotoServiceGrpc.newBlockingStub(channel).withInterceptors(new AllureGrpc());
    }

    public PhotoResponse createPhoto(CreatePhotoRequest request) {
        return blockingStub.createPhoto(request);
    }

    public PhotoResponse updatePhoto(UpdatePhotoRequest request) {
        return blockingStub.updatePhoto(request);
    }

    public void deleteAllPhotos(DeleteAllPhotosRequest request) {
        blockingStub.deleteAllPhotos(request);
    }
}
