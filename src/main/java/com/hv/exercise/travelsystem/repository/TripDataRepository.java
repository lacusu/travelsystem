package com.hv.exercise.travelsystem.repository;

import com.hv.exercise.travelsystem.entity.TripData;
import com.hv.exercise.travelsystem.model.TripSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripDataRepository extends JpaRepository<TripData, Long> {

    @Query(value = "SELECT NEW com.hv.exercise.travelsystem.model.TripSummary(" +
            "   CAST(t.startDatetime AS DATE) AS TripDate, " +
            "    t.companyId, " +
            "    t.busId, " +
            "    SUM(CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END) AS CompleteTripCount, " +
            "    SUM(CASE WHEN t.status = 'INCOMPLETE' THEN 1 ELSE 0 END) AS IncompleteTripCount, " +
            "    SUM(CASE WHEN t.status = 'CANCELLED' THEN 1 ELSE 0 END) AS CancelledTripCount, " +
            "    SUM(t.chargeAmount) AS TotalCharges) " +
            "FROM " +
            "    TripData t " +
            "WHERE t.status != 'UNPROCESSABLE' " +
            "GROUP BY " +
            "    CAST(t.startDatetime AS DATE), t.companyId, t.busId " +
            "ORDER BY " +
            "    TripDate, t.companyId, t.busId")
    List<TripSummary> getSummaryData();

    @Query(value = "SELECT t FROM TripData t WHERE t.status ='UNPROCESSABLE'")
    List<TripData> findUnprocesable();

    @Query(value = "SELECT t FROM TripData t WHERE t.status !='UNPROCESSABLE'")
    List<TripData> findProceededTripData();
}
