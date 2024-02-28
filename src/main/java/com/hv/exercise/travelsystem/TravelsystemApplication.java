package com.hv.exercise.travelsystem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@Slf4j
public class TravelsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelsystemApplication.class, args);




		log.info("I'm done. Byte! ==================================");
	}

}
