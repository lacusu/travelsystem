package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.entity.TouchOff;
import com.hv.exercise.travelsystem.entity.TouchOn;
import com.hv.exercise.travelsystem.repository.TouchOffRepository;
import com.hv.exercise.travelsystem.repository.TouchOnRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TouchService {
    private TouchOnRepository touchOnRepository;

    private TouchOffRepository touchOffRepository;


    public void importTouchOn(List<TouchOn> touchOns) {
        touchOnRepository.saveAll(touchOns);
    }

    public void importTouchOff(List<TouchOff> touchOffs) {
        touchOffRepository.saveAll(touchOffs);
    }

    public TouchOn saveTouchOn(TouchOn touchOn) {
        return touchOnRepository.save(touchOn);
    }

    public TouchOff saveTouchOff(TouchOff touchOff) {
        return touchOffRepository.save(touchOff);
    }

    public List<TouchOn> getUnprocessedTouchOn() {
        return touchOnRepository.findByIsProcessedIsFalse();
    }

    public Optional<TouchOff> findTouchOff(String companyId, String bustId) {
        return touchOffRepository.findByCompanyIdAndBusIdAndIsProcessedIsFalseOrderByDatetime(companyId, bustId).stream()
                .findFirst();
    }
}
