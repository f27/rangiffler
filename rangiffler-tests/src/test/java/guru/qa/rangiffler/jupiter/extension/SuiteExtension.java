package guru.qa.rangiffler.jupiter.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public interface SuiteExtension extends BeforeAllCallback {

    @Override
    default void beforeAll(ExtensionContext context) throws Exception {
        context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL)
                .getOrComputeIfAbsent(this.getClass(), key -> {
                    beforeSuite(context);
                    return new ExtensionContext.Store.CloseableResource() {
                        @Override
                        public void close() throws Throwable {
                            afterSuite();
                        }
                    };
                });
    }

    default void beforeSuite(ExtensionContext extensionContext) {
    }

    default void afterSuite() {
    }
}
