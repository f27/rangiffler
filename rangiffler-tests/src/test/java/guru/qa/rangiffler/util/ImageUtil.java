package guru.qa.rangiffler.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class ImageUtil {
    public static String getImageAsBase64(String imagePath) {
        String imageAsBase64;
        try (InputStream is = Objects.requireNonNull(ImageUtil.class.getClassLoader().getResourceAsStream(imagePath))) {
            imageAsBase64 = new String(Base64.getEncoder().encode(is.readAllBytes()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "data:image/jpeg;base64," + imageAsBase64;
    }
}
