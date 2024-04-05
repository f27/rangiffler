package guru.qa.rangiffler.service;

import guru.qa.rangiffler.entity.authority.Authority;
import guru.qa.rangiffler.entity.authority.AuthorityEntity;
import guru.qa.rangiffler.entity.user.UserEntity;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.repository.UserRepository;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final String DEFAULT_COUNTRY_CODE = "ru";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, UserModel> kafkaTemplate;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       KafkaTemplate<String, UserModel> kafkaTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public @Nonnull
    String registerUser(@Nonnull String username, @Nonnull String password) {
        UserEntity user = userRepository.findByUsername(username);
        if (user != null) {
            throw new DataIntegrityViolationException("Username `" + username + "` already exists");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setEnabled(true);
        userEntity.setAccountNonExpired(true);
        userEntity.setCredentialsNonExpired(true);
        userEntity.setAccountNonLocked(true);
        userEntity.setUsername(username);
        userEntity.setPassword(passwordEncoder.encode(password));

        AuthorityEntity readAuthorityEntity = new AuthorityEntity();
        readAuthorityEntity.setAuthority(Authority.read);
        AuthorityEntity writeAuthorityEntity = new AuthorityEntity();
        writeAuthorityEntity.setAuthority(Authority.write);

        userEntity.addAuthorities(readAuthorityEntity, writeAuthorityEntity);
        String savedUser = userRepository.save(userEntity).getUsername();

        kafkaTemplate.send("users", new UserModel(savedUser, DEFAULT_COUNTRY_CODE));
        return savedUser;
    }
}
