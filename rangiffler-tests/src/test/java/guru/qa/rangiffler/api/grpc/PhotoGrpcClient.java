package guru.qa.rangiffler.api.grpc;

import guru.qa.grpc.rangiffler.grpc.CreatePhotoRequest;
import guru.qa.grpc.rangiffler.grpc.DeleteAllPhotosRequest;
import guru.qa.grpc.rangiffler.grpc.PhotoResponse;
import guru.qa.grpc.rangiffler.grpc.RangifflerPhotoServiceGrpc;
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

    public void deleteAllPhotos(DeleteAllPhotosRequest request) {
        blockingStub.deleteAllPhotos(request);
    }
}
