package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.grpc.*;
import guru.qa.rangiffler.entity.PhotoEntity;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.Map;
import java.util.UUID;

import static guru.qa.rangiffler.service.Validation.validate;

@GrpcService
public class PhotoGrpcService extends RangifflerPhotoServiceGrpc.RangifflerPhotoServiceImplBase {

    private final PhotoService photoService;

    @Autowired
    public PhotoGrpcService(PhotoService photoService) {
        this.photoService = photoService;
    }

    @Override
    public void createPhoto(CreatePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
        try {
            validate(request);
            PhotoEntity photoEntity = photoService.createPhoto(
                    UUID.fromString(request.getUserId()),
                    request.getSrc().getBytes(),
                    request.getCountryCode(),
                    request.getDescription()
            );
            responseObserver.onNext(PhotoEntity.toGrpcMessage(photoEntity));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void updatePhoto(UpdatePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
        try {
            validate(request);
            PhotoEntity photoEntity = photoService.updatePhoto(
                    UUID.fromString(request.getUserId()),
                    UUID.fromString(request.getPhotoId()),
                    request.getCountryCode(),
                    request.getDescription()
            );
            responseObserver.onNext(PhotoEntity.toGrpcMessage(photoEntity));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void likePhoto(LikePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
        try {
            validate(request);
            PhotoEntity photoEntity = photoService.likePhoto(
                    UUID.fromString(request.getUserId()),
                    UUID.fromString(request.getPhotoId())
            );
            responseObserver.onNext(PhotoEntity.toGrpcMessage(photoEntity));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void deletePhoto(DeletePhotoRequest request, StreamObserver<Empty> responseObserver) {
        try {
            validate(request);
            photoService.deletePhoto(
                    UUID.fromString(request.getUserId()),
                    UUID.fromString(request.getPhotoId())
            );
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getPhotos(GetPhotosRequest request, StreamObserver<PhotoSliceResponse> responseObserver) {
        try {
            validate(request);
            Slice<PhotoEntity> photoEntitySlice = photoService.getPhotos(
                    request.getUserIdList().stream().map(UUID::fromString).toList(),
                    PageRequest.of(request.getPage(), request.getSize())
            );
            responseObserver.onNext(PhotoEntity.toGrpcMessage(photoEntitySlice));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getStat(GetStatRequest request, StreamObserver<StatMapResponse> responseObserver) {
        try {
            validate(request);
            Map<String, Integer> stat = photoService.getStat(
                    request.getUserIdList().stream().map(UUID::fromString).toList()
            );
            responseObserver.onNext(StatMapResponse.newBuilder().putAllStat(stat).build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void deleteAllPhotos(DeleteAllPhotosRequest request, StreamObserver<Empty> responseObserver) {
        try {
            validate(request);
            photoService.deleteAllPhotos(UUID.fromString(request.getUserId()));
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }
}
