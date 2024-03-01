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
public class TripFeeService {
    private TripFeeRepository tripFeeRepository;

    public void importTripFee(List<TripFee> tripFees) {
        tripFeeRepository.saveAll(tripFees);
    }

    public TripFee getTripFee(String fromStop, String toStop) throws Exception {
        return tripFeeRepository.findTripFee(fromStop, toStop)
                .orElseThrow(Exception::new);
    }

    public TripFee getMaxTripFeeBySingleStop(String fromStop) throws Exception {
        return tripFeeRepository.findMaxTripFeeBySingleStop(fromStop)
                .orElseThrow(Exception::new);
    }
}
