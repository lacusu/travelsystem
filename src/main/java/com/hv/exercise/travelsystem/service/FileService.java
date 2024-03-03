package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.config.AppConfig;
import com.hv.exercise.travelsystem.constant.TouchType;
import com.hv.exercise.travelsystem.entity.TouchData;
import com.hv.exercise.travelsystem.model.TouchDataModel;
import com.hv.exercise.travelsystem.util.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FileService {

    private TripService tripService;

    private TouchDataService touchDataService;

    private AppConfig appConfig;

    public void importTouchData() throws IOException {
        List<TouchDataModel> touchDataModels = readTouchFile();
        List<TouchData> touchDataList = touchDataModels.stream()
                .map(this::buildTouchData)
                .toList();
        touchDataService.saveAll(touchDataList);
    }

    public void writeToTripFile() {
        String[] HEADERS = {"started", "finished", "DurationSec", "fromStopId", "toStopId", "ChargeAmount", "CompanyId", "BusId", "HashedPan", "Status"};
        String pathToFile = buildOutputFilePath(appConfig.getDataFile().getOutput().getTripsFileName());
        log.info("Writing data to file: {}", pathToFile);
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .build();
        try (Writer writer = new FileWriter(pathToFile);
             CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
            tripService.getTripData().forEach(tripData -> {
                try {
                    printer.printRecord(
                            convertDataTimeToString(tripData.getStartDatetime()),
                            convertDataTimeToString(tripData.getEndDatetime()),
                            getDurationInSecond(tripData.getStartDatetime(), tripData.getEndDatetime()),
                            tripData.getFromStop(),
                            tripData.getToStop(),
                            tripData.getChargeAmount(),
                            tripData.getCompanyId(),
                            tripData.getBusId(),
                            tripData.getHashedPan(),
                            tripData.getStatus()
                    );
                } catch (IOException e) {
                    log.error("Failed to write a record with company id [{}], bus id [{}] to file: {}",
                            tripData.getCompanyId(), tripData.getBusId(), pathToFile);
                }
            });
        } catch (IOException e) {
            log.error("Failed to Write to File: {}", pathToFile, e);
        }
    }

    public void writeToUnprocessableTripFile() {
        String[] HEADERS = {"started", "finished", "DurationSec", "fromStopId", "toStopId", "ChargeAmount", "CompanyId", "BusId", "HashedPan", "Status"};
        String pathToFile = buildOutputFilePath(appConfig.getDataFile().getOutput().getUnprocessableFileName());
        log.info("Writing data to file: {}", pathToFile);
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .build();
        try (Writer writer = new FileWriter(pathToFile);
             CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
            tripService.getUnprocesableTrip().forEach(tripData -> {
                try {
                    printer.printRecord(
                            convertDataTimeToString(tripData.getStartDatetime()),
                            convertDataTimeToString(tripData.getEndDatetime()),
                            getDurationInSecond(tripData.getStartDatetime(), tripData.getEndDatetime()),
                            tripData.getFromStop(),
                            tripData.getToStop(),
                            tripData.getChargeAmount(),
                            tripData.getCompanyId(),
                            tripData.getBusId(),
                            tripData.getHashedPan(),
                            tripData.getReason()
                    );
                } catch (IOException e) {
                    log.error("Failed to write a record company id [{}], bus id [{}] to file: {}",
                            tripData.getCompanyId(), tripData.getBusId(), pathToFile);
                }
            });
        } catch (IOException e) {
            log.error("Failed to Write to File: {}", pathToFile);
        }
    }

    public void writeToSummaryFile() {
        String[] HEADERS = {"date", "CompanyId", "BusId", "CompleteTripCount", "IncompleteTripCount", "CancelledTripCount", "TotalCharges"};
        String pathToFile = buildOutputFilePath(appConfig.getDataFile().getOutput().getSummaryFileName());
        log.info("Writing data to file: {}", pathToFile);
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .build();
        try (Writer writer = new FileWriter(pathToFile);
             CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
            tripService.getSummaryTripData().forEach(tripSummary -> {
                try {
                    printer.printRecord(
                            tripSummary.getTripDate(),
                            tripSummary.getCompanyId(),
                            tripSummary.getBusId(),
                            tripSummary.getCompleteTripCount(),
                            tripSummary.getIncompleteTripCount(),
                            tripSummary.getCancelledTripCount(),
                            tripSummary.getTotalCharges()
                    );
                } catch (IOException e) {
                    log.error("Failed to write a record company id [{}], bus id [{}] to file: {}",
                            tripSummary.getCompanyId(), tripSummary.getBusId(), pathToFile);
                }
            });
        } catch (IOException e) {
            log.error("Failed to Write to File: {}", pathToFile);
        }
    }

    private List<TouchDataModel> readTouchFile() throws IOException {
        String pathToFile = appConfig.getDataFile().getInput().getTouchFilePath();
        log.info("Reading data from file: {}", pathToFile);
        String[] HEADERS = {"ID", "DateTimeUTC", "TouchType", "StopID", "CompanyID", "BusID", "PAN"};
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();
        try (Reader reader = Files.newBufferedReader(Paths.get(pathToFile));
             CSVParser csvParser = new CSVParser(reader, csvFormat)) {
            List<TouchDataModel> touchDataModels = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser) {
                TouchDataModel touchDataModel = TouchDataModel.builder()
                        .id(Long.parseLong(StringUtils.normalizeSpace(csvRecord.get("ID"))))
                        .touchType(TouchType.valueOf(StringUtils.normalizeSpace(csvRecord.get("TouchType")).toUpperCase()))
                        .stopId(StringUtils.normalizeSpace(csvRecord.get("StopID")))
                        .companyId(StringUtils.normalizeSpace(csvRecord.get("CompanyID")))
                        .busId(StringUtils.normalizeSpace(csvRecord.get("BusID")))
                        .pan(StringUtils.normalizeSpace(csvRecord.get("PAN")))
                        .build();

                String datetimeString = StringUtils.normalizeSpace(csvRecord.get("DateTimeUTC"));
                LocalDateTime dateTime = DateTimeUtil.convertStringToLocalDateTime(datetimeString,
                        appConfig.getDataFile().getDatetimeFormat());
                touchDataModel.setDatetime(dateTime);
                touchDataModels.add(touchDataModel);
            }
            return touchDataModels;
        } catch (Exception exception) {
            log.error("Failed to read Touch File!");
            throw exception;
        }
    }

    private TouchData buildTouchData(TouchDataModel touchDataModel) {
        return TouchData.builder()
                .stopId(touchDataModel.getStopId())
                .datetime(touchDataModel.getDatetime())
                .companyId(touchDataModel.getCompanyId())
                .busId(touchDataModel.getBusId())
                .pan(touchDataModel.getPan())
                .type(touchDataModel.getTouchType()).build();
    }

    private String buildOutputFilePath(String fileName) {
        return String.format("%s%s", appConfig.getDataFile().getOutput().getParentPath(), fileName);
    }

    private String convertDataTimeToString(LocalDateTime dateTime) {
        try {
            return DateTimeUtil.convertLocalDateTimeToString(dateTime, appConfig.getDataFile().getDatetimeFormat());
        } catch (Exception e) {
            //We Accept Null in same cases
            return null;
        }
    }

    public static long getDurationInSecond(LocalDateTime start, LocalDateTime end) {
        try {
            return DateTimeUtil.getDurationInSecond(start, end);
        } catch (Exception e) {
            //We Accept 0
            return 0L;
        }
    }
}
