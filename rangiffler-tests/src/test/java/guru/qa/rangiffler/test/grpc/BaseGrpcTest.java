package guru.qa.rangiffler.test.grpc;

import guru.qa.rangiffler.api.grpc.GeoGrpcClient;
import guru.qa.rangiffler.api.grpc.PhotoGrpcClient;
import guru.qa.rangiffler.api.grpc.UserdataGrpcClient;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.jupiter.annotation.meta.GrpcTest;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.Tag;

@GrpcTest
@Tag("gRPC")
@Epic("gRPC")
public abstract class BaseGrpcTest {

    protected static final Config CFG = Config.getInstance();

    protected final GeoGrpcClient geoGrpcClient = new GeoGrpcClient();
    protected final PhotoGrpcClient photoGrpcClient = new PhotoGrpcClient();
    protected final UserdataGrpcClient userdataGrpcClient = new UserdataGrpcClient();

}
