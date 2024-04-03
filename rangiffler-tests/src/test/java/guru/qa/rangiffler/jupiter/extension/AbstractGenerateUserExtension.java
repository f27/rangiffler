package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.GenerateUsers;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserModel;
import org.junit.jupiter.api.extension.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractGenerateUserExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace GENERATE_USER_NAMESPACE =
            ExtensionContext.Namespace.create(AbstractGenerateUserExtension.class, User.GenerationType.FOR_GENERATE_USER);
    public static final ExtensionContext.Namespace API_LOGIN_NAMESPACE =
            ExtensionContext.Namespace.create(AbstractGenerateUserExtension.class, User.GenerationType.FOR_API_LOGIN);

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        UserModel[] generatedUsers = context.getStore(GENERATE_USER_NAMESPACE).get(context.getUniqueId(), UserModel[].class);
        for (UserModel user : generatedUsers) {
            deleteUserById(user.getId(), user.getAuthId());
        }
        UserModel apiLoginUser = context.getStore(API_LOGIN_NAMESPACE).get(context.getUniqueId(), UserModel.class);
        if (apiLoginUser != null) {
            deleteUserById(apiLoginUser.getId(), apiLoginUser.getAuthId());
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        GenerateUser[] generateUserAnnotations = context.getRequiredTestMethod().getAnnotationsByType(GenerateUser.class);
        List<UserModel> generatedUsers = new ArrayList<>();
        for (GenerateUser generateUserAnnotation : generateUserAnnotations) {
            generatedUsers.add(generateUser(generateUserAnnotation));
        }
        context.getStore(GENERATE_USER_NAMESPACE).put(context.getUniqueId(), generatedUsers.toArray(new UserModel[0]));

        ApiLogin apiLoginAnnotation = context.getRequiredTestMethod().getAnnotation(ApiLogin.class);
        if (apiLoginAnnotation != null) {
            context.getStore(API_LOGIN_NAMESPACE).put(context.getUniqueId(), generateUser(apiLoginAnnotation.user()));
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        User annotation = parameterContext.getParameter().getAnnotation(User.class);
        if (annotation == null) {
            return false;
        }
        return isApiLoginUser(annotation, parameterContext, extensionContext)
                || isGenerateUser(annotation, parameterContext, extensionContext)
                || isGenerateUserArray(annotation, parameterContext, extensionContext);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
        User annotation = parameterContext.getParameter().getAnnotation(User.class);
        if (isApiLoginUser(annotation, parameterContext, extensionContext)) {
            return extensionContext.getStore(API_LOGIN_NAMESPACE).get(extensionContext.getUniqueId(), UserModel.class);
        }
        if (isGenerateUser(annotation, parameterContext, extensionContext)) {
            return extensionContext.getStore(GENERATE_USER_NAMESPACE).get(extensionContext.getUniqueId(), UserModel[].class)[0];
        }
        if (isGenerateUserArray(annotation, parameterContext, extensionContext)) {
            return extensionContext.getStore(GENERATE_USER_NAMESPACE).get(extensionContext.getUniqueId(), UserModel[].class);
        }
        throw new IllegalStateException();
    }

    public abstract UserModel createUser(GenerateUser annotation);

    public abstract void deleteUserById(UUID id, UUID authId);

    private UserModel generateUser(GenerateUser annotation) {
        UserModel user = createUser(annotation);
        return user;
    }

    private boolean isApiLoginUser(User annotation,
                                   ParameterContext parameterContext,
                                   ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(UserModel.class)
                && annotation.value().equals(User.GenerationType.FOR_API_LOGIN)
                && extensionContext.getRequiredTestMethod().isAnnotationPresent(ApiLogin.class);
    }

    private boolean isGenerateUser(User annotation,
                                   ParameterContext parameterContext,
                                   ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(UserModel.class)
                && annotation.value().equals(User.GenerationType.FOR_GENERATE_USER)
                && extensionContext.getRequiredTestMethod().isAnnotationPresent(GenerateUser.class);
    }

    private boolean isGenerateUserArray(User annotation,
                                        ParameterContext parameterContext,
                                        ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(UserModel[].class)
                && annotation.value().equals(User.GenerationType.FOR_GENERATE_USER)
                && extensionContext.getRequiredTestMethod().getAnnotation(GenerateUsers.class).value().length > 1;
    }
}
