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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {TriFeeService.class})
@ExtendWith(SpringExtension.class)
public class TripFeeServiceTest {

    @MockBean
    private TripFeeRepository tripFeeRepository;

    @Autowired
    private TriFeeService triFeeService;

    @Captor
    ArgumentCaptor<List<TripFee>> tripFeeArgumentCaptor;

    @Test
    void testImportTripFee_whenAllGood_thenDataInsertToDB() {
        //GIVEN
        TripFee tripFee = TripFee.builder()
                .startStop("StopA")
                .endStop(("StopB"))
                .fee(BigDecimal.valueOf(8.6))
                .build();

        //WHEN
        triFeeService.importTripFee(Collections.singletonList(tripFee));

        //THEN
        verify(tripFeeRepository).saveAll(tripFeeArgumentCaptor.capture());

        List<TripFee> savedTripFees = tripFeeArgumentCaptor.getValue();

        assertEquals(1, savedTripFees.size());
        assertEquals(tripFee.getStartStop(), savedTripFees.get(0).getStartStop());
        assertEquals(tripFee.getEndStop(), savedTripFees.get(0).getEndStop());
        assertEquals(tripFee.getFee(), savedTripFees.get(0).getFee());
    }

}
