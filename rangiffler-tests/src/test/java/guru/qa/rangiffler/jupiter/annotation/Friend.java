package guru.qa.rangiffler.jupiter.annotation;

import guru.qa.rangiffler.model.FriendStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Friend {

    String username() default "";

    String password() default "";

    boolean generateFirstname() default false;

    boolean generateLastname() default false;

    boolean generateCountry() default false;

    Photo[] photos() default {};

    String avatar() default "";

    FriendStatus status() default FriendStatus.FRIEND;
}
