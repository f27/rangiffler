package guru.qa.rangiffler.jupiter.annotation;

import guru.qa.rangiffler.jupiter.extension.GqlRequestResolver;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(GqlRequestResolver.class)
public @interface GqlRequestFile {
    String value();
}
