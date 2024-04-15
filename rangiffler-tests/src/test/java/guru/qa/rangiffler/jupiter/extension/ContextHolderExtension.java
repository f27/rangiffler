package guru.qa.rangiffler.jupiter.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ContextHolderExtension implements BeforeEachCallback, AfterEachCallback {
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        ContextHolder.INSTANCE.clear();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        ContextHolder.INSTANCE.setContext(context);
    }

    public enum ContextHolder {
        INSTANCE;

        private final ThreadLocal<ExtensionContext> tlExtensionContext = new ThreadLocal<>();

        public ExtensionContext getContext() {
            return tlExtensionContext.get();
        }

        public void setContext(ExtensionContext context) {
            tlExtensionContext.set(context);
        }

        public void clear() {
            tlExtensionContext.remove();
        }
    }
}
