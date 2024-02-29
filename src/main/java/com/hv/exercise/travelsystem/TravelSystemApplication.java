package com.hv.exercise.travelsystem;

import com.hv.exercise.travelsystem.service.PreprocessDataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@Slf4j
@AllArgsConstructor
public class TravelSystemApplication {

    @Autowired
    private static PreprocessDataService preprocessDataService;

    public static void main(String[] args) {
        SpringApplication.run(TravelSystemApplication.class, args);
        log.info("Importing touchData.csv file to DB");

        log.info("I'm done. Byte! ==================================");
    }
}
