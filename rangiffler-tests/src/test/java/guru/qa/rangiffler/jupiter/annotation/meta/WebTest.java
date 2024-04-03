package guru.qa.rangiffler.jupiter.annotation.meta;

import guru.qa.rangiffler.jupiter.extension.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({
        ContextHolderExtension.class,
        DBGenerateUserExtension.class,
        BrowserExtension.class,
        AllureJunit5.class,
        UserForRegistrationExtension.class,
        ApiLoginExtension.class
})
public @interface WebTest {
}
