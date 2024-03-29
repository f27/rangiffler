package guru.qa.rangiffler.api;

import guru.qa.grpc.rangiffler.grpc.*;
import guru.qa.rangiffler.model.photo.PhotoInput;
import guru.qa.rangiffler.model.photo.PhotoModel;
import guru.qa.rangiffler.model.photo.StatModel;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PhotoClient {

    @GrpcClient("photoClient")
    private RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub rangifflerPhotoServiceBlockingStub;

    public @Nonnull PhotoModel mutatePhoto(@Nonnull String username,
                                           @Nonnull PhotoInput input) {
        PhotoResponse response;
        if (input.id() != null) {
            if (input.like() != null) {
                LikePhotoRequest request = LikePhotoRequest.newBuilder()
                        .setUsername(username)
                        .setPhotoId(input.id().toString())
                        .build();
                response = rangifflerPhotoServiceBlockingStub.likePhoto(request);
            } else {
                UpdatePhotoRequest request = UpdatePhotoRequest.newBuilder()
                        .setUsername(username)
                        .setPhotoId(input.id().toString())
                        .setCountryCode(input.country().code())
                        .setDescription(input.description())
                        .build();
                response = rangifflerPhotoServiceBlockingStub.updatePhoto(request);
            }
        } else {
            CreatePhotoRequest request = CreatePhotoRequest.newBuilder()
                    .setUsername(username)
                    .setSrc(input.src())
                    .setCountryCode(input.country().code())
                    .setDescription(input.description())
                    .build();
            response = rangifflerPhotoServiceBlockingStub.createPhoto(request);
        }
        return PhotoModel.fromGrpcMessage(response);
    }

    public @Nonnull Slice<PhotoModel> getPhotos(@Nonnull String username,
                                                @Nonnull Boolean withFriends,
                                                int page,
                                                int size) {
        GetPhotosRequest request = GetPhotosRequest.newBuilder()
                .setUsername(username)
                .setWithFriends(withFriends)
                .setPage(page)
                .setSize(size)
                .build();
        GetPhotosResponse response = rangifflerPhotoServiceBlockingStub.getPhotos(request);
        return new SliceImpl<>(
                response.getPhotosList().stream().map(PhotoModel::fromGrpcMessage).toList(),
                PageRequest.of(page, size),
                response.getHasNext());
    }

    public @Nonnull List<StatModel> getStat(@Nonnull String username, @Nonnull Boolean withFriends) {
        GetStatRequest request = GetStatRequest.newBuilder()
                .setUsername(username)
                .setWithFriends(withFriends)
                .build();
        return rangifflerPhotoServiceBlockingStub.getStat(request).getStatMap().entrySet().stream()
                .map(entry -> new StatModel(entry.getValue(), entry.getKey(), null))
                .toList();
    }

    public @Nonnull Boolean deletePhoto(@Nonnull String username, @Nonnull UUID photoId) {
        DeletePhotoRequest request = DeletePhotoRequest.newBuilder()
                .setUsername(username)
                .setPhotoId(photoId.toString())
                .build();
        try {
            rangifflerPhotoServiceBlockingStub.deletePhoto(request);
            return true;
        } catch (Throwable t) {
            throw new RuntimeException(t.getMessage());
        }
    }
}
