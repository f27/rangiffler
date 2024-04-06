package guru.qa.rangiffler.jupiter.annotation;

import guru.qa.rangiffler.model.CountryEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Photo {
    String image() default "img/photo/default.jpg";

    CountryEnum country() default CountryEnum.RUSSIAN_FEDERATION;

    String description() default "";
}
