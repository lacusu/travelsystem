package com.hv.exercise.travelsystem.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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

        private Input input;
        private Output output;
        private String datetimeFormat;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Input {

        private String touchFilePath;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Output {

        private String parentPath;
        private String tripsFileName;
        private String summaryFileName;
        private String unprocessableFileName;
    }
}
