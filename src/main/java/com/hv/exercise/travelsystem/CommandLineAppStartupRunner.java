package com.hv.exercise.travelsystem;

import com.hv.exercise.travelsystem.service.PreprocessDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    @Autowired
    PreprocessDataService preprocessDataService;

    @Override
    public void run(String... args) throws Exception {
        preprocessDataService.importTouchData();

    }
}
