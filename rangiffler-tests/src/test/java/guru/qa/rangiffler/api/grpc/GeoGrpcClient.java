package guru.qa.rangiffler.api.grpc;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.grpc.CountryCode;
import guru.qa.grpc.rangiffler.grpc.RangifflerGeoServiceGrpc;
import guru.qa.rangiffler.model.CountryModel;

import javax.annotation.Nonnull;
import java.util.List;

public class GeoGrpcClient extends GrpcClient {

    private final RangifflerGeoServiceGrpc.RangifflerGeoServiceBlockingStub blockingStub;

    public GeoGrpcClient() {
        super(CFG.geoGrpcHost(), CFG.geoGrpcPort());
        this.blockingStub = RangifflerGeoServiceGrpc.newBlockingStub(channel);
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
