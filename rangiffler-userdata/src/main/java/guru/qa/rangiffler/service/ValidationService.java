package guru.qa.rangiffler.service;

import guru.qa.grpc.rangiffler.grpc.FriendshipRequest;
import guru.qa.grpc.rangiffler.grpc.GrpcUser;
import guru.qa.grpc.rangiffler.grpc.Username;
import guru.qa.grpc.rangiffler.grpc.UsersRequest;
import io.grpc.Status;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
public class ValidationService {

    public void validate(Username request) {
        validateUsername(request.getUsername());
    }

    public void validate(UsersRequest request) {
        validateUsername(request.getUsername());
    }

    public void validate(GrpcUser request) {
        validateUsername(request.getUsername());
        validateFirstname(request.getFirstname());
        validateLastname(request.getLastname());
        validateImageDataBase64(request.getAvatar());
        validateCountryCode(request.getCountryCode());
    }

    public void validate(FriendshipRequest request) {
        validateUsername(request.getUsername());
        validateUUID(request.getTargetUserId());
    }

    private void validateUsername(String username) {
        if (username.isEmpty()) {
            throw Status.INVALID_ARGUMENT.withDescription("Username can't be empty").asRuntimeException();
        }
    }

    private void validateFirstname(String firstname) {
        if (firstname.length() > 50) {
            throw Status.INVALID_ARGUMENT.withDescription("Too long firstname").asRuntimeException();
        }
    }

    private void validateLastname(String lastname) {
        if (lastname.length() > 50) {
            throw Status.INVALID_ARGUMENT.withDescription("Too long lastname").asRuntimeException();
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

    private void validateImageDataBase64(String src) {
        if (src.isEmpty()) {
            return;
        }
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
}
