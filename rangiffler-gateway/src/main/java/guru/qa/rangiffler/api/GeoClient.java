package guru.qa.rangiffler.api;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.grpc.CountryCode;
import guru.qa.grpc.rangiffler.grpc.RangifflerGeoServiceGrpc;
import guru.qa.rangiffler.model.country.CountryModel;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeoClient {

    @GrpcClient("geoClient")
    private RangifflerGeoServiceGrpc.RangifflerGeoServiceBlockingStub rangifflerGeoServiceBlockingStub;

    public @Nonnull CountryModel getCountryByCode(String countryCode) {
        CountryCode request = CountryCode.newBuilder()
                .setCode(countryCode)
                .build();
        return CountryModel.fromGrpcMessage(rangifflerGeoServiceBlockingStub.getCountryByCode(request));
    }

    public @Nonnull List<CountryModel> getAllCountries() {
        return CountryModel.fromGrpcMessage(rangifflerGeoServiceBlockingStub.getAllCountries(Empty.getDefaultInstance()));
    }
}
