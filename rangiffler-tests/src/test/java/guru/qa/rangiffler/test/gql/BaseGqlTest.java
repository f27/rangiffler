package guru.qa.rangiffler.test.gql;

import guru.qa.rangiffler.api.rest.GatewayApiClient;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.jupiter.annotation.meta.GqlTest;
import guru.qa.rangiffler.jupiter.extension.ApiLoginExtension;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.RegisterExtension;

@GqlTest
@Tag("GraphQL")
@Epic("GraphQL")
public abstract class BaseGqlTest {

    protected static final Config CFG = Config.getInstance();

    @RegisterExtension
    protected final ApiLoginExtension apiLoginExtension = new ApiLoginExtension(false);

    protected final GatewayApiClient gatewayApiClient = new GatewayApiClient();

}
