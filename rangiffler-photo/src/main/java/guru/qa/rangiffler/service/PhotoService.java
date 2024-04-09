package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.grpc.*;
import guru.qa.rangiffler.entity.PhotoEntity;
import guru.qa.rangiffler.repository.PhotoRepository;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.grpc.Status.NOT_FOUND;

@GrpcService
public class PhotoService extends RangifflerPhotoServiceGrpc.RangifflerPhotoServiceImplBase {

    private final PhotoRepository photoRepository;

    @Autowired
    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @Override
    public void createPhoto(CreatePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
        PhotoEntity photo = new PhotoEntity();
        photo.setUserId(UUID.fromString(request.getUserId()));
        photo.setPhoto(request.getSrc().getBytes());
        photo.setCountryCode(request.getCountryCode());
        photo.setDescription(request.getDescription());

        responseObserver.onNext(PhotoEntity.toGrpcMessage(photoRepository.saveAndFlush(photo)));
        responseObserver.onCompleted();
    }

    @Override
    public void updatePhoto(UpdatePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
        photoRepository.findByUserIdAndId(UUID.fromString(request.getUserId()), UUID.fromString(request.getPhotoId()))
                .ifPresentOrElse(
                        photoEntity -> {
                            photoEntity.setCountryCode(request.getCountryCode());
                            photoEntity.setDescription(request.getDescription());
                            responseObserver.onNext(PhotoEntity
                                    .toGrpcMessage(photoRepository.saveAndFlush(photoEntity)));
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(
                                NOT_FOUND.withDescription("Photo not found")
                                        .asRuntimeException()
                        )
                );
    }

    @Override
    public void likePhoto(LikePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
        photoRepository.findById(UUID.fromString(request.getPhotoId()))
                .ifPresentOrElse(
                        photoEntity -> {
                            photoEntity.addLike(UUID.fromString(request.getUserId()));
                            responseObserver.onNext(PhotoEntity
                                    .toGrpcMessage(photoRepository.saveAndFlush(photoEntity)));
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(
                                NOT_FOUND.withDescription("Photo not found")
                                        .asRuntimeException()
                        )
                );
    }

    @Override
    public void deletePhoto(DeletePhotoRequest request, StreamObserver<Empty> responseObserver) {
        photoRepository.findByUserIdAndId(UUID.fromString(request.getUserId()), UUID.fromString(request.getPhotoId()))
                .ifPresentOrElse(
                        photoEntity -> {
                            photoRepository.delete(photoEntity);
                            responseObserver.onNext(Empty.getDefaultInstance());
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(
                                NOT_FOUND.withDescription("Photo not found")
                                        .asRuntimeException()
                        )
                );
    }

    @Override
    public void getPhotos(GetPhotosRequest request, StreamObserver<PhotoSliceResponse> responseObserver) {
        List<UUID> userIdList = request.getUserIdList().stream().map(UUID::fromString).toList();

        responseObserver.onNext(
                PhotoEntity.toGrpcMessage(
                        photoRepository
                                .findAllByUserIdInOrderByCreatedDateDesc(userIdList,
                                        PageRequest.of(request.getPage(),
                                                request.getSize()))));
        responseObserver.onCompleted();
    }

    @Override
    public void getStat(GetStatRequest request, StreamObserver<StatMapResponse> responseObserver) {
        List<UUID> userIdList = request.getUserIdList().stream().map(UUID::fromString).toList();

        List<PhotoEntity> photos = photoRepository.findAllByUserIdIn(userIdList);
        Map<String, Integer> stat = new HashMap<>();
        photos.forEach(photoModel -> {
            String countryCode = photoModel.getCountryCode();
            stat.putIfAbsent(countryCode, 0);
            stat.put(countryCode, stat.get(countryCode) + 1);
        });

        responseObserver.onNext(StatMapResponse.newBuilder().putAllStat(stat).build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void deleteAllPhotos(DeleteAllPhotosRequest request, StreamObserver<Empty> responseObserver) {
        photoRepository.removeAllByUserId(UUID.fromString(request.getUserId()));
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
