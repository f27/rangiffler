package guru.qa.rangiffler.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface User {

    GenerationType value() default GenerationType.FOR_API_LOGIN;

    enum GenerationType {
        FOR_GENERATE_USER, FOR_API_LOGIN
    }
}
