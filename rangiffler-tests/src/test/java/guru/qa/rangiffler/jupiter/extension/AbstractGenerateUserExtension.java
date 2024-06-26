package guru.qa.rangiffler.jupiter.extension;

import guru.qa.grpc.rangiffler.grpc.FriendStatus;
import guru.qa.rangiffler.jupiter.annotation.*;
import guru.qa.rangiffler.model.UserModel;
import org.junit.jupiter.api.extension.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.qameta.allure.Allure.step;

public abstract class AbstractGenerateUserExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace GENERATE_USER_NAMESPACE =
            ExtensionContext.Namespace.create(AbstractGenerateUserExtension.class, User.GenerationType.FOR_GENERATE_USER);
    public static final ExtensionContext.Namespace API_LOGIN_NAMESPACE =
            ExtensionContext.Namespace.create(AbstractGenerateUserExtension.class, User.GenerationType.FOR_API_LOGIN);

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        UserModel[] generatedUsers = context.getStore(GENERATE_USER_NAMESPACE).get(context.getUniqueId(), UserModel[].class);
        UserModel apiLoginUser = context.getStore(API_LOGIN_NAMESPACE).get(context.getUniqueId(), UserModel.class);
        if (generatedUsers != null || apiLoginUser != null) {
            step("Удалить сгенерированных пользователей", () -> {
                if (generatedUsers != null) {
                    for (UserModel user : generatedUsers) {
                        deleteUser(user);
                    }
                }
                if (apiLoginUser != null) {
                    deleteUser(apiLoginUser);
                }
            });
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        GenerateUser[] generateUserAnnotations = context.getRequiredTestMethod().getAnnotationsByType(GenerateUser.class);
        ApiLogin apiLoginAnnotation = context.getRequiredTestMethod().getAnnotation(ApiLogin.class);

        if (generateUserAnnotations.length > 0 || apiLoginAnnotation != null) {
            step("Сгенерировать пользователей", () -> {
                List<UserModel> generatedUsers = new ArrayList<>();
                for (GenerateUser generateUserAnnotation : generateUserAnnotations) {
                    generatedUsers.add(generateUser(generateUserAnnotation));
                }
                context.getStore(GENERATE_USER_NAMESPACE).put(context.getUniqueId(), generatedUsers.toArray(new UserModel[0]));

                if (apiLoginAnnotation != null) {
                    context.getStore(API_LOGIN_NAMESPACE).put(context.getUniqueId(), generateUser(apiLoginAnnotation.user()));
                }
            });
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

    public abstract UserModel createUser(String username,
                                         String password,
                                         boolean generateFirstname,
                                         boolean generateLastname,
                                         boolean generateCountry,
                                         String avatar,
                                         FriendStatus status) throws IOException;

    public abstract void addPhotos(UserModel user, Photo[] photos);

    public abstract void addFriends(UserModel user, Friend[] friends) throws IOException;

    public abstract void deleteUser(UserModel user);

    private UserModel generateUser(GenerateUser annotation) throws IOException {
        UserModel user = createUser(
                annotation.username(),
                annotation.password(),
                annotation.generateFirstname(),
                annotation.generateLastname(),
                annotation.generateCountry(),
                annotation.avatar(),
                FriendStatus.NOT_FRIEND);
        addPhotos(user, annotation.photos());
        addFriends(user, annotation.friends());
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
