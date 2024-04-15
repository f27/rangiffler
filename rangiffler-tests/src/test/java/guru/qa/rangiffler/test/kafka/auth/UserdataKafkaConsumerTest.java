package guru.qa.rangiffler.test.kafka.auth;

import guru.qa.rangiffler.db.entity.user.UserEntity;
import guru.qa.rangiffler.db.repository.UserdataRepository;
import guru.qa.rangiffler.db.repository.hibernate.UserdataRepositoryHibernate;
import guru.qa.rangiffler.jupiter.annotation.UserForRegistration;
import guru.qa.rangiffler.kafka.KafkaProducerForUserdata;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.kafka.BaseKafkaTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.codeborne.selenide.Selenide.sleep;
import static io.qameta.allure.Allure.step;

@Feature("Userdata")
@Story("Получение сообщения из Kafka")
@DisplayName("Получение сообщения из Kafka")
public class UserdataKafkaConsumerTest extends BaseKafkaTest {
    UserdataRepository userdataRepository = new UserdataRepositoryHibernate();

    @Test
    @DisplayName("После получения сообщения из Kafka [Userdata] должна создать запись в БД")
    void userdataKafkaConsumerTest(@UserForRegistration UserModel user) throws ExecutionException, InterruptedException {
        step("Отправить сообщение в Kafka", () -> KafkaProducerForUserdata.sendMessage(user));
        Optional<UserEntity> userInUserdata = Optional.empty();
        for (int i = 1; i <= 20; i++) {
            userInUserdata = userdataRepository.findByUsername(user.username());
            if (userInUserdata.isPresent())
                break;
            sleep(500);
        }
        if (userInUserdata.isEmpty()) {
            Assertions.fail("Пользователь не был найден в БД userdata");
        } else {
            final String usernameFromDb = userInUserdata.get().getUsername();
            step("Проверить, что username в БД совпадает с отправленным",
                    () -> Assertions.assertEquals(user.username(), usernameFromDb));
        }
    }
}
