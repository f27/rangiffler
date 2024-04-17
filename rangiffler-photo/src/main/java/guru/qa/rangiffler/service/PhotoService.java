package guru.qa.rangiffler.service;

import guru.qa.rangiffler.entity.PhotoEntity;
import guru.qa.rangiffler.repository.PhotoRepository;
import io.grpc.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;

    @Autowired
    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @Transactional
    public PhotoEntity createPhoto(UUID userId, byte[] image, String countryCode, String description) {
        PhotoEntity photo = new PhotoEntity();
        photo.setUserId(userId);
        photo.setPhoto(image);
        photo.setCountryCode(countryCode);
        photo.setDescription(description);
        return photoRepository.saveAndFlush(photo);
    }

    @Transactional
    public PhotoEntity updatePhoto(UUID userId, UUID photoId, String countryCode, String description) {
        PhotoEntity photo = photoRepository.findByUserIdAndId(userId, photoId).orElseThrow(
                () -> Status.NOT_FOUND.withDescription("Photo not found").asRuntimeException());
        photo.setCountryCode(countryCode);
        photo.setDescription(description);
        return photoRepository.saveAndFlush(photo);
    }

    @Transactional
    public PhotoEntity likePhoto(UUID userId, UUID photoId) {
        PhotoEntity photo = photoRepository.findById(photoId).orElseThrow(
                () -> Status.NOT_FOUND.withDescription("Photo not found").asRuntimeException());
        photo.addLike(userId);
        return photoRepository.saveAndFlush(photo);
    }

    @Transactional
    public void deletePhoto(UUID userId, UUID photoId) {
        PhotoEntity photo = photoRepository.findByUserIdAndId(userId, photoId).orElseThrow(
                () -> Status.NOT_FOUND.withDescription("Photo not found").asRuntimeException());
        photoRepository.delete(photo);
    }

    @Transactional(readOnly = true)
    public Slice<PhotoEntity> getPhotos(List<UUID> userIdList, Pageable pageable) {
        return photoRepository.findAllByUserIdInOrderByCreatedDateDesc(userIdList, pageable);
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> getStat(List<UUID> userIdList) {
        List<PhotoEntity> photos = photoRepository.findAllByUserIdIn(userIdList);
        Map<String, Integer> stat = new HashMap<>();
        photos.forEach(photoModel -> {
            String countryCode = photoModel.getCountryCode();
            stat.putIfAbsent(countryCode, 0);
            stat.put(countryCode, stat.get(countryCode) + 1);
        });
        return stat;
    }

    @Transactional
    public void deleteAllPhotos(UUID userId) {
        photoRepository.removeAllByUserId(userId);
    }
}
