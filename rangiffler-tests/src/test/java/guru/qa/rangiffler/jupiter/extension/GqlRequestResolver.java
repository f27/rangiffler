package guru.qa.rangiffler.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rangiffler.jupiter.annotation.GqlRequestFile;
import guru.qa.rangiffler.model.gql.GqlRequest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.IOException;
import java.io.InputStream;

public class GqlRequestResolver implements ParameterResolver {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ClassLoader cl = GqlRequestResolver.class.getClassLoader();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().isAnnotationPresent(GqlRequestFile.class)
                && parameterContext.getParameter().getType().isAssignableFrom(GqlRequest.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
        GqlRequestFile annotation = parameterContext.getParameter().getAnnotation(GqlRequestFile.class);
        try (InputStream is = cl.getResourceAsStream(annotation.value())) {
            return OBJECT_MAPPER.readValue(is.readAllBytes(), GqlRequest.class);
        } catch (IOException e) {
            throw new ParameterResolutionException(e.getMessage());
        }
    }
}
