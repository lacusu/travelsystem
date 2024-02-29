package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.entity.TripFee;
import com.hv.exercise.travelsystem.repository.TripFeeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class TriFeeService {
    private TripFeeRepository tripFeeRepository;

    public void importTripFee(List<TripFee> tripFees) {
        tripFeeRepository.saveAll(tripFees);
    }
}
