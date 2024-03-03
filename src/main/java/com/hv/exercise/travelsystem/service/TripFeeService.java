package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.entity.TripFee;
import com.hv.exercise.travelsystem.exception.TripFeeServiceException;
import com.hv.exercise.travelsystem.repository.TripFeeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class TripFeeService {
    private TripFeeRepository tripFeeRepository;

    public TripFee calculateTripFee(String fromStopId, String toStopId) {
        log.info("Calculating trip fee for the trip from [{}] to [{}]", fromStopId, toStopId);
        if (StringUtils.isNotBlank(fromStopId) && StringUtils.isNotBlank(toStopId)) {
            return getTripFee(fromStopId, toStopId);
        } else if (StringUtils.isNotBlank(fromStopId)) {
            return getMaxTripFeeBySingleStop(fromStopId);
        } else if (StringUtils.isNotBlank(toStopId)) {
            return getMaxTripFeeBySingleStop(toStopId);
        } else {
            throw new TripFeeServiceException("Missing data to identify for a trip fee");
        }
    }

    private TripFee getTripFee(String fromStop, String toStop) {
        return tripFeeRepository.findTripFee(fromStop, toStop)
                .orElseThrow(TripFeeServiceException::new);
    }

    private TripFee getMaxTripFeeBySingleStop(String fromStop) {
        return tripFeeRepository.findMaxTripFeeBySingleStop(fromStop)
                .orElseThrow(TripFeeServiceException::new);
    }
}
