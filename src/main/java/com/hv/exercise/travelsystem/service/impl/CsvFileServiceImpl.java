package com.hv.exercise.travelsystem.service.impl;

import com.hv.exercise.travelsystem.constant.TouchType;
import com.hv.exercise.travelsystem.model.TouchDataModel;
import com.hv.exercise.travelsystem.service.FileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CsvFileServiceImpl implements FileService {

    @Override
    public List<TouchDataModel> readTouchFile(String filePath) throws IOException {
        String[] HEADERS = {"ID", "DateTimeUTC", "TouchType", "StopID", "CompanyID", "BusID", "PAN"};
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();
        try (Reader reader = Files.newBufferedReader(Paths.get(filePath));
             CSVParser csvParser = new CSVParser(reader, csvFormat)) {
            List<TouchDataModel> touchDataModels = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser) {
                TouchDataModel touchDataModel = TouchDataModel.builder()
                        .id(Long.parseLong(StringUtils.normalizeSpace(csvRecord.get("ID"))))
                        .touchType(TouchType.valueOf(StringUtils.normalizeSpace(csvRecord.get("TouchType"))))
                        .stopId(StringUtils.normalizeSpace(csvRecord.get("StopID")))
                        .companyId(StringUtils.normalizeSpace(csvRecord.get("CompanyID")))
                        .busId(StringUtils.normalizeSpace(csvRecord.get("BusID")))
                        .pan(StringUtils.normalizeSpace(csvRecord.get("PAN")))
                        .build();
                // Define the format of the datetime string
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

                // Parse the datetime string to a LocalDateTime object
                LocalDateTime localDateTime =
                        LocalDateTime.parse(StringUtils.normalizeSpace(csvRecord.get("DateTimeUTC")), formatter);
                touchDataModel.setDatetime(localDateTime);
                touchDataModels.add(touchDataModel);
            }
            return touchDataModels;
        } catch (Exception exception) {
            log.error("Failed to read Touch File!");
            throw exception;
        }
    }
}
