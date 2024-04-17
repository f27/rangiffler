package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.grpc.rangiffler.grpc.AllCountriesResponse;
import guru.qa.grpc.rangiffler.grpc.CountryCode;
import guru.qa.grpc.rangiffler.grpc.CountryResponse;
import guru.qa.grpc.rangiffler.grpc.RangifflerGeoServiceGrpc;
import guru.qa.rangiffler.entity.CountryEntity;
import guru.qa.rangiffler.repository.CountryRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
public class GeoService extends RangifflerGeoServiceGrpc.RangifflerGeoServiceImplBase {

    private final CountryRepository countryRepository;

    @Autowired
    public GeoService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void getCountryByCode(CountryCode request, StreamObserver<CountryResponse> responseObserver) {
        countryRepository.findByCode(request.getCode())
                .ifPresentOrElse(
                        countryEntity -> {
                            responseObserver.onNext(CountryEntity.toGrpcMessage(countryEntity));
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(
                                Status.NOT_FOUND.withDescription("Country not found")
                                        .asRuntimeException()
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public void getAllCountries(Empty request, StreamObserver<AllCountriesResponse> responseObserver) {
        responseObserver.onNext(CountryEntity.toGrpcMessage(countryRepository.findAllByOrderByNameAsc()));
        responseObserver.onCompleted();
    }
}
