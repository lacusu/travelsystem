package com.hv.exercise.travelsystem;

import com.hv.exercise.travelsystem.service.FileService;
import com.hv.exercise.travelsystem.service.PreprocessDataService;
import com.hv.exercise.travelsystem.service.TripService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommandLineAppStartupRunner implements CommandLineRunner {
    @Autowired
    PreprocessDataService preprocessDataService;

    @Autowired
    TripService tripService;

    @Autowired
    FileService fileService;

    @Override
    public void run(String... args) throws Exception {
        preprocessDataService.importTouchData();
        tripService.calculateTripFee();
        fileService.writeToTripFile(tripService.getTripData());
        tripService.processSummaryData();
        tripService.exportUnprocessable();
    }
}
