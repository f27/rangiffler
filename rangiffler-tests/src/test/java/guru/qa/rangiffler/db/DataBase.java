package guru.qa.rangiffler.db;

import guru.qa.rangiffler.config.Config;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DataBase {
    AUTH("jdbc:mysql://%s:%d/rangiffler-auth?serverTimezone=UTC"),
    GEO("jdbc:mysql://%s:%d/rangiffler-geo?serverTimezone=UTC"),
    PHOTO("jdbc:mysql://%s:%d/rangiffler-photo?serverTimezone=UTC"),
    USERDATA("jdbc:mysql://%s:%d/rangiffler-userdata?serverTimezone=UTC");

    private static final Config CFG = Config.getInstance();
    private final String url;

    public String getUrl() {
        return String.format(
                url,
                CFG.dbHost(),
                CFG.dbPort()
        );
    }

    public String p6spyUrl() {
        final String url = getUrl();
        return "jdbc:p6spy:" + url.substring(url.indexOf("jdbc:") + 5);
    }
}
