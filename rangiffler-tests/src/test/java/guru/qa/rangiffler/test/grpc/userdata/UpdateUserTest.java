package guru.qa.rangiffler.test.grpc.userdata;

import guru.qa.grpc.rangiffler.grpc.GrpcUser;
import guru.qa.rangiffler.db.entity.user.UserEntity;
import guru.qa.rangiffler.db.repository.UserdataRepository;
import guru.qa.rangiffler.db.repository.hibernate.UserdataRepositoryHibernate;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.grpc.BaseGrpcTest;
import guru.qa.rangiffler.util.DataUtil;
import guru.qa.rangiffler.util.ImageUtil;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;
import static io.qameta.allure.Allure.step;

@Feature("USERDATA")
@Story("UpdateUser")
@DisplayName("UpdateUser")
public class UpdateUserTest extends BaseGrpcTest {
    private final UserdataRepository userdataRepository = new UserdataRepositoryHibernate();

    @Test
    @GenerateUser
    @DisplayName("UpdateUser: можно изменить firstname, lastname, avatar, countryCode")
    void updateUserChangeFirstnameAndLastnameAndAvatarAndCountryCodeTest(@User(FOR_GENERATE_USER) UserModel user) {
        String newFirstname = DataUtil.generateRandomFirstname();
        String newLastname = DataUtil.generateRandomLastname();
        String newAvatar = ImageUtil.getImageAsBase64("img/avatar/2.jpg");
        String newCountryCode = CountryEnum.getRandom().getCode();
        GrpcUser request = GrpcUser.newBuilder()
                .setUsername(user.username())
                .setFirstname(newFirstname)
                .setLastname(newLastname)
                .setAvatar(newAvatar)
                .setCountryCode(newCountryCode)
                .build();

        GrpcUser response = userdataGrpcBlockingStub.updateUser(request);

        step("Проверить ответ",
                () -> {
                    step("id", () -> Assertions.assertEquals(user.id(), UUID.fromString(response.getId())));
                    step("username", () -> Assertions.assertEquals(user.username(), response.getUsername()));
                    step("firstname", () -> Assertions.assertEquals(newFirstname, response.getFirstname()));
                    step("lastname", () -> Assertions.assertEquals(newLastname, response.getLastname()));
                    step("avatar", () -> Assertions.assertEquals(newAvatar, response.getAvatar()));
                    step("countryCode", () -> Assertions.assertEquals(newCountryCode, response.getCountryCode()));
                });
        step("Проверить БД",
                () -> {
                    UserEntity userFromDB = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("id", () -> Assertions.assertEquals(user.id(), userFromDB.getId()));
                    step("username", () -> Assertions.assertEquals(user.username(), userFromDB.getUsername()));
                    step("firstname", () -> Assertions.assertEquals(newFirstname, userFromDB.getFirstname()));
                    step("lastname", () -> Assertions.assertEquals(newLastname, userFromDB.getLastname()));
                    step("avatar", () -> Assertions.assertEquals(
                            newAvatar,
                            new String(userFromDB.getAvatar(), StandardCharsets.UTF_8)));
                    step("countryCode", () -> Assertions.assertEquals(newCountryCode, userFromDB.getCountryCode()));
                });
    }

    @Test
    @GenerateUser
    @DisplayName("UpdateUser: изменение id должно игнорироваться")
    void updateUserShouldNotChangeIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        GrpcUser request = GrpcUser.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setUsername(user.username())
                .setFirstname(user.firstname())
                .setLastname(user.lastname())
                .setAvatar(user.getAvatarAsBase64())
                .setCountryCode(user.country().getCode())
                .build();

        GrpcUser response = userdataGrpcBlockingStub.updateUser(request);

        step("Проверить ответ",
                () -> {
                    step("id", () -> Assertions.assertEquals(user.id(), UUID.fromString(response.getId())));
                    step("username", () -> Assertions.assertEquals(user.username(), response.getUsername()));
                    step("firstname", () -> Assertions.assertEquals(user.firstname(), response.getFirstname()));
                    step("lastname", () -> Assertions.assertEquals(user.lastname(), response.getLastname()));
                    step("avatar", () -> Assertions.assertEquals(user.getAvatarAsBase64(), response.getAvatar()));
                    step("countryCode", () -> Assertions.assertEquals(user.country().getCode(), response.getCountryCode()));
                });
        step("Проверить БД",
                () -> {
                    UserEntity userFromDB = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("id", () -> Assertions.assertEquals(user.id(), userFromDB.getId()));
                    step("username", () -> Assertions.assertEquals(user.username(), userFromDB.getUsername()));
                    step("firstname", () -> Assertions.assertEquals(user.firstname(), userFromDB.getFirstname()));
                    step("lastname", () -> Assertions.assertEquals(user.lastname(), userFromDB.getLastname()));
                    step("avatar", () -> Assertions.assertEquals(
                            user.getAvatarAsBase64(),
                            new String(userFromDB.getAvatar(), StandardCharsets.UTF_8)));
                    step("countryCode", () -> Assertions.assertEquals(user.country().getCode(), userFromDB.getCountryCode()));
                });
    }

    @Test
    @GenerateUser
    @DisplayName("UpdateUser: нельзя изменить firstname на слишком длинный")
    void updateUserShouldNotChangeFirstnameIfTooLongTest(@User(FOR_GENERATE_USER) UserModel user) {
        GrpcUser request = GrpcUser.newBuilder()
                .setUsername(user.username())
                .setFirstname(DataUtil.generateStringWithLength(51))
                .setLastname(user.lastname())
                .setAvatar(user.getAvatarAsBase64())
                .setCountryCode(user.country().getCode())
                .build();

        step("Проверить исключение",
                () -> {
                    StatusRuntimeException e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.updateUser(request)
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Too long firstname").asRuntimeException().getMessage(),
                            e.getMessage());
                });
        step("Проверить БД",
                () -> {
                    UserEntity userFromDB = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("id", () -> Assertions.assertEquals(user.id(), userFromDB.getId()));
                    step("username", () -> Assertions.assertEquals(user.username(), userFromDB.getUsername()));
                    step("firstname", () -> Assertions.assertEquals(user.firstname(), userFromDB.getFirstname()));
                    step("lastname", () -> Assertions.assertEquals(user.lastname(), userFromDB.getLastname()));
                    step("avatar", () -> Assertions.assertEquals(
                            user.getAvatarAsBase64(),
                            new String(userFromDB.getAvatar(), StandardCharsets.UTF_8)));
                    step("countryCode", () -> Assertions.assertEquals(user.country().getCode(), userFromDB.getCountryCode()));
                });
    }

    @Test
    @GenerateUser
    @DisplayName("UpdateUser: нельзя изменить lastname на слишком длинный")
    void updateUserShouldNotChangeLastnameIfTooLongTest(@User(FOR_GENERATE_USER) UserModel user) {
        GrpcUser request = GrpcUser.newBuilder()
                .setUsername(user.username())
                .setFirstname(user.firstname())
                .setLastname(DataUtil.generateStringWithLength(51))
                .setAvatar(user.getAvatarAsBase64())
                .setCountryCode(user.country().getCode())
                .build();

        step("Проверить исключение",
                () -> {
                    StatusRuntimeException e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.updateUser(request)
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Too long lastname").asRuntimeException().getMessage(),
                            e.getMessage());
                });
        step("Проверить БД",
                () -> {
                    UserEntity userFromDB = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("id", () -> Assertions.assertEquals(user.id(), userFromDB.getId()));
                    step("username", () -> Assertions.assertEquals(user.username(), userFromDB.getUsername()));
                    step("firstname", () -> Assertions.assertEquals(user.firstname(), userFromDB.getFirstname()));
                    step("lastname", () -> Assertions.assertEquals(user.lastname(), userFromDB.getLastname()));
                    step("avatar", () -> Assertions.assertEquals(
                            user.getAvatarAsBase64(),
                            new String(userFromDB.getAvatar(), StandardCharsets.UTF_8)));
                    step("countryCode", () -> Assertions.assertEquals(user.country().getCode(), userFromDB.getCountryCode()));
                });
    }

    @Test
    @GenerateUser
    @DisplayName("UpdateUser: нельзя изменить avatar на некорректный")
    void updateUserShouldNotChangeAvatarIfIncorrectTest(@User(FOR_GENERATE_USER) UserModel user) {
        GrpcUser request = GrpcUser.newBuilder()
                .setUsername(user.username())
                .setFirstname(user.firstname())
                .setLastname(user.lastname())
                .setAvatar("http://evil/saveIp")
                .setCountryCode(user.country().getCode())
                .build();

        step("Проверить исключение",
                () -> {
                    StatusRuntimeException e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.updateUser(request)
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Bad image").asRuntimeException().getMessage(),
                            e.getMessage());
                });
        step("Проверить БД",
                () -> {
                    UserEntity userFromDB = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("id", () -> Assertions.assertEquals(user.id(), userFromDB.getId()));
                    step("username", () -> Assertions.assertEquals(user.username(), userFromDB.getUsername()));
                    step("firstname", () -> Assertions.assertEquals(user.firstname(), userFromDB.getFirstname()));
                    step("lastname", () -> Assertions.assertEquals(user.lastname(), userFromDB.getLastname()));
                    step("avatar", () -> Assertions.assertEquals(
                            user.getAvatarAsBase64(),
                            new String(userFromDB.getAvatar(), StandardCharsets.UTF_8)));
                    step("countryCode", () -> Assertions.assertEquals(user.country().getCode(), userFromDB.getCountryCode()));
                });
    }

    @Test
    @GenerateUser
    @DisplayName("UpdateUser: нельзя изменить countryCode на слишком длинный")
    void updateUserShouldNotChangeCountryCodeIfTooLongTest(@User(FOR_GENERATE_USER) UserModel user) {
        GrpcUser request = GrpcUser.newBuilder()
                .setUsername(user.username())
                .setFirstname(user.firstname())
                .setLastname(user.lastname())
                .setAvatar(user.avatar())
                .setCountryCode(DataUtil.generateStringWithLength(51))
                .build();

        step("Проверить исключение",
                () -> {
                    StatusRuntimeException e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.updateUser(request)
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Too long country code").asRuntimeException().getMessage(),
                            e.getMessage());
                });
        step("Проверить БД",
                () -> {
                    UserEntity userFromDB = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("id", () -> Assertions.assertEquals(user.id(), userFromDB.getId()));
                    step("username", () -> Assertions.assertEquals(user.username(), userFromDB.getUsername()));
                    step("firstname", () -> Assertions.assertEquals(user.firstname(), userFromDB.getFirstname()));
                    step("lastname", () -> Assertions.assertEquals(user.lastname(), userFromDB.getLastname()));
                    step("avatar", () -> Assertions.assertEquals(
                            user.getAvatarAsBase64(),
                            new String(userFromDB.getAvatar(), StandardCharsets.UTF_8)));
                    step("countryCode", () -> Assertions.assertEquals(user.country().getCode(), userFromDB.getCountryCode()));
                });
    }

    @Test
    @GenerateUser
    @DisplayName("UpdateUser: нельзя изменить countryCode на пустой")
    void updateUserShouldNotChangeCountryCodeIfEmptyTest(@User(FOR_GENERATE_USER) UserModel user) {
        GrpcUser request = GrpcUser.newBuilder()
                .setUsername(user.username())
                .setFirstname(user.firstname())
                .setLastname(user.lastname())
                .setAvatar(user.avatar())
                .build();

        step("Проверить исключение",
                () -> {
                    StatusRuntimeException e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.updateUser(request)
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Country code can't be empty").asRuntimeException().getMessage(),
                            e.getMessage());
                });
        step("Проверить БД",
                () -> {
                    UserEntity userFromDB = userdataRepository.findByUsername(user.username()).orElseThrow();
                    step("id", () -> Assertions.assertEquals(user.id(), userFromDB.getId()));
                    step("username", () -> Assertions.assertEquals(user.username(), userFromDB.getUsername()));
                    step("firstname", () -> Assertions.assertEquals(user.firstname(), userFromDB.getFirstname()));
                    step("lastname", () -> Assertions.assertEquals(user.lastname(), userFromDB.getLastname()));
                    step("avatar", () -> Assertions.assertEquals(
                            user.getAvatarAsBase64(),
                            new String(userFromDB.getAvatar(), StandardCharsets.UTF_8)));
                    step("countryCode", () -> Assertions.assertEquals(user.country().getCode(), userFromDB.getCountryCode()));
                });
    }

    @Test
    @GenerateUser
    @DisplayName("UpdateUser: пустой username должен возвращать INVALID_ARGUMENT")
    void updateUserWithEmptyUsernameTest() {
        step("Проверить исключение",
                () -> {
                    StatusRuntimeException e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.updateUser(GrpcUser.newBuilder().setUsername("").build())
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Username can't be empty").asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }

    @Test
    @GenerateUser
    @DisplayName("UpdateUser: некорректный username должен возвращать NOT_FOUND")
    void updateUserWithIncorrectUsernameTest() {
        step("Проверить исключение",
                () -> {
                    StatusRuntimeException e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.updateUser(GrpcUser.newBuilder()
                                    .setCountryCode(CountryEnum.getRandom().getCode())
                                    .setUsername(".").build())
                    );
                    Assertions.assertEquals(
                            Status.NOT_FOUND.withDescription("User not found").asRuntimeException().getMessage(),
                            e.getMessage());
                });
    }
}
