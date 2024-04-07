package guru.qa.rangiffler.model;

import guru.qa.rangiffler.util.ImageUtil;

import java.util.UUID;

public record PhotoModel(
        UUID id,
        CountryEnum country,
        String description,
        String photo
) {
    public static PhotoModel create(String photoClasspath) {
        return new PhotoModel(
                null,
                CountryEnum.RUSSIAN_FEDERATION,
                "",
                photoClasspath
        );
    }

    public static PhotoModel create(String photoClasspath, String description) {
        return new PhotoModel(
                null,
                CountryEnum.RUSSIAN_FEDERATION,
                description,
                photoClasspath
        );
    }

    public static PhotoModel create(String photoClasspath, CountryEnum country) {
        return new PhotoModel(
                null,
                country,
                "",
                photoClasspath
        );
    }

    public static PhotoModel create(String photoClasspath, String description, CountryEnum country) {
        return new PhotoModel(
                null,
                country,
                description,
                photoClasspath
        );
    }

    public String getPhotoAsBase64() {
        return ImageUtil.getImageAsBase64(photo);
    }
}
