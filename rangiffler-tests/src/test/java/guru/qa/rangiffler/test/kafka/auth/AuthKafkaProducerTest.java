package guru.qa.rangiffler.test.kafka.auth;

import guru.qa.rangiffler.api.AuthApiClient;
import guru.qa.rangiffler.jupiter.annotation.UserForRegistration;
import guru.qa.rangiffler.kafka.KafkaService;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.kafka.BaseKafkaTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.qameta.allure.Allure.step;

@Feature("Auth")
@Story("Отправка сообщения в Kafka")
@DisplayName("Отправка сообщения в Kafka")
public class AuthKafkaProducerTest extends BaseKafkaTest {

    private final AuthApiClient authClient = new AuthApiClient();

    @Test
    @DisplayName("После регистрации [Auth] сервис должен отправить сообщение в Kafka с именем пользователя")
    void authKafkaProducerTest(@UserForRegistration UserModel user) throws InterruptedException, IOException {
        authClient.register(user.username(), user.password());
        UserModel userFromKafka = KafkaService.getMessage(user.username());
        step("Сообщение из кафки должно быть получено", () ->
                Assertions.assertNotNull(userFromKafka)
        );
        step("Сообщение должно содержать username пользователя", () ->
                Assertions.assertEquals(user.username(), userFromKafka.username())
        );
    }
}
