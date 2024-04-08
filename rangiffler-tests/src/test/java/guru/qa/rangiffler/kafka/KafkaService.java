package guru.qa.rangiffler.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.model.UserModel;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaService implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaService.class);
    private static final Config CFG = Config.getInstance();
    private static final WaitForOne<String, UserModel> STORE = new WaitForOne<>();
    private static final Properties STR_KAFKA_PROPERTIES = new Properties();
    private static final ObjectMapper OM = new ObjectMapper();

    static {
        STR_KAFKA_PROPERTIES.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, String.format("%s:%d", CFG.kafkaHost(), CFG.kafkaPort()));
        STR_KAFKA_PROPERTIES.put(ConsumerConfig.GROUP_ID_CONFIG, "stringKafkaStringConsumerService");
        STR_KAFKA_PROPERTIES.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        STR_KAFKA_PROPERTIES.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        STR_KAFKA_PROPERTIES.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    private final AtomicBoolean threadStarted = new AtomicBoolean(true);
    private final Consumer<String, String> stringConsumer;

    public KafkaService() {
        this.stringConsumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(STR_KAFKA_PROPERTIES);
        this.stringConsumer.subscribe(CFG.kafkaTopics());
    }

    public static UserModel getMessage(String username) throws InterruptedException {
        return STORE.wait(username);
    }

    public void stop() {
        this.threadStarted.set(false);
    }

    @Override
    public void run() {
        try {
            while (threadStarted.get()) {
                LOG.info("### TRY TO POLL");
                ConsumerRecords<String, String> strRecords = stringConsumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, String> record : strRecords) {
                    logRecord(record);
                    deserializeRecord(record.value());
                }
                try {
                    stringConsumer.commitSync();
                } catch (CommitFailedException e) {
                    LOG.warn("### Commit failed: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOG.error("Error while consuming", e);
        } finally {
            stringConsumer.close();
            Thread.currentThread().interrupt();
        }
    }

    private void deserializeRecord(@Nonnull String recordValue) {
        try {
            UserModel userModel = OM.readValue(recordValue, UserModel.class);

            if (userModel == null || userModel.username() == null) {
                LOG.info("### Empty username in message ###");
                return;
            }

            STORE.provide(userModel.username(), userModel);
        } catch (JsonProcessingException e) {
            LOG.warn("### Parse message fail: " + e.getMessage());
        }
    }

    private void logRecord(@Nonnull ConsumerRecord<String, String> record) {
        LOG.info(String.format("topic = %s, \npartition = %d, \noffset = %d, \nkey = %s, \nvalue = %s\n\n",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value()));
    }
}
