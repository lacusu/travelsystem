package com.hv.exercise.travelsystem.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.config")
@Getter
@Setter
@NoArgsConstructor
public class AppConfig {
    private DataFile dataFile;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DataFile {

        private TouchDataFile touchDataFile;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TouchDataFile {

        private List<String> headers;

    }
}
