package guru.qa.rangiffler.jupiter.extension;

import com.codeborne.selenide.LocalStorage;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.rangiffler.api.AuthApiClient;
import guru.qa.rangiffler.api.cookie.ThreadSafeCookieManager;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Token;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.util.OAuthUtil;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.Cookie;

import javax.annotation.Nonnull;

import static io.qameta.allure.Allure.step;

public class ApiLoginExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
    private final AuthApiClient authApiClient = new AuthApiClient();
    private final boolean initBrowser;

    public ApiLoginExtension() {
        this(true);
    }

    public ApiLoginExtension(boolean initBrowser) {
        this.initBrowser = initBrowser;
    }

    public static String getCodeVerifier() {
        ExtensionContext context = ContextHolderExtension.ContextHolder.INSTANCE.getContext();
        return context.getStore(NAMESPACE).get(context.getUniqueId() + "code_verifier", String.class);
    }

    public static void setCodeVerifier(@Nonnull String codeVerifier) {
        ExtensionContext context = ContextHolderExtension.ContextHolder.INSTANCE.getContext();
        context.getStore(NAMESPACE).put(context.getUniqueId() + "code_verifier", codeVerifier);
    }

    public static String getCodeChallenge() {
        ExtensionContext context = ContextHolderExtension.ContextHolder.INSTANCE.getContext();
        return context.getStore(NAMESPACE).get(context.getUniqueId() + "code_challenge", String.class);
    }

    public static void setCodeChallenge(@Nonnull String codeChallenge) {
        ExtensionContext context = ContextHolderExtension.ContextHolder.INSTANCE.getContext();
        context.getStore(NAMESPACE).put(context.getUniqueId() + "code_challenge", codeChallenge);
    }

    public static String getCode() {
        ExtensionContext context = ContextHolderExtension.ContextHolder.INSTANCE.getContext();
        return context.getStore(NAMESPACE).get(context.getUniqueId() + "code", String.class);
    }

    public static void setCode(@Nonnull String code) {
        ExtensionContext context = ContextHolderExtension.ContextHolder.INSTANCE.getContext();
        context.getStore(NAMESPACE).put(context.getUniqueId() + "code", code);
    }

    public static String getToken() {
        ExtensionContext context = ContextHolderExtension.ContextHolder.INSTANCE.getContext();
        return context.getStore(NAMESPACE).get(context.getUniqueId() + "id_token", String.class);
    }

    public static void setToken(@Nonnull String token) {
        ExtensionContext context = ContextHolderExtension.ContextHolder.INSTANCE.getContext();
        context.getStore(NAMESPACE).put(context.getUniqueId() + "id_token", token);
    }

    public static String getCsrfToken() {
        return ThreadSafeCookieManager.INSTANCE.getCookieValue("XSRF-TOKEN");
    }

    public static Cookie getJsessionCookie() {
        return new Cookie(
                "JSESSIONID",
                ThreadSafeCookieManager.INSTANCE.getCookieValue("JSESSIONID")
        );
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        ApiLogin apiLogin = context.getRequiredTestMethod().getAnnotation(ApiLogin.class);

        if (apiLogin != null) {
            UserModel generatedUser = context.getStore(AbstractGenerateUserExtension.API_LOGIN_NAMESPACE)
                    .get(context.getUniqueId(), UserModel.class);

            setCodeVerifier(OAuthUtil.generateCodeVerifier());
            setCodeChallenge(OAuthUtil.generateCodeChallenge(getCodeVerifier()));

            authApiClient.doLogin(generatedUser.getUsername(), generatedUser.getPassword());

            if (initBrowser) {
                step("Запустить браузер с авторизованным пользователем", () -> {
                    Selenide.open("/");
                    LocalStorage localStorage = Selenide.localStorage();
                    localStorage.setItem("codeChallenge", getCodeChallenge());
                    localStorage.setItem("id_token", getToken());
                    localStorage.setItem("codeVerifier", getCodeVerifier());
                    WebDriverRunner.getWebDriver().manage().addCookie(getJsessionCookie());
                    Selenide.refresh();
                });
            }
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        ThreadSafeCookieManager.INSTANCE.removeAll();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().isAnnotationPresent(Token.class)
                && parameterContext.getParameter().getType().isAssignableFrom(String.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return "Bearer " + getToken();
    }
}
