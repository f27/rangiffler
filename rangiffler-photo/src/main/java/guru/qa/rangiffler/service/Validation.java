package guru.qa.rangiffler.service;

import guru.qa.grpc.rangiffler.grpc.*;
import io.grpc.Status;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
public class Validation {

    public static void validate(CreatePhotoRequest request) {
        validateUUID(request.getUserId());
        validateImageDataBase64(request.getSrc());
        validateCountryCode(request.getCountryCode());
        validateDescription(request.getDescription());
    }

    public static void validate(UpdatePhotoRequest request) {
        validateUUID(request.getUserId());
        validateUUID(request.getPhotoId());
        validateCountryCode(request.getCountryCode());
        validateDescription(request.getDescription());
    }

    public static void validate(LikePhotoRequest request) {
        validateUUID(request.getUserId());
        validateUUID(request.getPhotoId());
    }

    public static void validate(DeletePhotoRequest request) {
        validateUUID(request.getUserId());
        validateUUID(request.getPhotoId());
    }

    public static void validate(GetPhotosRequest request) {
        request.getUserIdList().forEach(Validation::validateUUID);
        validatePage(request.getPage());
        validateSize(request.getSize());
    }

    public static void validate(GetStatRequest request) {
        request.getUserIdList().forEach(Validation::validateUUID);
    }

    public static void validate(DeleteAllPhotosRequest request) {
        validateUUID(request.getUserId());
    }

    private static void validateImageDataBase64(String src) {
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

    private static void validateUUID(String uuid) {
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw Status.INVALID_ARGUMENT.withDescription("Bad UUID").asRuntimeException();
        }
    }

    private static void validatePage(int page) {
        if (page < 0) {
            throw Status.INVALID_ARGUMENT.withDescription("Bad page").asRuntimeException();
        }
    }

    private static void validateSize(int size) {
        if (size < 1) {
            throw Status.INVALID_ARGUMENT.withDescription("Bad size").asRuntimeException();
        }
    }

    private static void validateCountryCode(String countryCode) {
        if (countryCode.length() > 50) {
            throw Status.INVALID_ARGUMENT.withDescription("Too long country code").asRuntimeException();
        }
        if (countryCode.isEmpty()) {
            throw Status.INVALID_ARGUMENT.withDescription("Country code can't be empty").asRuntimeException();
        }
    }

    private static void validateDescription(String description) {
        if (description.length() > 255) {
            throw Status.INVALID_ARGUMENT.withDescription("Too long description").asRuntimeException();
        }
    }
}
