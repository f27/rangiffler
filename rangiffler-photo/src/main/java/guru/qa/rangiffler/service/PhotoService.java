package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.grpc.*;
import guru.qa.rangiffler.entity.PhotoEntity;
import guru.qa.rangiffler.repository.PhotoRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.*;

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
        try {
            photo.setUserId(UUID.fromString(request.getUserId()));
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad user id").asRuntimeException());
            return;
        }

        String photoSrc = request.getSrc();
        if (!isCorrectImageDataBase64(photoSrc)) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad image").asRuntimeException());
            return;
        }
        String photoCountryCode = request.getCountryCode();
        if (isBadCountryCode(photoCountryCode)) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Too long country code").asRuntimeException());
            return;
        }
        String photoDescription = request.getDescription();
        if (isBadDescription(photoDescription)) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Too long description").asRuntimeException());
            return;
        }
        photo.setPhoto(photoSrc.getBytes());
        photo.setCountryCode(photoCountryCode);
        photo.setDescription(photoDescription);

        responseObserver.onNext(PhotoEntity.toGrpcMessage(photoRepository.saveAndFlush(photo)));
        responseObserver.onCompleted();
    }

    @Override
    public void updatePhoto(UpdatePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
        UUID currentUserId;
        try {
            currentUserId = UUID.fromString(request.getUserId());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad user id").asRuntimeException());
            return;
        }

        UUID photoId;
        try {
            photoId = UUID.fromString(request.getPhotoId());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad photo id").asRuntimeException());
            return;
        }

        String photoCountryCode = request.getCountryCode();
        if (isBadCountryCode(photoCountryCode)) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Too long country code").asRuntimeException());
            return;
        }
        String photoDescription = request.getDescription();
        if (isBadDescription(photoDescription)) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Too long description").asRuntimeException());
            return;
        }

        photoRepository.findByUserIdAndId(currentUserId, photoId)
                .ifPresentOrElse(
                        photoEntity -> {
                            photoEntity.setCountryCode(photoCountryCode);
                            photoEntity.setDescription(photoDescription);
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
        UUID currentUserId;
        try {
            currentUserId = UUID.fromString(request.getUserId());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad user id").asRuntimeException());
            return;
        }

        UUID photoId;
        try {
            photoId = UUID.fromString(request.getPhotoId());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad photo id").asRuntimeException());
            return;
        }
        photoRepository.findById(photoId)
                .ifPresentOrElse(
                        photoEntity -> {
                            photoEntity.addLike(currentUserId);
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
        UUID currentUserId;
        try {
            currentUserId = UUID.fromString(request.getUserId());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad user id").asRuntimeException());
            return;
        }

        UUID photoId;
        try {
            photoId = UUID.fromString(request.getPhotoId());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad photo id").asRuntimeException());
            return;
        }
        photoRepository.findByUserIdAndId(currentUserId, photoId)
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
        List<UUID> userIdList;
        try {
            userIdList = request.getUserIdList().stream().map(UUID::fromString).toList();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad user id").asRuntimeException());
            return;
        }
        if (request.getPage() < 0) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad page").asRuntimeException());
            return;
        }
        if (request.getSize() < 1) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad size").asRuntimeException());
            return;
        }
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
        List<UUID> userIdList;
        try {
            userIdList = request.getUserIdList().stream().map(UUID::fromString).toList();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad user id").asRuntimeException());
            return;
        }

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
        UUID currentUserId;
        try {
            currentUserId = UUID.fromString(request.getUserId());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad user id").asRuntimeException());
            return;
        }

        photoRepository.removeAllByUserId(currentUserId);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    private boolean isBadCountryCode(String countryCode) {
        return countryCode.length() > 50;
    }

    private boolean isBadDescription(String description) {
        return description.length() > 255;
    }

    private boolean isCorrectImageDataBase64(String src) {
        if (!src.contains("data:image")) {
            return false;
        }
        if (src.contains("base64,")) {
            String[] splitedSrc = src.split("base64,");
            if (splitedSrc.length > 1) {
                try {
                    Base64.getDecoder().decode(splitedSrc[1]);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }
        }
        return false;
    }
}
