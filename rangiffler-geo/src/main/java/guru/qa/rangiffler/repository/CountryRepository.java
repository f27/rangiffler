package guru.qa.rangiffler.repository;

import guru.qa.rangiffler.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

    Optional<CountryEntity> findByCode(String countryCode);

    List<CountryEntity> findAllByOrderByNameAsc();

}
