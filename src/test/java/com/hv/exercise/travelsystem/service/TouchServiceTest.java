package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.entity.TouchOff;
import com.hv.exercise.travelsystem.entity.TouchOn;
import com.hv.exercise.travelsystem.repository.TouchOffRepository;
import com.hv.exercise.travelsystem.repository.TouchOnRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {TouchService.class})
@ExtendWith(SpringExtension.class)
class TouchServiceTest {

    @MockBean
    private TouchOnRepository touchOnRepository;

    @MockBean
    private TouchOffRepository touchOffRepository;

    @Autowired
    private TouchService touchService;

    @Captor
    ArgumentCaptor<List<TouchOn>> touchOnArgumentCaptor;

    @Captor
    ArgumentCaptor<List<TouchOff>> touchOffArgumentCaptor;

    @Test
    void testImportTouchOn_whenAllGood_thenDataInsertToDB() {
        //GIVEN
        TouchOn touchOn = TouchOn.builder().build();
        touchOn.setBusId("Bus10");
        touchOn.setPan("1234567890");
        touchOn.setProcessed(false);
        touchOn.setCompanyId("Company10");
        touchOn.setStopId("StopA");
        touchOn.setDatetime(LocalDateTime.now());

        //WHEN
        touchService.importTouchOn(Collections.singletonList(touchOn));

        //THEN
        verify(touchOnRepository).saveAll(touchOnArgumentCaptor.capture());

        List<TouchOn> savedTouchOns = touchOnArgumentCaptor.getValue();

        assertEquals(1, savedTouchOns.size());
        assertEquals(touchOn.getBusId(), savedTouchOns.get(0).getBusId());
        assertEquals(touchOn.getDatetime(), savedTouchOns.get(0).getDatetime());
        assertEquals(touchOn.getStopId(), savedTouchOns.get(0).getStopId());
        assertEquals(touchOn.getCompanyId(), savedTouchOns.get(0).getCompanyId());
        assertEquals(touchOn.getPan(), savedTouchOns.get(0).getPan());
    }

    @Test
    void testImportTouchOff_whenAllGood_thenDataInsertToDB() {
        //GIVEN
        TouchOff touchOff = TouchOff.builder().build();
        touchOff.setBusId("Bus10");
        touchOff.setPan("1234567890");
        touchOff.setProcessed(false);
        touchOff.setCompanyId("Company10");
        touchOff.setStopId("StopA");
        touchOff.setDatetime(LocalDateTime.now());

        //WHEN
        touchService.importTouchOff(Collections.singletonList(touchOff));

        //THEN
        verify(touchOffRepository).saveAll(touchOffArgumentCaptor.capture());

        List<TouchOff> savedTouchOffs = touchOffArgumentCaptor.getValue();

        assertEquals(1, savedTouchOffs.size());
        assertEquals(touchOff.getBusId(), savedTouchOffs.get(0).getBusId());
        assertEquals(touchOff.getDatetime(), savedTouchOffs.get(0).getDatetime());
        assertEquals(touchOff.getStopId(), savedTouchOffs.get(0).getStopId());
        assertEquals(touchOff.getCompanyId(), savedTouchOffs.get(0).getCompanyId());
        assertEquals(touchOff.getPan(), savedTouchOffs.get(0).getPan());
    }

    @Test
    void testGetUnprocessedTouchOn_whenFound_thenReturnList(){
        //GIVEN

        //WHEN
        List<TouchOn> touchOns = touchService.getUnprocessedTouchOn();

        //THEN
        assertEquals(1, touchOns.size());
    }

}
