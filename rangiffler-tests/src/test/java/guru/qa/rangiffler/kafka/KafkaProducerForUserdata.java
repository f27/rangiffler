package guru.qa.rangiffler.kafka;

import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.model.UserModel;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringSerializer;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaProducerForUserdata {

    private static final Config CFG = Config.getInstance();
    private static KafkaProducer<String, String> producer;

    public static void sendMessage(UserModel user) throws ExecutionException, InterruptedException {
        if (producer == null) {
            Properties producerProperties = new Properties();
            producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                    String.format("%s:%d", CFG.kafkaHost(), CFG.kafkaPort()));
            producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producerProperties.put(ProducerConfig.ACKS_CONFIG, "all");
            producer = new KafkaProducer<>(producerProperties);
        }
        final ProducerRecord<String, String> record = new ProducerRecord<>(
                "users",
                null,
                System.currentTimeMillis(),
                null,
                user.toKafkaJson(),
                List.of(new RecordHeader("__TypeId__", UserModel.class.getName().getBytes(StandardCharsets.UTF_8)))
        );
        Future<RecordMetadata> future = producer.send(record);
        RecordMetadata metadata = future.get();
    }
}
