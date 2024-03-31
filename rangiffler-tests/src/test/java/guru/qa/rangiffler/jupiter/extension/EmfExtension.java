package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.db.jpa.EmfProvider;
import jakarta.persistence.EntityManagerFactory;

public class EmfExtension implements SuiteExtension {
    @Override
    public void afterSuite() {
        EmfProvider.INSTANCE.storedEmf().forEach(
                EntityManagerFactory::close
        );
    }
}
