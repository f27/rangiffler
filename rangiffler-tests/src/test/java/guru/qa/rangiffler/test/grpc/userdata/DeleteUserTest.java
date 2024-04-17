package guru.qa.rangiffler.test.grpc.userdata;

import guru.qa.grpc.rangiffler.grpc.Username;
import guru.qa.rangiffler.db.entity.user.UserEntity;
import guru.qa.rangiffler.db.repository.UserdataRepository;
import guru.qa.rangiffler.db.repository.hibernate.UserdataRepositoryHibernate;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.grpc.BaseGrpcTest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;
import static io.qameta.allure.Allure.step;

@Feature("USERDATA")
@Story("DeleteUser")
@DisplayName("DeleteUser")
public class DeleteUserTest extends BaseGrpcTest {
    private final UserdataRepository userdataRepository = new UserdataRepositoryHibernate();

    @Test
    @GenerateUser
    @DisplayName("DeleteUser: с правильным  username должен удалить пользователя")
    void deleteUserTest(@User(FOR_GENERATE_USER) UserModel user) {
        Username request = Username.newBuilder()
                .setUsername(user.username())
                .build();
        step("Отправить запрос и проверить, что нет исключений",
                () -> Assertions.assertDoesNotThrow(
                        () -> userdataGrpcBlockingStub.deleteUser(request))
        );
        step("Пользователя не должно быть в БД", () -> {
            Optional<UserEntity> userEntity = userdataRepository.findByUsername(user.username());
            Assertions.assertTrue(userEntity.isEmpty());
        });
    }

    @Test
    @GenerateUser
    @DisplayName("DeleteUser: с пустым username должен вернуть INVALID_ARGUMENT")
    void deleteUserEmptyUsernameTest(@User(FOR_GENERATE_USER) UserModel user) {
        Username request = Username.newBuilder()
                .setUsername("")
                .build();
        step("Проверить исключение",
                () -> {
                    Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                            () -> userdataGrpcBlockingStub.deleteUser(request)
                    );
                    Assertions.assertEquals(
                            Status.INVALID_ARGUMENT.withDescription("Username can't be empty")
                                    .asRuntimeException().getMessage(),
                            e.getMessage());
                });
        step("Пользователя должен быть в БД", () -> {
            Optional<UserEntity> userEntity = userdataRepository.findByUsername(user.username());
            Assertions.assertTrue(userEntity.isPresent());
        });
    }
}
