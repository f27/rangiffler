package guru.qa.rangiffler.service;

import guru.qa.rangiffler.entity.user.UserEntity;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.repository.UserdataRepository;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class UserdataKafkaListener {

    private static final String DEFAULT_COUNTRY_CODE = "ru";

    private final UserdataRepository userdataRepository;

    @Autowired
    public UserdataKafkaListener(UserdataRepository userdataRepository) {
        this.userdataRepository = userdataRepository;
    }

    @Transactional
    @KafkaListener(topics = "users", groupId = "userdata")
    public void listener(@Payload UserModel user, ConsumerRecord<String, UserModel> cr) {
        UserEntity userDataEntity = new UserEntity();
        userDataEntity.setUsername(user.username());
        userDataEntity.setCountryCode(DEFAULT_COUNTRY_CODE);
        userdataRepository.save(userDataEntity);
    }
}
