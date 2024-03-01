package com.hv.exercise.travelsystem.repository;

import com.hv.exercise.travelsystem.entity.TouchOn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TouchOnRepository extends JpaRepository<TouchOn, Long> {
    List<TouchOn> findByIsProcessedIsFalse();
}
