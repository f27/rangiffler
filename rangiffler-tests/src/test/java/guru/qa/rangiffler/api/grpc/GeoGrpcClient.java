package guru.qa.rangiffler.api.grpc;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.grpc.AllCountriesResponse;
import guru.qa.grpc.rangiffler.grpc.CountryCode;
import guru.qa.grpc.rangiffler.grpc.CountryResponse;
import guru.qa.grpc.rangiffler.grpc.RangifflerGeoServiceGrpc;
import io.qameta.allure.grpc.AllureGrpc;

import javax.annotation.Nonnull;

public class GeoGrpcClient extends GrpcClient {

    private final RangifflerGeoServiceGrpc.RangifflerGeoServiceBlockingStub blockingStub;

    public GeoGrpcClient() {
        super(GrpcChannelProvider.INSTANCE.channel(CFG.geoGrpcAddress()));
        this.blockingStub = RangifflerGeoServiceGrpc.newBlockingStub(channel).withInterceptors(new AllureGrpc());
    }

    public @Nonnull CountryResponse getCountryByCode(CountryCode countryCode) {
        return blockingStub.getCountryByCode(countryCode);
    }

    public @Nonnull AllCountriesResponse getAllCountries() {
        return blockingStub.getAllCountries(Empty.getDefaultInstance());
    }
}
