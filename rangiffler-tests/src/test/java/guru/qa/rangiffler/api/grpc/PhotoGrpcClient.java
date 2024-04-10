package guru.qa.rangiffler.api.grpc;

import guru.qa.grpc.rangiffler.grpc.CreatePhotoRequest;
import guru.qa.grpc.rangiffler.grpc.DeleteAllPhotosRequest;
import guru.qa.grpc.rangiffler.grpc.PhotoResponse;
import guru.qa.grpc.rangiffler.grpc.RangifflerPhotoServiceGrpc;
import guru.qa.rangiffler.model.PhotoModel;

import java.util.UUID;

public class PhotoGrpcClient extends GrpcClient {

    private final RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub blockingStub;

    public PhotoGrpcClient() {
        super(CFG.photoGrpcHost(), CFG.photoGrpcPort());
        this.blockingStub = RangifflerPhotoServiceGrpc.newBlockingStub(channel);
    }

    public PhotoResponse createPhoto(UUID userId, PhotoModel photoModel) {
        CreatePhotoRequest request = CreatePhotoRequest.newBuilder()
                .setUserId(userId.toString())
                .setSrc(photoModel.getPhotoAsBase64())
                .setCountryCode(photoModel.country().getCode())
                .setDescription(photoModel.description())
                .build();
        return blockingStub.createPhoto(request);
    }

    public void deleteAllPhotos(UUID userId) {
        DeleteAllPhotosRequest request = DeleteAllPhotosRequest.newBuilder()
                .setUserId(userId.toString())
                .build();
        blockingStub.deleteAllPhotos(request);
    }
}
