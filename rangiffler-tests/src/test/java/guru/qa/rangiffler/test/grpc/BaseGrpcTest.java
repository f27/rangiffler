package guru.qa.rangiffler.test.grpc;

import guru.qa.grpc.rangiffler.grpc.RangifflerGeoServiceGrpc;
import guru.qa.grpc.rangiffler.grpc.RangifflerPhotoServiceGrpc;
import guru.qa.grpc.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import guru.qa.rangiffler.grpc.GrpcChannelProvider;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.jupiter.annotation.meta.GrpcTest;
import io.qameta.allure.Epic;
import io.qameta.allure.grpc.AllureGrpc;
import org.junit.jupiter.api.Tag;

@GrpcTest
@Tag("gRPC")
@Epic("gRPC")
public abstract class BaseGrpcTest {

    protected static final Config CFG = Config.getInstance();

    protected final RangifflerGeoServiceGrpc.RangifflerGeoServiceBlockingStub geoGrpcBlockingStub =
            RangifflerGeoServiceGrpc.newBlockingStub(GrpcChannelProvider.INSTANCE.channel(CFG.geoGrpcAddress()))
                    .withInterceptors(new AllureGrpc());
    protected final RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub photoGrpcBlockingStub =
            RangifflerPhotoServiceGrpc.newBlockingStub(GrpcChannelProvider.INSTANCE.channel(CFG.photoGrpcAddress()))
                    .withInterceptors(new AllureGrpc());
    protected final RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub userdataGrpcBlockingStub =
            RangifflerUserdataServiceGrpc.newBlockingStub(GrpcChannelProvider.INSTANCE.channel(CFG.userdataGrpcAddress()))
                    .withInterceptors(new AllureGrpc());

}
