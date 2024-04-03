package guru.qa.rangiffler.api.cookie;

import java.net.*;
import java.util.List;

public enum ThreadSafeCookieManager implements CookieStore {
    INSTANCE;

    private final ThreadLocal<CookieStore> tlCookieStore = ThreadLocal.withInitial(
            () -> new CookieManager(null, CookiePolicy.ACCEPT_ALL).getCookieStore()
    );

    private CookieStore getCookieStore() {
        return tlCookieStore.get();
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        getCookieStore().add(uri, cookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return getCookieStore().get(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return getCookieStore().getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return getCookieStore().getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return getCookieStore().remove(uri, cookie);
    }

    @Override
    public boolean removeAll() {
        return getCookieStore().removeAll();
    }

    public String getCookieValue(String cookieName) {
        return getCookies().stream()
                .filter(cookie -> cookie.getName().equals(cookieName))
                .map(HttpCookie::getValue)
                .findAny()
                .orElseThrow();
    }
}
