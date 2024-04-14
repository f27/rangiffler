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
            photo.setUserId(validUUID(request.getUserId()));
            photo.setPhoto(validImageDataBase64(request.getSrc()).getBytes());
            photo.setCountryCode(validCountryCode(request.getCountryCode()));
            photo.setDescription(validDescription(request.getDescription()));
            responseObserver.onNext(PhotoEntity.toGrpcMessage(photoRepository.saveAndFlush(photo)));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void updatePhoto(UpdatePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
        try {
            photoRepository.findByUserIdAndId(validUUID(request.getUserId()), validUUID(request.getPhotoId()))
                    .ifPresentOrElse(
                            photoEntity -> {
                                photoEntity.setCountryCode(validCountryCode(request.getCountryCode()));
                                photoEntity.setDescription(validDescription(request.getDescription()));
                                responseObserver.onNext(PhotoEntity
                                        .toGrpcMessage(photoRepository.saveAndFlush(photoEntity)));
                                responseObserver.onCompleted();
                            },
                            () -> responseObserver.onError(
                                    NOT_FOUND.withDescription("Photo not found")
                                            .asRuntimeException()
                            )
                    );
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void likePhoto(LikePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
        try {
            photoRepository.findById(validUUID(request.getPhotoId()))
                    .ifPresentOrElse(
                            photoEntity -> {
                                photoEntity.addLike(validUUID(request.getUserId()));
                                responseObserver.onNext(PhotoEntity
                                        .toGrpcMessage(photoRepository.saveAndFlush(photoEntity)));
                                responseObserver.onCompleted();
                            },
                            () -> responseObserver.onError(
                                    NOT_FOUND.withDescription("Photo not found")
                                            .asRuntimeException()
                            )
                    );
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void deletePhoto(DeletePhotoRequest request, StreamObserver<Empty> responseObserver) {
        try {
            photoRepository.findByUserIdAndId(validUUID(request.getUserId()), validUUID(request.getPhotoId()))
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
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getPhotos(GetPhotosRequest request, StreamObserver<PhotoSliceResponse> responseObserver) {
        try {
            List<UUID> userIdList = request.getUserIdList().stream().map(this::validUUID).toList();
            responseObserver.onNext(
                    PhotoEntity.toGrpcMessage(
                            photoRepository
                                    .findAllByUserIdInOrderByCreatedDateDesc(userIdList,
                                            PageRequest.of(validPage(request.getPage()),
                                                    validSize(request.getSize())))));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getStat(GetStatRequest request, StreamObserver<StatMapResponse> responseObserver) {
        try {
            List<UUID> userIdList = request.getUserIdList().stream().map(this::validUUID).toList();

            List<PhotoEntity> photos = photoRepository.findAllByUserIdIn(userIdList);
            Map<String, Integer> stat = new HashMap<>();
            photos.forEach(photoModel -> {
                String countryCode = photoModel.getCountryCode();
                stat.putIfAbsent(countryCode, 0);
                stat.put(countryCode, stat.get(countryCode) + 1);
            });

            responseObserver.onNext(StatMapResponse.newBuilder().putAllStat(stat).build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    @Transactional
    public void deleteAllPhotos(DeleteAllPhotosRequest request, StreamObserver<Empty> responseObserver) {
        try {
            photoRepository.removeAllByUserId(validUUID(request.getUserId()));
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    private UUID validUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw Status.INVALID_ARGUMENT.withDescription("Bad UUID").asRuntimeException();
        }
    }

    private int validPage(int page) {
        if (page < 0) {
            throw Status.INVALID_ARGUMENT.withDescription("Bad page").asRuntimeException();
        }
        return page;
    }

    private int validSize(int size) {
        if (size < 1) {
            throw Status.INVALID_ARGUMENT.withDescription("Bad size").asRuntimeException();
        }
        return size;
    }

    private String validCountryCode(String countryCode) {
        if (countryCode.length() > 50) {
            throw Status.INVALID_ARGUMENT.withDescription("Too long country code").asRuntimeException();
        }
        if (countryCode.isEmpty()) {
            throw Status.INVALID_ARGUMENT.withDescription("Country code can't be empty").asRuntimeException();
        }
        return countryCode;
    }

    private String validDescription(String description) {
        if (description.length() > 255) {
            throw Status.INVALID_ARGUMENT.withDescription("Too long description").asRuntimeException();
        }
        return description;
    }

    private String validImageDataBase64(String src) {
        if (!src.contains("data:image")) {
            throw Status.INVALID_ARGUMENT.withDescription("Bad image").asRuntimeException();
        }
        if (src.contains("base64,")) {
            String[] splitedSrc = src.split("base64,");
            if (splitedSrc.length > 1) {
                try {
                    Base64.getDecoder().decode(splitedSrc[1]);
                    return src;
                } catch (IllegalArgumentException e) {
                    throw Status.INVALID_ARGUMENT.withDescription("Bad image").asRuntimeException();
                }
            }
        }
        throw Status.INVALID_ARGUMENT.withDescription("Bad image").asRuntimeException();
    }
}
