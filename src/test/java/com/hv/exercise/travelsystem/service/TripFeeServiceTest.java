package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.entity.TripFee;
import com.hv.exercise.travelsystem.repository.TripFeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {TripFeeService.class})
@ExtendWith(SpringExtension.class)
class TripFeeServiceTest {

    @MockBean
    private TripFeeRepository tripFeeRepository;

    @Autowired
    private TripFeeService tripFeeService;

    @Captor
    ArgumentCaptor<String> fromStopArgument;

    @Captor
    ArgumentCaptor<String> toStopArgument;

    @Test
    void testCalculateTripFee_whenFullStopsProvided_thenReturnTheRightFee() {
        //GIVEN
        TripFee tripFee = TripFee.builder()
                .fromStop("StopA")
                .toStop(("StopB"))
                .fee(BigDecimal.valueOf(8.6))
                .build();
        when(tripFeeRepository.findTripFee("StopA", "StopB")).thenReturn(Optional.of(tripFee));

        //WHEN
        TripFee returnedTripFee = tripFeeService.calculateTripFee("StopA", "StopB");

        //THEN

        assertEquals(tripFee.getFromStop(), returnedTripFee.getFromStop());
        assertEquals(tripFee.getToStop(), returnedTripFee.getToStop());
        assertEquals(tripFee.getFee(), returnedTripFee.getFee());

        //verify repository argument
        verify(tripFeeRepository).findTripFee(fromStopArgument.capture(), toStopArgument.capture());
        assertEquals(tripFee.getFromStop(), fromStopArgument.getValue());
        assertEquals(tripFee.getToStop(), toStopArgument.getValue());
    }

    @Test
    void testCalculateTripFee_whenFromStopProvided_thenReturnTheMaxFee() {
        //GIVEN
        TripFee tripFee = TripFee.builder()
                .fromStop("StopA")
                .toStop(("StopB"))
                .fee(BigDecimal.valueOf(8.6))
                .build();
        when(tripFeeRepository.findMaxTripFeeBySingleStop("StopA")).thenReturn(Optional.of(tripFee));

        //WHEN
        TripFee returnedTripFee = tripFeeService.calculateTripFee("StopA", null);

        //THEN

        assertEquals(tripFee.getFromStop(), returnedTripFee.getFromStop());
        assertEquals(tripFee.getToStop(), returnedTripFee.getToStop());
        assertEquals(tripFee.getFee(), returnedTripFee.getFee());

        //verify repository argument
        verify(tripFeeRepository).findMaxTripFeeBySingleStop(fromStopArgument.capture());
        assertEquals(tripFee.getFromStop(), fromStopArgument.getValue());
    }

    @Test
    void testCalculateTripFee_whenToStopProvided_thenReturnTheMaxFee() {
        //GIVEN
        TripFee tripFee = TripFee.builder()
                .fromStop("StopA")
                .toStop(("StopB"))
                .fee(BigDecimal.valueOf(8.6))
                .build();
        when(tripFeeRepository.findMaxTripFeeBySingleStop("StopB")).thenReturn(Optional.of(tripFee));

        //WHEN
        TripFee returnedTripFee = tripFeeService.calculateTripFee(null, "StopB");

        //THEN

        assertEquals(tripFee.getFromStop(), returnedTripFee.getFromStop());
        assertEquals(tripFee.getToStop(), returnedTripFee.getToStop());
        assertEquals(tripFee.getFee(), returnedTripFee.getFee());

        //verify repository argument
        verify(tripFeeRepository).findMaxTripFeeBySingleStop(fromStopArgument.capture());
        assertEquals(tripFee.getToStop(), fromStopArgument.getValue());
    }

}
