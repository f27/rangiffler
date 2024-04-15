package guru.qa.rangiffler.service;

import guru.qa.grpc.rangiffler.grpc.*;
import io.grpc.Status;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
public class ValidationService {

    public void validate(CreatePhotoRequest request) {
        validateUUID(request.getUserId());
        validateImageDataBase64(request.getSrc());
        validateCountryCode(request.getCountryCode());
        validateDescription(request.getDescription());
    }

    public void validate(UpdatePhotoRequest request) {
        validateUUID(request.getUserId());
        validateUUID(request.getPhotoId());
        validateCountryCode(request.getCountryCode());
        validateDescription(request.getDescription());
    }

    public void validate(LikePhotoRequest request) {
        validateUUID(request.getUserId());
        validateUUID(request.getPhotoId());
    }

    public void validate(DeletePhotoRequest request) {
        validateUUID(request.getUserId());
        validateUUID(request.getPhotoId());
    }

    public void validate(GetPhotosRequest request) {
        request.getUserIdList().forEach(this::validateUUID);
        validatePage(request.getPage());
        validateSize(request.getSize());
    }

    public void validate(GetStatRequest request) {
        request.getUserIdList().forEach(this::validateUUID);
    }

    public void validate(DeleteAllPhotosRequest request) {
        validateUUID(request.getUserId());
    }

    private void validateImageDataBase64(String src) {
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

    private void validateUUID(String uuid) {
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw Status.INVALID_ARGUMENT.withDescription("Bad UUID").asRuntimeException();
        }
    }

    private void validatePage(int page) {
        if (page < 0) {
            throw Status.INVALID_ARGUMENT.withDescription("Bad page").asRuntimeException();
        }
    }

    private void validateSize(int size) {
        if (size < 1) {
            throw Status.INVALID_ARGUMENT.withDescription("Bad size").asRuntimeException();
        }
    }

    private void validateCountryCode(String countryCode) {
        if (countryCode.length() > 50) {
            throw Status.INVALID_ARGUMENT.withDescription("Too long country code").asRuntimeException();
        }
        if (countryCode.isEmpty()) {
            throw Status.INVALID_ARGUMENT.withDescription("Country code can't be empty").asRuntimeException();
        }
    }

    private void validateDescription(String description) {
        if (description.length() > 255) {
            throw Status.INVALID_ARGUMENT.withDescription("Too long description").asRuntimeException();
        }
    }
}
