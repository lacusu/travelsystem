package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.entity.TripData;
import com.hv.exercise.travelsystem.repository.TripDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {TripService.class})
@ExtendWith(SpringExtension.class)
public class TripServiceTest {

    @Autowired
    private TripService tripService;

    @MockBean
    private TripDataRepository tripDataRepository;

    @Captor
    ArgumentCaptor<TripData> tripDataArgumentCaptor;

    @Test
    void testCalculateTripFee_whenAllGood_thenPersistRecord(){
        //GIVEN

        //WHEN
        tripService.calculateTripFee();

        //THEN
        verify(tripDataRepository).save(tripDataArgumentCaptor.capture());

        TripData savedTripData = tripDataArgumentCaptor.capture();

        assertNotNull(savedTripData);
    }
}
