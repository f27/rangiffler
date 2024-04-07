package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.grpc.*;
import guru.qa.rangiffler.api.UserDataClient;
import guru.qa.rangiffler.entity.PhotoEntity;
import guru.qa.rangiffler.repository.PhotoRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static io.grpc.Status.NOT_FOUND;

@GrpcService
public class PhotoService extends RangifflerPhotoServiceGrpc.RangifflerPhotoServiceImplBase {

    private final PhotoRepository photoRepository;
    private final UserDataClient userDataClient;

    @Autowired
    public PhotoService(PhotoRepository photoRepository, UserDataClient userDataClient) {
        this.photoRepository = photoRepository;
        this.userDataClient = userDataClient;
    }

    @Override
    public void createPhoto(CreatePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
        User currentUser = userDataClient.currentUser(request.getUsername());
        PhotoEntity photo = new PhotoEntity();
        photo.setUserId(UUID.fromString(currentUser.getId()));
        photo.setPhoto(request.getSrc().getBytes());
        photo.setCountryCode(request.getCountryCode());
        photo.setDescription(request.getDescription());

        responseObserver.onNext(PhotoEntity.toGrpcMessage(photoRepository.saveAndFlush(photo), UUID.fromString(currentUser.getId())));
        responseObserver.onCompleted();
    }

    @Override
    public void updatePhoto(UpdatePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
        User currentUser = userDataClient.currentUser(request.getUsername());
        photoRepository.findByUserIdAndId(UUID.fromString(currentUser.getId()), UUID.fromString(request.getPhotoId()))
                .ifPresentOrElse(
                        photoEntity -> {
                            photoEntity.setCountryCode(request.getCountryCode());
                            photoEntity.setDescription(request.getDescription());
                            responseObserver.onNext(PhotoEntity
                                    .toGrpcMessage(photoRepository.saveAndFlush(photoEntity),
                                            UUID.fromString(currentUser.getId())));
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
        UUID currentUserId = UUID.fromString(userDataClient.currentUser(request.getUsername()).getId());
        photoRepository.findById(UUID.fromString(request.getPhotoId()))
                .ifPresentOrElse(
                        photoEntity -> {
                            photoEntity.addLike(currentUserId);
                            responseObserver.onNext(PhotoEntity
                                    .toGrpcMessage(photoRepository.saveAndFlush(photoEntity), currentUserId));
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
        UUID currentUserId = UUID.fromString(userDataClient.currentUser(request.getUsername()).getId());
        photoRepository.findByUserIdAndId(currentUserId, UUID.fromString(request.getPhotoId()))
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
    public void getPhotos(GetPhotosRequest request, StreamObserver<GetPhotosResponse> responseObserver) {
        String username = request.getUsername();
        User currentUser = userDataClient.currentUser(username);
        List<UUID> userIds = new ArrayList<>();
        userIds.add(UUID.fromString(currentUser.getId()));

        if (request.getWithFriends()) {
            userIds.addAll(
                    userDataClient.getFriends(username).getUsersList().stream()
                            .map(User::getId)
                            .map(UUID::fromString)
                            .toList()
            );
        }

        responseObserver.onNext(
                PhotoEntity.toGrpcMessage(
                        photoRepository
                                .findAllByUserIdInOrderByCreatedDateDesc(userIds,
                                        PageRequest.of(request.getPage(),
                                                request.getSize())),
                        UUID.fromString(currentUser.getId())));
        responseObserver.onCompleted();
    }

    @Override
    public void getStat(GetStatRequest request, StreamObserver<GetStatResponse> responseObserver) {
        String username = request.getUsername();
        User currentUser = userDataClient.currentUser(username);
        List<UUID> userIds = new ArrayList<>();
        userIds.add(UUID.fromString(currentUser.getId()));

        if (request.getWithFriends()) {
            userIds.addAll(
                    userDataClient.getFriends(username).getUsersList().stream()
                            .map(User::getId)
                            .map(UUID::fromString)
                            .toList()
            );
        }

        List<PhotoEntity> photos = photoRepository.findAllByUserIdIn(userIds);
        Map<String, Integer> stat = new HashMap<>();
        photos.forEach(photoModel -> {
            String countryCode = photoModel.getCountryCode();
            stat.putIfAbsent(countryCode, 0);
            stat.put(countryCode, stat.get(countryCode) + 1);
        });

        responseObserver.onNext(GetStatResponse.newBuilder().putAllStat(stat).build());
        responseObserver.onCompleted();
    }
}
