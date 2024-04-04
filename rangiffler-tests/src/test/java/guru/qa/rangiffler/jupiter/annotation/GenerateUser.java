package guru.qa.rangiffler.jupiter.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(GenerateUsers.class)
public @interface GenerateUser {

    String username() default "";

    String password() default "";

    boolean generateFirstname() default false;

    boolean generateLastname() default false;

    String avatar() default "";

}
