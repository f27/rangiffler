package guru.qa.rangiffler.api;

import guru.qa.rangiffler.api.cookie.ThreadSafeCookieManager;
import guru.qa.rangiffler.api.interceptor.AllureInterceptor;
import guru.qa.rangiffler.config.Config;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.LoggerFactory;
import retrofit2.Converter;
import retrofit2.Retrofit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.CookieManager;
import java.net.CookiePolicy;

public abstract class RestClient {

    protected static final Config CFG = Config.getInstance();

    protected final OkHttpClient okHttpClient;
    protected final Retrofit retrofit;

    public RestClient(@Nonnull String baseUri,
                      boolean followRedirect,
                      @Nonnull Converter.Factory converter,
                      @Nullable Interceptor... interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.followRedirects(followRedirect).addInterceptor(new AllureInterceptor());
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }
        builder.addNetworkInterceptor(
                new HttpLoggingInterceptor(
                        LoggerFactory.getLogger(getClass())::debug
                ).setLevel(HttpLoggingInterceptor.Level.BODY)
        );
        builder.cookieJar(new JavaNetCookieJar(new CookieManager(ThreadSafeCookieManager.INSTANCE, CookiePolicy.ACCEPT_ALL)));
        this.okHttpClient = builder.build();
        this.retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUri)
                .addConverterFactory(converter)
                .build();
    }
}
