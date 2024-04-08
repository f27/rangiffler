package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.kafka.KafkaService;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaExtension implements SuiteExtension {

    private static final KafkaService KAFKA_CONSUMER = new KafkaService();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void beforeSuite(ExtensionContext extensionContext) {
        executor.execute(KAFKA_CONSUMER);
        executor.shutdown();
    }

    @Override
    public void afterSuite() {
        KAFKA_CONSUMER.stop();
    }
}
