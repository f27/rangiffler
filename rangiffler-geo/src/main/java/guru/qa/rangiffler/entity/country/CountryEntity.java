package guru.qa.rangiffler.entity.country;

import guru.qa.grpc.rangiffler.grpc.AllCountriesResponse;
import guru.qa.grpc.rangiffler.grpc.CountryResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "country")
public class CountryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true, columnDefinition = "LONGBLOB")
    private byte[] flag;

    public static CountryResponse toGrpcMessage(CountryEntity entity) {
        return CountryResponse.newBuilder()
                .setId(entity.getId().toString())
                .setCode(entity.getCode())
                .setName(entity.getName())
                .setFlag(new String(entity.getFlag(), StandardCharsets.UTF_8))
                .build();
    }

    public static AllCountriesResponse toGrpcMessage(List<CountryEntity> countries) {
        return AllCountriesResponse.newBuilder()
                .addAllAllCountries(countries.stream()
                        .map(CountryEntity::toGrpcMessage)
                        .toList()
                ).build();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        CountryEntity that = (CountryEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
