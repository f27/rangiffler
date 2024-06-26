package guru.qa.rangiffler.jupiter.annotation.meta;

import guru.qa.rangiffler.jupiter.extension.ApiGenerateUserExtension;
import guru.qa.rangiffler.jupiter.extension.ContextHolderExtension;
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
        AllureJunit5.class,
        ApiGenerateUserExtension.class,
})
public @interface GqlTest {
}
