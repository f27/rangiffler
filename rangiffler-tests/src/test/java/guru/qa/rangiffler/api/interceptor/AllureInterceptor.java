package guru.qa.rangiffler.api.interceptor;

import io.qameta.allure.okhttp3.AllureOkHttp3;

public class AllureInterceptor extends AllureOkHttp3 {
    public AllureInterceptor() {
        this.setRequestTemplate("request.ftl");
        this.setResponseTemplate("response.ftl");
    }
}
