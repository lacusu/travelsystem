package com.hv.exercise.travelsystem.repository;

import com.hv.exercise.travelsystem.entity.TripFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TripFeeRepository extends JpaRepository<TripFee, Long> {
    @Query(value = "SELECT t FROM TripFee t " +
            "WHERE (t.fromStop = :fromStop AND t.toStop = :toStop)" +
            "OR (t.fromStop = :toStop AND t.toStop = :fromStop)")
    Optional<TripFee> findTripFee(@Param("fromStop") String fromStop,
                                  @Param("toStop") String toStop);

    @Query(value = "SELECT t FROM TripFee t " +
            "WHERE t.fromStop = :fromStop or t.toStop = :fromStop " +
            "ORDER BY t.fee DESC limit 1")
    Optional<TripFee> findMaxTripFeeBySingleStop(@Param("fromStop") String fromStop);
}
