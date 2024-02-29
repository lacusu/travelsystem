package com.hv.exercise.travelsystem.repository;

import com.hv.exercise.travelsystem.entity.TripFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripFeeRepository  extends JpaRepository<TripFee, Long> {
}
