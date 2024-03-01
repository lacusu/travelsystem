package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.constant.TripStatus;
import com.hv.exercise.travelsystem.entity.TouchOff;
import com.hv.exercise.travelsystem.entity.TouchOn;
import com.hv.exercise.travelsystem.entity.TripData;
import com.hv.exercise.travelsystem.entity.TripFee;
import com.hv.exercise.travelsystem.repository.TripDataRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class TripService {
    private TouchService touchService;

    private TripFeeService tripFeeService;

    private TripDataRepository tripDataRepository;

    private HashingService hashingService;

    private FileService fileService;

    public List<TripData> getTripData() {
        return tripDataRepository.findAll();
    }

    public void calculateTripFee() {
        List<TouchOn> touchOns = touchService.getUnprocessedTouchOn();

//        List<TripData> tripDataList =
        List<TripData> tripDataList = new ArrayList<>();
        touchOns
                .forEach(touchOn -> {

                    String reason = validateTouchOn(touchOn);
                    if (StringUtils.isBlank(touchOn.getCompanyId()) || StringUtils.isBlank(touchOn.getBusId())) {
                        TripData processableTripData = constructTripData(touchOn, TouchOff.builder().build(),
                                BigDecimal.ZERO, TripStatus.UNPROCESSABLE);
                        processableTripData.setReason(reason);
                        tripDataList.add(processableTripData);
                    } else {
                        Optional<TouchOff> touchOffOptional = touchService.findTouchOff(touchOn.getCompanyId(), touchOn.getBusId());
                        BigDecimal chargeAmount;
                        TripStatus status;
                        if (touchOffOptional.isPresent()) {
                            TouchOff touchOff = touchOffOptional.get();
                            if (!touchOff.getPan().equalsIgnoreCase(touchOn.getPan())) {
                                reason = "PAN mismatch";
                            } else {
                                reason = validateTouchOff(touchOff);
                            }

                            if (touchOff.getStopId().equalsIgnoreCase(touchOn.getStopId())) {
                                //Cancelled
                                chargeAmount = BigDecimal.ZERO;
                                status = TripStatus.CANCELLED;
                            } else {
                                //Completed
                                TripFee tripFee = null;
                                try {
                                    tripFee = tripFeeService.getTripFee(touchOn.getStopId(), touchOff.getStopId());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                chargeAmount = tripFee.getFee();
                                status = TripStatus.COMPLETED;
                            }

                            TripData tripData = constructTripData(touchOn, touchOff, chargeAmount, status);


                            if (StringUtils.isNotBlank(reason)) {
                                tripData.setStatus(TripStatus.UNPROCESSABLE);
                                tripData.setReason(reason);
                            }

                            tripDataList.add(tripData);

                            touchOff.setProcessed(true);
                            touchService.saveTouchOff(touchOff);

                        } else {
                            //Incomplete
                            status = TripStatus.INCOMPLETE;
                            TripFee tripFee = null;
                            try {
                                tripFee = tripFeeService.getMaxTripFeeBySingleStop(touchOn.getStopId());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            chargeAmount = tripFee.getFee();
                            TouchOff incompleteTouchOff = new TouchOff();
                            incompleteTouchOff.setStopId(touchOn.getStopId().equalsIgnoreCase(tripFee.getFromStop()) ?
                                    tripFee.getToStop() : tripFee.getFromStop());
                            tripDataList.add(constructTripData(touchOn, incompleteTouchOff, chargeAmount, status));
                        }
                    }
                    touchOn.setProcessed(true);
                    touchService.saveTouchOn(touchOn);
                });
        tripDataRepository.saveAll(tripDataList);
    }

    public void exportUnprocessable() {
        fileService.writeToUnprocessableTripFile(tripDataRepository.findUnprocesable());
    }

    public void processSummaryData() {
        fileService.writeToSummaryFile(tripDataRepository.getSummaryData());
    }

    private String validateTouchOn(TouchOn touchOn) {
        String reason = null;
        if (Objects.isNull(touchOn.getDatetime())) {
            reason = "Invalid Date Time";
        } else if (StringUtils.isBlank(touchOn.getBusId())) {
            reason = "Invalid Bus ID";
        } else if (StringUtils.isBlank(touchOn.getCompanyId())) {
            reason = "Invalid Company ID";
        } else if (StringUtils.isBlank(touchOn.getStopId())) {
            reason = "Invalid Stop ID";
        } else if (StringUtils.isBlank(touchOn.getPan())) {
            reason = "Invalid PAN";
        }
//        touchOn.setReason(reason);
        return reason;
    }

    private String validateTouchOff(TouchOff touchOff) {
        String reason = null;
        if (Objects.isNull(touchOff.getDatetime())) {
            reason = "Invalid Date Time";
        } else if (StringUtils.isBlank(touchOff.getStopId())) {
            reason = "Invalid Stop ID";
        } else if (StringUtils.isBlank(touchOff.getPan())) {
            reason = "Invalid PAN";
        }
        return reason;
    }

    private TripData constructTripData(TouchOn touchOn, TouchOff touchOff, BigDecimal chargeAmount, TripStatus status) {
        return TripData.builder()
                .busId(touchOn.getBusId())
                .companyId(touchOn.getCompanyId())
                .fromStop(touchOn.getStopId())
                .toStop((touchOff.getStopId()))
                .startDatetime(touchOn.getDatetime())
                .endDatetime(touchOff.getDatetime())
                .chargeAmount(chargeAmount)
                .hashedPan(hashingService.hash(touchOn.getPan()))
                .status(status)
                .build();
    }

}
