package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.constant.TouchType;
import com.hv.exercise.travelsystem.constant.TripStatus;
import com.hv.exercise.travelsystem.entity.TouchData;
import com.hv.exercise.travelsystem.entity.TripData;
import com.hv.exercise.travelsystem.entity.TripFee;
import com.hv.exercise.travelsystem.repository.TripDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {TripService.class})
@ExtendWith(SpringExtension.class)
public class TripServiceTest {

    @Autowired
    private TripService tripService;

    @MockBean
    private TouchDataService touchDataService;

    @MockBean
    private TripFeeService tripFeeService;

    @MockBean
    private TripDataRepository tripDataRepository;

    @MockBean
    private HashingService hashingService;

    @Captor
    ArgumentCaptor<TripData> tripDataArgumentCaptor;


    @Captor
    ArgumentCaptor<List<TripData>> tripDataListArgumentCaptor;

    @Test
    void testProcessTrip_whenTripCanProcessCompleted_thenPersistRecord() {
        //GIVEN
        //Mocking the unprocessed Touch On
        TouchData touchOn = TouchData.builder()
                .busId("Bus1")
                .pan("123123")
                .isProcessed(false)
                .companyId("Company1")
                .datetime(LocalDateTime.of(2024, 2, 28, 10, 20, 10))
                .stopId("StopA")
                .type(TouchType.ON)
                .build();
        List<TouchData> unprocessedTouchOns = Collections.singletonList(touchOn);

        when(touchDataService.getUnprocessedTouchOns()).thenReturn(unprocessedTouchOns);

        //Mocking a Touch Off which match with the Touch On
        TouchData touchOff = TouchData.builder()
                .busId("Bus1")
                .pan("123123")
                .isProcessed(false)
                .companyId("Company1")
                .datetime(LocalDateTime.of(2024, 2, 28, 10, 25, 10))
                .stopId("StopC")
                .type(TouchType.OFF)
                .build();
        when(touchDataService.findTouchOff(touchOn.getCompanyId(), touchOn.getBusId(), touchOn.getDatetime()))
                .thenReturn(Optional.of(touchOff));

        //Mocking Trip Fee
        TripFee tripFee = TripFee.builder()
                .fromStop(touchOn.getStopId())
                .toStop(touchOff.getStopId())
                .fee(BigDecimal.valueOf(2.4))
                .build();
        when(tripFeeService.calculateTripFee(anyString(), anyString())).thenReturn(tripFee);

        //WHEN
        tripService.processTrip();

        //THEN
        verify(tripDataRepository).saveAll(tripDataListArgumentCaptor.capture());

        List<TripData> savedTripDataList = tripDataListArgumentCaptor.getValue();

        assertNotNull(savedTripDataList);
        assertEquals(1, savedTripDataList.size());
        assertEquals(touchOn.getStopId(), savedTripDataList.get(0).getFromStop());
        assertEquals(touchOn.getCompanyId(), savedTripDataList.get(0).getCompanyId());
        assertEquals(touchOn.getBusId(), savedTripDataList.get(0).getBusId());
        assertEquals(touchOff.getStopId(), savedTripDataList.get(0).getToStop());
        assertEquals(TripStatus.COMPLETED, savedTripDataList.get(0).getStatus());
        assertEquals(tripFee.getFee(), savedTripDataList.get(0).getChargeAmount());
    }

    @Test
    void testProcessTrip_whenTripCanProcessInComplete_thenPersistRecordWithMaxFeePosible() {
        //GIVEN
        //Mocking the unprocessed Touch On
        TouchData touchOn = TouchData.builder()
                .busId("Bus1")
                .pan("123123")
                .isProcessed(false)
                .companyId("Company1")
                .datetime(LocalDateTime.of(2024, 2, 28, 10, 20, 10))
                .stopId("StopA")
                .type(TouchType.ON)
                .build();
        List<TouchData> unprocessedTouchOns = Collections.singletonList(touchOn);

        when(touchDataService.getUnprocessedTouchOns()).thenReturn(unprocessedTouchOns);

        //Mocking a Touch Off for not found

        when(touchDataService.findTouchOff(touchOn.getCompanyId(), touchOn.getBusId(), touchOn.getDatetime()))
                .thenReturn(Optional.empty());

        //Mocking Trip Fee
        TripFee tripFee = TripFee.builder()
                .fromStop(touchOn.getStopId())
                .toStop("StopC")
                .fee(BigDecimal.valueOf(2.4))
                .build();
        when(tripFeeService.calculateTripFee(anyString(), any())).thenReturn(tripFee);

        //WHEN
        tripService.processTrip();

        //THEN
        verify(tripDataRepository).saveAll(tripDataListArgumentCaptor.capture());

        List<TripData> savedTripDataList = tripDataListArgumentCaptor.getValue();

        assertNotNull(savedTripDataList);
        assertEquals(1, savedTripDataList.size());
        assertEquals(touchOn.getStopId(), savedTripDataList.get(0).getFromStop());
        assertEquals(touchOn.getCompanyId(), savedTripDataList.get(0).getCompanyId());
        assertEquals(touchOn.getBusId(), savedTripDataList.get(0).getBusId());
        assertEquals(tripFee.getToStop(), savedTripDataList.get(0).getToStop());
        assertEquals(TripStatus.INCOMPLETE, savedTripDataList.get(0).getStatus());
        assertEquals(tripFee.getFee(), savedTripDataList.get(0).getChargeAmount());
    }

    @Test
    void testProcessTrip_whenTripCanProcessCancelled_thenPersistRecordWithAmountZero() {
        //GIVEN
        //Mocking the unprocessed Touch On
        TouchData touchOn = TouchData.builder()
                .busId("Bus1")
                .pan("123123")
                .isProcessed(false)
                .companyId("Company1")
                .datetime(LocalDateTime.of(2024, 2, 28, 10, 20, 10))
                .stopId("StopA")
                .type(TouchType.ON)
                .build();
        List<TouchData> unprocessedTouchOns = Collections.singletonList(touchOn);

        when(touchDataService.getUnprocessedTouchOns()).thenReturn(unprocessedTouchOns);

        //Mocking a Touch Off which match with the Touch On
        TouchData touchOff = TouchData.builder()
                .busId("Bus1")
                .pan("123123")
                .isProcessed(false)
                .companyId("Company1")
                .datetime(LocalDateTime.of(2024, 2, 28, 10, 25, 10))
                .stopId("StopA")
                .type(TouchType.OFF)
                .build();
        when(touchDataService.findTouchOff(touchOn.getCompanyId(), touchOn.getBusId(), touchOn.getDatetime()))
                .thenReturn(Optional.of(touchOff));

        //WHEN
        tripService.processTrip();

        //THEN
        verify(tripDataRepository).saveAll(tripDataListArgumentCaptor.capture());

        List<TripData> savedTripDataList = tripDataListArgumentCaptor.getValue();

        assertNotNull(savedTripDataList);
        assertEquals(1, savedTripDataList.size());
        assertEquals(touchOn.getStopId(), savedTripDataList.get(0).getFromStop());
        assertEquals(touchOn.getCompanyId(), savedTripDataList.get(0).getCompanyId());
        assertEquals(touchOn.getBusId(), savedTripDataList.get(0).getBusId());
        assertEquals(touchOff.getStopId(), savedTripDataList.get(0).getToStop());
        assertEquals(BigDecimal.ZERO, savedTripDataList.get(0).getChargeAmount());
        assertEquals(TripStatus.CANCELLED, savedTripDataList.get(0).getStatus());
    }

    @Test
    void testProcessTrip_whenTripCanNotProcess_thenPersistRecordWithStatusUnrocesable() {
        //GIVEN
        //Mocking the unprocessed Touch On
        TouchData touchOn = TouchData.builder()
                .busId("Bus1")
                .isProcessed(false)
                .companyId("Company1")
                .datetime(LocalDateTime.of(2024, 2, 28, 10, 20, 10))
                .stopId("StopA")
                .type(TouchType.ON)
                .build();
        List<TouchData> unprocessedTouchOns = Collections.singletonList(touchOn);

        when(touchDataService.getUnprocessedTouchOns()).thenReturn(unprocessedTouchOns);

        //Mocking a Touch Off which match with the Touch On
        TouchData touchOff = TouchData.builder()
                .busId("Bus1")
                .isProcessed(false)
                .companyId("Company1")
                .datetime(LocalDateTime.of(2024, 2, 28, 10, 25, 10))
                .stopId("StopA")
                .type(TouchType.OFF)
                .build();
        when(touchDataService.findTouchOff(touchOn.getCompanyId(), touchOn.getBusId(), touchOn.getDatetime()))
                .thenReturn(Optional.of(touchOff));

        //WHEN
        tripService.processTrip();

        //THEN
        verify(tripDataRepository).saveAll(tripDataListArgumentCaptor.capture());

        List<TripData> savedTripDataList = tripDataListArgumentCaptor.getValue();

        assertNotNull(savedTripDataList);
        assertEquals(1, savedTripDataList.size());
        assertEquals(touchOn.getStopId(), savedTripDataList.get(0).getFromStop());
        assertEquals(touchOn.getCompanyId(), savedTripDataList.get(0).getCompanyId());
        assertEquals(touchOn.getBusId(), savedTripDataList.get(0).getBusId());
        assertEquals(touchOff.getStopId(), savedTripDataList.get(0).getToStop());
        assertEquals(TripStatus.UNPROCESSABLE, savedTripDataList.get(0).getStatus());
        assertEquals("Touch On: Invalid PAN - Touch Off: Invalid PAN", savedTripDataList.get(0).getReason());
    }
}
