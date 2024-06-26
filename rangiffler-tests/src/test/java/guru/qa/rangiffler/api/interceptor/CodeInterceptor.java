package guru.qa.rangiffler.api.interceptor;

import guru.qa.rangiffler.jupiter.extension.ApiLoginExtension;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class CodeInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        final Response response = chain.proceed(chain.request());
        if (response.isRedirect()) {
            final String location = response.header("Location");
            if (location.contains("code=")) {
                final String code = StringUtils.substringAfter(location, "code=");
                ApiLoginExtension.setCode(code);
            }
        }
        return response;
    }
}
