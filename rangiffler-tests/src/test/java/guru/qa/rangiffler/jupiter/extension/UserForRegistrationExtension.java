package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.db.entity.user.UserEntity;
import guru.qa.rangiffler.db.repository.AuthRepository;
import guru.qa.rangiffler.db.repository.PhotoRepository;
import guru.qa.rangiffler.db.repository.UserdataRepository;
import guru.qa.rangiffler.db.repository.hibernate.AuthRepositoryHibernate;
import guru.qa.rangiffler.db.repository.hibernate.PhotoRepositoryHibernate;
import guru.qa.rangiffler.db.repository.hibernate.UserdataRepositoryHibernate;
import guru.qa.rangiffler.jupiter.annotation.UserForRegistration;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.util.DataUtil;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.sleep;
import static io.qameta.allure.Allure.step;

public class UserForRegistrationExtension implements AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserForRegistrationExtension.class);

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Optional<Parameter> annotatedParameter = Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(UserForRegistration.class))
                .findAny();
        if (annotatedParameter.isPresent()) {
            UserModel userToDelete = context.getStore(NAMESPACE).get(context.getUniqueId(), UserModel.class);
            step("Удалить зарегистрированного во время теста пользователя", () -> {
                AuthRepository authRepository = new AuthRepositoryHibernate();
                UserdataRepository userdataRepository = new UserdataRepositoryHibernate();
                PhotoRepository photoRepository = new PhotoRepositoryHibernate();
                // user in userdata can be created with time lag
                Optional<UserEntity> userInUserdata = Optional.empty();
                for (int i = 1; i <= 30; i++) {
                    userInUserdata = userdataRepository.findByUsername(userToDelete.username());
                    if (userInUserdata.isPresent())
                        break;
                    sleep(200);
                }
                authRepository.findByUsername(userToDelete.username())
                        .ifPresent(userAuthEntity -> authRepository.deleteById(userAuthEntity.getId()));
                userInUserdata
                        .ifPresent(userEntity -> {
                            photoRepository.deleteByUserId(userEntity.getId());
                            userdataRepository.deleteById(userEntity.getId());
                        });

            });
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        List<Parameter> annotatedParameters = Arrays.stream(extensionContext.getRequiredTestMethod().getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(UserForRegistration.class))
                .toList();
        return annotatedParameters.size() == 1
                && parameterContext.getParameter().getType().isAssignableFrom(UserModel.class);
    }

    @Override
    public UserModel resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        UserModel user = new UserModel(
                null,
                null,
                DataUtil.generateRandomUsername(),
                DataUtil.generateRandomPassword(),
                null,
                null,
                null,
                CountryEnum.RUSSIAN_FEDERATION,
                null,
                new ArrayList<>(),
                new ArrayList<>()
        );
        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), user);
        return user;
    }
}
