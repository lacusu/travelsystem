package com.hv.exercise.travelsystem.repository;

import com.hv.exercise.travelsystem.entity.TouchData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TouchDataRepository extends JpaRepository<TouchData, Long> {

    @Query(value = "SELECT t FROM TouchData t " +
            "WHERE t.type = 'ON' " +
            "AND t.isProcessed is FALSE")
    List<TouchData> getUnprocessedTouchOns();

    @Query(value = "SELECT t FROM TouchData t " +
            "WHERE t.companyId = :companyId " +
            "AND t.busId = :busId " +
            "AND t.type = 'OFF' " +
            "AND t.isProcessed is FALSE " +
            "AND t.datetime >= :startTime " +
            "ORDER BY t.datetime ASC " +
            "LIMIT 1")
    Optional<TouchData> getTouchOff(@Param("companyId") String companyId,
                                    @Param("busId") String busId,
                                    @Param("startTime") LocalDateTime startTime);

    @Query(value = "SELECT t FROM TouchData t " +
            "WHERE t.isProcessed is FALSE")
    List<TouchData> getUnprocessedTouches();
}
