package guru.qa.rangiffler.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Objects;

public class ImageUtil {
    public static String getImageAsBase64(String imagePath) {
        String imageAsBase64 = Base64.getEncoder().encodeToString(getImageAsByteArray(imagePath));
        return "data:image/jpeg;base64," + imageAsBase64;
    }

    public static String imageAsByteArrayToImageAsBase64(byte[] image) {
        String imageAsBase64 = Base64.getEncoder().encodeToString(image);
        return "data:image/jpeg;base64," + imageAsBase64;
    }

    public static byte[] getImageAsByteArray(String imageClasspath) {
        try (InputStream is = Objects.requireNonNull(ImageUtil.class.getClassLoader().getResourceAsStream(imageClasspath))) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
