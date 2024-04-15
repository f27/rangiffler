package guru.qa.rangiffler.service;

import guru.qa.grpc.rangiffler.grpc.*;
import guru.qa.rangiffler.api.PhotoClient;
import guru.qa.rangiffler.api.UserdataClient;
import guru.qa.rangiffler.model.photo.PhotoInput;
import guru.qa.rangiffler.model.photo.PhotoModel;
import guru.qa.rangiffler.model.photo.StatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PhotoService {

    private final PhotoClient photoClient;
    private final UserdataClient userdataClient;

    @Autowired
    public PhotoService(PhotoClient photoClient, UserdataClient userdataClient) {
        this.photoClient = photoClient;
        this.userdataClient = userdataClient;
    }

    public PhotoModel mutatePhoto(String username, PhotoInput input) {
        String userId = userdataClient.getUser(Username.newBuilder().setUsername(username).build()).getId();
        boolean isLikePhotoMutation = input.id() != null && input.like() != null;
        if (isLikePhotoMutation) {
            LikePhotoRequest request = LikePhotoRequest.newBuilder()
                    .setUserId(userId)
                    .setPhotoId(input.id().toString())
                    .build();
            PhotoResponse response = photoClient.likePhoto(request);
            return PhotoModel.fromGrpcMessage(response, UUID.fromString(userId), username);
        }
        boolean isUpdatePhotoMutation = input.id() != null;
        if (isUpdatePhotoMutation) {
            UpdatePhotoRequest request = UpdatePhotoRequest.newBuilder()
                    .setUserId(userId)
                    .setPhotoId(input.id().toString())
                    .setCountryCode(input.country().code())
                    .setDescription(input.description())
                    .build();
            PhotoResponse response = photoClient.updatePhoto(request);
            return PhotoModel.fromGrpcMessage(response, UUID.fromString(userId), username);
        }
        CreatePhotoRequest request = CreatePhotoRequest.newBuilder()
                .setUserId(userId)
                .setSrc(input.src())
                .setCountryCode(input.country().code())
                .setDescription(input.description())
                .build();
        PhotoResponse response = photoClient.createPhoto(request);
        return PhotoModel.fromGrpcMessage(response, UUID.fromString(userId), username);
    }

    public Slice<PhotoModel> getPhotos(String username, UUID userId, List<UUID> friendsIds, int page, int size) {
        GetPhotosRequest request = GetPhotosRequest.newBuilder()
                .setPage(page)
                .setSize(size)
                .addUserId(userId.toString())
                .addAllUserId(friendsIds.stream().map(UUID::toString).toList())
                .build();
        PhotoSliceResponse response = photoClient.getPhotos(request);
        return new SliceImpl<>(
                response.getPhotosList().stream()
                        .map(photoResponse ->
                                PhotoModel.fromGrpcMessage(photoResponse, userId, username)
                        ).toList(),
                PageRequest.of(page, size),
                response.getHasNext());
    }

    public List<StatModel> getStat(UUID userId, List<UUID> friendsId) {
        GetStatRequest request = GetStatRequest.newBuilder()
                .addUserId(userId.toString())
                .addAllUserId(friendsId.stream().map(UUID::toString).toList())
                .build();
        StatMapResponse response = photoClient.getStat(request);
        return response.getStatMap().entrySet().stream()
                .map(entry -> new StatModel(entry.getValue(), entry.getKey(), null))
                .toList();
    }

    public Boolean deletePhoto(String username, UUID photoId) {
        String userId = userdataClient.getUser(Username.newBuilder().setUsername(username).build()).getId();
        DeletePhotoRequest request = DeletePhotoRequest.newBuilder()
                .setUserId(userId)
                .setPhotoId(photoId.toString())
                .build();
        photoClient.deletePhoto(request);
        return true;
    }
}
