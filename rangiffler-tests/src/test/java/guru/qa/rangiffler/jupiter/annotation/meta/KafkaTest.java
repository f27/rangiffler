package guru.qa.rangiffler.jupiter.annotation.meta;

import guru.qa.rangiffler.jupiter.extension.ContextHolderExtension;
import guru.qa.rangiffler.jupiter.extension.KafkaExtension;
import guru.qa.rangiffler.jupiter.extension.UserForRegistrationExtension;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({
        KafkaExtension.class,
        UserForRegistrationExtension.class,
        ContextHolderExtension.class,
        AllureJunit5.class,
})
public @interface KafkaTest {
}
