package com.hv.exercise.travelsystem;

import com.hv.exercise.travelsystem.service.FileService;
import com.hv.exercise.travelsystem.service.TripService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommandLineAppStartupRunner implements CommandLineRunner {

    @Autowired
    TripService tripService;

    @Autowired
    FileService fileService;

    @Override
    public void run(String... args) throws Exception {
        log.info("====================> Phase 1. Modeling data and import to database...");
        fileService.importTouchData();

        log.info("====================> Phase 2. Processing data...");
        tripService.processTrip();

        log.info("====================> Phase 3. Exporting data to file...");
        fileService.writeToTripFile();
        fileService.writeToUnprocessableTripFile();
        fileService.writeToSummaryFile();
        log.info("<==================== DONE!");
    }
}
