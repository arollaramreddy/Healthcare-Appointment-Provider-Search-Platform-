package com.example.appointments.repository;

import com.example.appointments.model.Provider;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
    boolean existsByNpi(String npi);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Provider p where p.id = :id")
    Optional<Provider> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select distinct p from Provider p
            where (:specialty is null or lower(p.specialty) like lower(concat('%', :specialty, '%')))
              and (:city is null or lower(p.city) = lower(:city))
              and (:state is null or upper(p.state) = upper(:state))
              and (:accepting is null or p.acceptingNewPatients = :accepting)
            order by p.name
            """)
    List<Provider> search(@Param("specialty") String specialty,
                          @Param("city") String city,
                          @Param("state") String state,
                          @Param("accepting") Boolean accepting);
}
