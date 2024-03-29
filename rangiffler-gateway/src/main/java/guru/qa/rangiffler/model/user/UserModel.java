package guru.qa.rangiffler.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rangiffler.grpc.FriendStatus;
import guru.qa.grpc.rangiffler.grpc.User;
import guru.qa.rangiffler.model.country.CountryModel;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Slice;

import java.util.UUID;

public record UserModel(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        @Size(max = 30, message = "First name can`t be longer than 30 characters")
        String firstname,
        @JsonProperty("surname")
        @Size(max = 50, message = "Surname can`t be longer than 50 characters")
        String surname,
        @JsonProperty("avatar")
        String avatar,
        @JsonProperty("country_code")
        String countryCode,
        @JsonProperty("location")
        CountryModel location,
        @JsonProperty("friendStatus")
        FriendStatus friendStatus,
        @JsonProperty("friends")
        Slice<UserModel> friends,
        @JsonProperty("incomeInvitations")
        Slice<UserModel> incomeInvitations,
        @JsonProperty("outcomeInvitations")
        Slice<UserModel> outcomeInvitations

) {
    public static UserModel fromGrpcMessage(User user) {
        return new UserModel(
                UUID.fromString(user.getId()),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getAvatar(),
                user.getCountryCode(),
                null,
                user.getFriendStatus() != FriendStatus.NOT_FRIEND ? user.getFriendStatus() : null,
                null,
                null,
                null
        );
    }
}
