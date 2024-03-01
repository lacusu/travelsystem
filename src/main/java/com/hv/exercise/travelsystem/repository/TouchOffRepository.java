package com.hv.exercise.travelsystem.repository;

import com.hv.exercise.travelsystem.entity.TouchOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TouchOffRepository extends JpaRepository<TouchOff, Long> {
    List<TouchOff> findByCompanyIdAndBusIdAndIsProcessedIsFalseOrderByDatetime(String companyId, String busId);
}
