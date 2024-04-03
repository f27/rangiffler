package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.db.repository.AuthRepository;
import guru.qa.rangiffler.db.repository.UserdataRepository;
import guru.qa.rangiffler.db.repository.hibernate.AuthRepositoryHibernate;
import guru.qa.rangiffler.db.repository.hibernate.UserdataRepositoryHibernate;
import guru.qa.rangiffler.jupiter.annotation.UserForRegistration;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.util.DataUtil;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserForRegistrationExtension implements AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserForRegistrationExtension.class);

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Optional<Parameter> annotatedParameter = Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(UserForRegistration.class))
                .findAny();
        if (annotatedParameter.isPresent()) {
            UserModel userToDelete = context.getStore(NAMESPACE).get(context.getUniqueId(), UserModel.class);
            AuthRepository authRepository = new AuthRepositoryHibernate();
            UserdataRepository userdataRepository = new UserdataRepositoryHibernate();
            authRepository.findByUsername(userToDelete.getUsername())
                    .ifPresent(userAuthEntity -> authRepository.deleteById(userAuthEntity.getId()));
            userdataRepository.findByUsername(userToDelete.getUsername())
                    .ifPresent(userEntity -> userdataRepository.deleteById(userEntity.getId()));
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
        UserModel user = new UserModel();
        user.setUsername(DataUtil.generateRandomUsername());
        user.setPassword(DataUtil.generateRandomPassword());
        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), user);
        return user;
    }
}
