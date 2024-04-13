package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.grpc.*;
import guru.qa.rangiffler.entity.PhotoEntity;
import guru.qa.rangiffler.repository.PhotoRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
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
            String photoSrc = request.getSrc();
            String photoCountryCode = request.getCountryCode();
            String photoDescription = request.getDescription();
            checkImageDataBase64(photoSrc);
            checkCountryCode(photoCountryCode);
            checkDescription(photoDescription);

            photo.setPhoto(photoSrc.getBytes());
            photo.setCountryCode(photoCountryCode);
            photo.setDescription(photoDescription);
            responseObserver.onNext(PhotoEntity.toGrpcMessage(photoRepository.saveAndFlush(photo)));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad UUID").asRuntimeException());
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void updatePhoto(UpdatePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
        try {
            UUID currentUserId = UUID.fromString(request.getUserId());
            UUID photoId = UUID.fromString(request.getPhotoId());
            String photoCountryCode = request.getCountryCode();
            String photoDescription = request.getDescription();
            checkCountryCode(photoCountryCode);
            checkDescription(photoDescription);
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
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad UUID").asRuntimeException());
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void likePhoto(LikePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
        try {
            UUID currentUserId = UUID.fromString(request.getUserId());
            UUID photoId = UUID.fromString(request.getPhotoId());

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
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad UUID").asRuntimeException());
        }
    }

    @Override
    public void deletePhoto(DeletePhotoRequest request, StreamObserver<Empty> responseObserver) {
        try {
            UUID currentUserId = UUID.fromString(request.getUserId());
            UUID photoId = UUID.fromString(request.getPhotoId());

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
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad UUID").asRuntimeException());
        }
    }

    @Override
    public void getPhotos(GetPhotosRequest request, StreamObserver<PhotoSliceResponse> responseObserver) {
        try {
            List<UUID> userIdList = request.getUserIdList().stream().map(UUID::fromString).toList();
            checkPage(request.getPage());
            checkSize(request.getSize());

            responseObserver.onNext(
                    PhotoEntity.toGrpcMessage(
                            photoRepository
                                    .findAllByUserIdInOrderByCreatedDateDesc(userIdList,
                                            PageRequest.of(request.getPage(),
                                                    request.getSize()))));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad UUID").asRuntimeException());
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getStat(GetStatRequest request, StreamObserver<StatMapResponse> responseObserver) {
        try {
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
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad UUID").asRuntimeException());
        }
    }

    @Override
    @Transactional
    public void deleteAllPhotos(DeleteAllPhotosRequest request, StreamObserver<Empty> responseObserver) {
        try {
            UUID currentUserId = UUID.fromString(request.getUserId());

            photoRepository.removeAllByUserId(currentUserId);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Bad UUID").asRuntimeException());
        }
    }

    private void checkPage(int page) {
        if (page < 0) {
            throw Status.INVALID_ARGUMENT.withDescription("Bad page").asRuntimeException();
        }
    }

    private void checkSize(int size) {
        if (size < 1) {
            throw Status.INVALID_ARGUMENT.withDescription("Bad size").asRuntimeException();
        }
    }

    private void checkCountryCode(String countryCode) {
        if (countryCode.length() > 50) {
            throw Status.INVALID_ARGUMENT.withDescription("Too long country code").asRuntimeException();
        }
    }

    private void checkDescription(String description) {
        if (description.length() > 255) {
            throw Status.INVALID_ARGUMENT.withDescription("Too long description").asRuntimeException();
        }
    }

    private void checkImageDataBase64(String src) {
        if (!src.contains("data:image")) {
            throw Status.INVALID_ARGUMENT.withDescription("Bad image").asRuntimeException();
        }
        if (src.contains("base64,")) {
            String[] splitedSrc = src.split("base64,");
            if (splitedSrc.length > 1) {
                try {
                    Base64.getDecoder().decode(splitedSrc[1]);
                    return;
                } catch (IllegalArgumentException e) {
                    throw Status.INVALID_ARGUMENT.withDescription("Bad image").asRuntimeException();
                }
            }
        }
        throw Status.INVALID_ARGUMENT.withDescription("Bad image").asRuntimeException();
    }
}
