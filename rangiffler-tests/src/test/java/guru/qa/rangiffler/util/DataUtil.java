package guru.qa.rangiffler.util;

import com.github.javafaker.Faker;
import guru.qa.rangiffler.model.CountryEnum;

import javax.annotation.Nonnull;

public class DataUtil {

    private final static Faker FAKER = new Faker();

    @Nonnull
    public static String generateRandomUsername() {
        return FAKER.name().username();
    }

    @Nonnull
    public static String generateRandomPassword() {
        return FAKER.internet().password(3, 12);
    }

    @Nonnull
    public static String generateRandomFirstname() {
        return FAKER.name().firstName();
    }

    @Nonnull
    public static String generateRandomLastname() {
        return FAKER.name().lastName();
    }

    @Nonnull
    public static String generateRandomPassword(int minLength, int maxLength) {
        return FAKER.internet().password(minLength, maxLength);
    }

    @Nonnull
    public static String generateStringWithLength(int length) {
        return FAKER.regexify(String.format("[a-z1-9]{%d}", length));
    }

    @Nonnull
    public static CountryEnum generateRandomCountry() {
        return CountryEnum.getRandom();
    }
}
