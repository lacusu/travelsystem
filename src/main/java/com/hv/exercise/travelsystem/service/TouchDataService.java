package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.entity.TouchData;
import com.hv.exercise.travelsystem.repository.TouchDataRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class TouchDataService {
    private TouchDataRepository touchDataRepository;

    public void saveAll(List<TouchData> touchData) {
        touchDataRepository.saveAll(touchData);
    }

    public TouchData saveTouchData(TouchData touchData) {
        return touchDataRepository.save(touchData);
    }

    public List<TouchData> getUnprocessedTouchOns() {
        log.info("Getting unprocessed touch-ons");
        return touchDataRepository.getUnprocessedTouchOns();
    }

    public Optional<TouchData> findTouchOff(String companyId, String busId, LocalDateTime touchOnTime) {
        log.info("Finding Touch Off by company id [{}] and bus id [{}]", companyId, busId);
        if (StringUtils.isBlank(companyId) || StringUtils.isBlank(busId)) {
            return Optional.empty();
        }
        return touchDataRepository.getTouchOff(companyId, busId, touchOnTime);

    }

    public List<TouchData> getUnprocessedTouches() {
        log.info("Getting unprocessed touches");
        return touchDataRepository.getUnprocessedTouches();
    }
}
