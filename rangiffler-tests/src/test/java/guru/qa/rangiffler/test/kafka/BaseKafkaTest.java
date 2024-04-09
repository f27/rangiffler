package guru.qa.rangiffler.test.kafka;

import guru.qa.rangiffler.jupiter.annotation.meta.KafkaTest;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.Tag;

@KafkaTest
@Tag("Kafka")
@Epic("Kafka")
public abstract class BaseKafkaTest {
}
