package guru.qa.rangiffler.api.grpc;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.grpc.CountryCode;
import guru.qa.grpc.rangiffler.grpc.RangifflerGeoServiceGrpc;
import guru.qa.rangiffler.model.CountryModel;
import io.qameta.allure.grpc.AllureGrpc;

import javax.annotation.Nonnull;
import java.util.List;

public class GeoGrpcClient extends GrpcClient {

    private final RangifflerGeoServiceGrpc.RangifflerGeoServiceBlockingStub blockingStub;

    public GeoGrpcClient() {
        super(GrpcChannelProvider.INSTANCE.channel(CFG.geoGrpcAddress()));
        this.blockingStub = RangifflerGeoServiceGrpc.newBlockingStub(channel).withInterceptors(new AllureGrpc());
    }

    public @Nonnull CountryModel getCountryByCode(String countryCode) {
        CountryCode request = CountryCode.newBuilder()
                .setCode(countryCode)
                .build();
        return CountryModel.fromGrpcMessage(blockingStub.getCountryByCode(request));
    }

    public @Nonnull List<CountryModel> getAllCountries() {
        return CountryModel.fromGrpcMessage(blockingStub.getAllCountries(Empty.getDefaultInstance()));
    }
}
