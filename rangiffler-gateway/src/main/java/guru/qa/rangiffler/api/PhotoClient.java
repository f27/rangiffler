package guru.qa.rangiffler.api;

import guru.qa.grpc.rangiffler.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class PhotoClient {

    @GrpcClient("photoClient")
    private RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub rangifflerPhotoServiceBlockingStub;

    public PhotoResponse likePhoto(LikePhotoRequest request) {
        return rangifflerPhotoServiceBlockingStub.likePhoto(request);
    }

    public PhotoResponse updatePhoto(UpdatePhotoRequest request) {
        return rangifflerPhotoServiceBlockingStub.updatePhoto(request);
    }

    public PhotoResponse createPhoto(CreatePhotoRequest request) {
        return rangifflerPhotoServiceBlockingStub.createPhoto(request);
    }

    public PhotoSliceResponse getPhotos(GetPhotosRequest request) {
        return rangifflerPhotoServiceBlockingStub.getPhotos(request);
    }

    public StatMapResponse getStat(GetStatRequest request) {
        return rangifflerPhotoServiceBlockingStub.getStat(request);
    }

    public void deletePhoto(DeletePhotoRequest request) {
        rangifflerPhotoServiceBlockingStub.deletePhoto(request);
    }
}
