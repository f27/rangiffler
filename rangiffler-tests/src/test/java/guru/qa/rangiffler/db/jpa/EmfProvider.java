package guru.qa.rangiffler.db.jpa;

import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.db.DataBase;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EmfProvider {
    INSTANCE;

    private static final Config CFG = Config.getInstance();

    private final Map<DataBase, EntityManagerFactory> store = new ConcurrentHashMap<>();

    public EntityManagerFactory emf(DataBase database) {
        return store.computeIfAbsent(database, k -> {
            Map<String, String> settings = new HashMap<>();
            settings.put("hibernate.connection.url", database.p6spyUrl());
            settings.put("hibernate.connection.user", CFG.dbUser());
            settings.put("hibernate.connection.password", CFG.dbPassword());
            settings.put("hibernate.connection.driver_class", "com.p6spy.engine.spy.P6SpyDriver");
            settings.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
            return new ThreadSafeEntityManagerFactory(
                    Persistence.createEntityManagerFactory("rangiffler", settings)
            );
        });
    }

    public Collection<EntityManagerFactory> storedEmf() {
        return store.values();
    }
}
