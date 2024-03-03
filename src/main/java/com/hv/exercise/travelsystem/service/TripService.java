package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.constant.TouchType;
import com.hv.exercise.travelsystem.constant.TripStatus;
import com.hv.exercise.travelsystem.entity.TouchData;
import com.hv.exercise.travelsystem.entity.TripData;
import com.hv.exercise.travelsystem.entity.TripFee;
import com.hv.exercise.travelsystem.model.TripSummary;
import com.hv.exercise.travelsystem.repository.TripDataRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class TripService {
    private TouchDataService touchDataService;

    private TripFeeService tripFeeService;

    private TripDataRepository tripDataRepository;

    private HashingService hashingService;

    public List<TripData> getTripData() {
        return tripDataRepository.findProceededTripData();
    }

    public void processTrip() {
        List<TouchData> touchData = touchDataService.getUnprocessedTouchOns();
        List<TripData> tripDataList = touchData
                .stream()
                .map(touchOn -> {
                    //Initializing Trip Data by Touch On Data
                    TripData tripData = constructTripData(touchOn, new TouchData(), BigDecimal.ZERO, TripStatus.UNPROCESSABLE);
                    String touchOnReason = validateTouchData(touchOn);
                    String touchOffReason = "";
                    //Finding Touch Off Stop for further process
                    try {
                        Optional<TouchData> touchOffOptional =
                                touchDataService.findTouchOff(touchOn.getCompanyId(), touchOn.getBusId(), touchOn.getDatetime());

                        if (touchOffOptional.isPresent()) {
                            //Full trip CASE
                            TouchData touchOff = touchOffOptional.get();
                            tripData = handleFullTrip(touchOn, touchOff);
                            if (StringUtils.isNotBlank(touchOff.getPan())
                                    && StringUtils.isNotBlank(touchOn.getPan())
                                    && !touchOn.getPan().equalsIgnoreCase(touchOff.getPan())) {
                                touchOffReason = "PAN mismatched";
                            } else {
                                touchOffReason = validateTouchData(touchOff);
                            }

                        } else {
                            //Incomplete Trip CASE
                            tripData = handleIncompleteTrip(touchOn);
                        }
                    } catch (Exception exception) {
                        touchOffReason = exception.getMessage();
                    }
                    //Mask the Touch On as processed
                    setTouchDataProcessed(touchOn);
                    return finalizeTripData(tripData, touchOnReason, touchOffReason);
                }).toList();
        //Persist all the trip data
        tripDataRepository.saveAll(tripDataList);
    }

    public List<TripData> getUnprocesableTrip() {
        log.info("Getting unprocessed trips");
        List<TripData> tripDataList = tripDataRepository.findUnprocesable();

        List<TripData> unprocessedTripByTouchData = touchDataService.getUnprocessedTouches().stream()
                .map(touchData -> {
                    TripData tripData;
                    if (TouchType.ON.equals(touchData.getType())) {
                        tripData = constructTripData(touchData, new TouchData(), BigDecimal.ZERO, TripStatus.UNPROCESSABLE);
                        String reason = validateTouchData(touchData);
                        tripData.setReason(String.format("Touch On: %s", reason));
                    } else {
                        tripData = constructTripData(new TouchData(), touchData, BigDecimal.ZERO, TripStatus.UNPROCESSABLE);
                        String reason = validateTouchData(touchData);
                        tripData.setReason(String.format("Touch Off: %s", StringUtils.isNotBlank(reason) ? reason :
                                "Invalid data"));
                    }
                    return tripData;
                }).toList();
        tripDataList.addAll(unprocessedTripByTouchData);
        return tripDataList;
    }

    public List<TripSummary> getSummaryTripData() {
        log.info("Getting summary data trips");
        return tripDataRepository.getSummaryData();
    }

    private TripData finalizeTripData(TripData tripData, String touchOnReason, String touchOffReason) {
        if (StringUtils.isNotBlank(touchOnReason)) {
            touchOnReason = String.format("Touch On: %s", touchOnReason);
        }
        String reason = touchOnReason;
        if (StringUtils.isNotBlank(touchOffReason)) {
            touchOffReason = String.format("Touch Off: %s", touchOffReason);

            if (StringUtils.isNotBlank(reason)) {
                reason = StringUtils.joinWith(" - ", reason, touchOffReason);
            } else {
                reason = touchOffReason;
            }
        }
        if (StringUtils.isNotBlank(reason)) {
            tripData.setReason(reason);
            tripData.setStatus(TripStatus.UNPROCESSABLE);
        }
        return tripData;
    }

    private void setTouchDataProcessed(TouchData touchData) {
        log.info("Marking Touch [{}] Id [{}] as processed", touchData.getType(), touchData.getId());
        touchData.setProcessed(true);
        touchDataService.saveTouchData(touchData);
    }

    private TripData handleIncompleteTrip(TouchData touchOn) {
        log.info("Handling incomplete trip - company id [{}] - bus id [{}] - date time [{}]",
                touchOn.getCompanyId(), touchOn.getBusId(), touchOn.getDatetime());
        TripFee tripFee = tripFeeService.calculateTripFee(touchOn.getStopId(), null);
        BigDecimal chargeAmount = tripFee.getFee();
        TouchData incompleteTouchOff = TouchData.builder().build();
        incompleteTouchOff.setStopId(touchOn.getStopId().equalsIgnoreCase(tripFee.getFromStop()) ?
                tripFee.getToStop() : tripFee.getFromStop());
        return constructTripData(touchOn, incompleteTouchOff, chargeAmount, TripStatus.INCOMPLETE);
    }

    private TripData handleFullTrip(TouchData touchOn, TouchData touchOff) {
        log.info("Handling full trip - company id [{}] - bus id [{}] - date time [{}] - from stop [{}] to stop [{}]",
                touchOn.getCompanyId(), touchOn.getBusId(),
                touchOn.getDatetime(), touchOn.getStopId(), touchOff.getStopId());

        BigDecimal chargeAmount;
        TripStatus status;

        if (touchOff.getStopId().equalsIgnoreCase(touchOn.getStopId())) {
            //Cancelled Case
            chargeAmount = BigDecimal.ZERO;
            status = TripStatus.CANCELLED;
        } else {
            //Completed Case
            chargeAmount = tripFeeService.calculateTripFee(touchOn.getStopId(), touchOff.getStopId()).getFee();
            status = TripStatus.COMPLETED;
        }

        TripData tripData = constructTripData(touchOn, touchOff, chargeAmount, status);
        //Mask the Touch Off as processed
        setTouchDataProcessed(touchOff);
        return tripData;
    }

    private String validateTouchData(TouchData touchData) {
        String reason = null;
        if (Objects.isNull(touchData.getDatetime())) {
            reason = "Invalid Date Time";
        } else if (StringUtils.isBlank(touchData.getBusId())) {
            reason = "Invalid Bus ID";
        } else if (StringUtils.isBlank(touchData.getCompanyId())) {
            reason = "Invalid Company ID";
        } else if (StringUtils.isBlank(touchData.getStopId())) {
            reason = "Invalid Stop ID";
        } else if (StringUtils.isBlank(touchData.getPan())) {
            reason = "Invalid PAN";
        }
        return reason;
    }

    private TripData constructTripData(TouchData touchOn, TouchData touchOff, BigDecimal chargeAmount, TripStatus
            status) {
        String hashedPan = hashingService.hashSHA256(StringUtils.isNotBlank(touchOn.getPan()) ?
                touchOn.getPan() :
                touchOff.getPan());

        String busId = StringUtils.isNotBlank(touchOn.getBusId()) ? touchOn.getBusId() : touchOff.getBusId();
        String companyId = StringUtils.isNotBlank(touchOn.getCompanyId()) ?
                touchOn.getCompanyId() :
                touchOff.getCompanyId();

        return TripData.builder()
                .busId(busId)
                .companyId(companyId)
                .fromStop(touchOn.getStopId())
                .startDatetime(touchOn.getDatetime())
                .hashedPan(hashedPan)
                .toStop((touchOff.getStopId()))
                .endDatetime(touchOff.getDatetime())
                .chargeAmount(chargeAmount)
                .status(status)
                .build();
    }

}
