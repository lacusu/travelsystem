package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.config.AppConfig;
import com.hv.exercise.travelsystem.constant.TouchType;
import com.hv.exercise.travelsystem.constant.TripStatus;
import com.hv.exercise.travelsystem.entity.TouchData;
import com.hv.exercise.travelsystem.entity.TripData;
import com.hv.exercise.travelsystem.model.TripSummary;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {FileService.class})
@ExtendWith(SpringExtension.class)
class FileServiceTest {

    private static final String inputDataTimeFormat = "dd-MM-yyyy HH:mm:ss";
    @Autowired
    private FileService fileService;

    @MockBean
    private TouchDataService touchDataService;

    @MockBean
    private TripService tripService;

    @MockBean
    private AppConfig appConfig;

    @Captor
    ArgumentCaptor<List<TouchData>> touchDataArgumentCaptor;

    @Test
    void testImportTouchData_whenGoodFile_thenReturnItems() throws IOException {
        //GIVEN
        String pathToFile = "src/test/resources/file-service/touchData.csv";

        AppConfig.Input input = new AppConfig.Input();
        input.setTouchFilePath(pathToFile);
        AppConfig.DataFile dataFile = new AppConfig.DataFile();
        dataFile.setInput(input);
        dataFile.setDatetimeFormat(inputDataTimeFormat);
        when(appConfig.getDataFile()).thenReturn(dataFile);

        //WHEN
        fileService.importTouchData();

        //THEN
        verify(touchDataService).saveAll(touchDataArgumentCaptor.capture());
        List<TouchData> savedTouchDataList = touchDataArgumentCaptor.getValue();

        assertNotNull(savedTouchDataList);
        assertEquals(2, savedTouchDataList.size());

        //Verifying for 1st record
        int firstRecordIndex = 0;
        assertEquals(LocalDateTime.of(2024, 2, 28, 10, 10, 10),
                savedTouchDataList.get(firstRecordIndex).getDatetime());
        assertEquals(TouchType.ON, savedTouchDataList.get(firstRecordIndex).getType());
        assertEquals("Company1", savedTouchDataList.get(firstRecordIndex).getCompanyId());
        assertEquals("Bus10", savedTouchDataList.get(firstRecordIndex).getBusId());
        assertEquals("StopA", savedTouchDataList.get(firstRecordIndex).getStopId());
        assertEquals("1234556789", savedTouchDataList.get(firstRecordIndex).getPan());

        //Verifying for 2nd record
        int secondRecordIndex = 1;
        assertEquals(LocalDateTime.of(2024, 2, 28, 10, 20, 10),
                savedTouchDataList.get(secondRecordIndex).getDatetime());
        assertEquals(TouchType.OFF, savedTouchDataList.get(secondRecordIndex).getType());
        assertEquals("Company1", savedTouchDataList.get(secondRecordIndex).getCompanyId());
        assertEquals("Bus10", savedTouchDataList.get(secondRecordIndex).getBusId());
        assertEquals("StopA", savedTouchDataList.get(secondRecordIndex).getStopId());
        assertEquals("1234556789", savedTouchDataList.get(secondRecordIndex).getPan());

    }

    @Test
    void testImportTouchData_whenNoRecord_thenReturnEmptyList() throws IOException {
        //GIVEN
        String pathToFile = "src/test/resources/file-service/touchDataNoRecord.csv";

        AppConfig.Input input = new AppConfig.Input();
        input.setTouchFilePath(pathToFile);
        AppConfig.DataFile dataFile = new AppConfig.DataFile();
        dataFile.setInput(input);
        dataFile.setDatetimeFormat(inputDataTimeFormat);
        when(appConfig.getDataFile()).thenReturn(dataFile);

        //WHEN
        fileService.importTouchData();

        //THEN
        verify(touchDataService).saveAll(touchDataArgumentCaptor.capture());
        List<TouchData> savedTouchDataList = touchDataArgumentCaptor.getValue();

        assertNotNull(savedTouchDataList);
        assertEquals(0, savedTouchDataList.size());

    }

    @Test
    void testImportTouchData_whenEmptyFile_thenReturnEmptyList() throws IOException {
        //GIVEN
        String pathToFile = "src/test/resources/file-service/touchDataEmpty.csv";

        AppConfig.Input input = new AppConfig.Input();
        input.setTouchFilePath(pathToFile);
        AppConfig.DataFile dataFile = new AppConfig.DataFile();
        dataFile.setInput(input);
        dataFile.setDatetimeFormat(inputDataTimeFormat);
        when(appConfig.getDataFile()).thenReturn(dataFile);

        //WHEN
        fileService.importTouchData();

        //THEN
        verify(touchDataService).saveAll(touchDataArgumentCaptor.capture());
        List<TouchData> savedTouchDataList = touchDataArgumentCaptor.getValue();

        assertNotNull(savedTouchDataList);
        assertEquals(0, savedTouchDataList.size());


    }

    @Test
    void testImportTouchData_whenFileNotFound_thenThrowException() {
        //GIVEN
        String pathToFile = "PathToFile";

        //WHEN & THEN
        assertThrows(Exception.class, () -> fileService.importTouchData());

    }

    @Test
    void testWriteToTripFile_whenDataFine_thenCreateCsvFile() throws IOException {
        //GIVEN
        //Mocking Data File Config
        //The file name is generated random for each test and remove after test
        String fileName = String.format("%s-Trips.csv", UUID.randomUUID());
        String parentPath = "src/test/resources/file-service/out/";
        AppConfig.Output output = new AppConfig.Output();
        output.setParentPath(parentPath);
        output.setTripsFileName(fileName);
        AppConfig.DataFile dataFile = new AppConfig.DataFile();
        dataFile.setOutput(output);
        dataFile.setDatetimeFormat(inputDataTimeFormat);
        when(appConfig.getDataFile()).thenReturn(dataFile);

        //Creating 3 types of trip
        TripData completedTripData = TripData.builder()
                .startDatetime(LocalDateTime.of(2024, 2, 28, 10, 20, 10))
                .endDatetime(LocalDateTime.of(2024, 2, 28, 10, 25, 10))
                .companyId("Company1")
                .busId("Bus1")
                .fromStop("StopA")
                .toStop("StopB")
                .status(TripStatus.COMPLETED)
                .chargeAmount(BigDecimal.valueOf(10))
                .hashedPan("hashed")
                .build();
        TripData incompleteTripData = TripData.builder()
                .startDatetime(LocalDateTime.of(2024, 2, 28, 10, 20, 10))
                .companyId("Company2")
                .busId("Bus2")
                .fromStop("StopA")
                .toStop("StopC")
                .status(TripStatus.INCOMPLETE)
                .chargeAmount(BigDecimal.valueOf(20))
                .hashedPan("hashed")
                .build();
        TripData cancelledTripData = TripData.builder()
                .startDatetime(LocalDateTime.of(2024, 2, 28, 10, 20, 10))
                .endDatetime(LocalDateTime.of(2024, 2, 28, 10, 20, 10))
                .companyId("Company3")
                .busId("Bus3")
                .fromStop("StopA")
                .toStop("StopA")
                .status(TripStatus.CANCELLED)
                .chargeAmount(BigDecimal.ZERO)
                .hashedPan("hashed")
                .build();
        List<TripData> tripDataList = new ArrayList<>();
        tripDataList.add(completedTripData);
        tripDataList.add(incompleteTripData);
        tripDataList.add(cancelledTripData);

        when(tripService.getTripData()).thenReturn(tripDataList);

        //WHEN
        fileService.writeToTripFile();

        //THEN
        String pathToFile = parentPath + fileName;
        String[] HEADERS = {"started", "finished", "DurationSec", "fromStopId", "toStopId", "ChargeAmount", "CompanyId", "BusId", "HashedPan", "Status"};
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();
        try (Reader reader = Files.newBufferedReader(Paths.get(pathToFile));
             CSVParser csvParser = new CSVParser(reader, csvFormat)) {

            int index = 0;
            for (CSVRecord csvRecord : csvParser) {
                // Assert each record in the CSV file
                assertEquals(tripDataList.get(index).getFromStop(), csvRecord.get(3));
                assertEquals(tripDataList.get(index).getToStop(), csvRecord.get(4));
                assertEquals(tripDataList.get(index).getChargeAmount(), BigDecimal.valueOf(Long.parseLong(csvRecord.get(5))));
                assertEquals(tripDataList.get(index).getCompanyId(), csvRecord.get(6));
                assertEquals(tripDataList.get(index).getBusId(), csvRecord.get(7));
                assertEquals(tripDataList.get(index).getHashedPan(), csvRecord.get(8));
                assertEquals(tripDataList.get(index).getStatus(), TripStatus.valueOf(csvRecord.get(9)));
                index++;
            }
        } catch (Exception exception) {
            assertNotNull(exception);
        } finally {
            // Clean up: Delete the temporary CSV file
            deleteTestingFile(pathToFile);

        }
    }

    @Test
    void testWriteToUnprocessableTripFile_whenDataFine_thenCreateCsvFile() throws IOException {
        //GIVEN
        //Mocking Data File Config
        //The file name is generated random for each test and remove after test
        String fileName = String.format("%s-unprocessedFile.csv", UUID.randomUUID());
        String parentPath = "src/test/resources/file-service/out/";
        AppConfig.Output output = new AppConfig.Output();
        output.setParentPath(parentPath);
        output.setUnprocessableFileName(fileName);
        AppConfig.DataFile dataFile = new AppConfig.DataFile();
        dataFile.setOutput(output);
        dataFile.setDatetimeFormat(inputDataTimeFormat);
        when(appConfig.getDataFile()).thenReturn(dataFile);

        //Creating 3 types of trip
        TripData unprocessedTripData = TripData.builder()
                .startDatetime(LocalDateTime.of(2024, 2, 28, 10, 20, 10))
                .endDatetime(LocalDateTime.of(2024, 2, 28, 10, 25, 10))
                .companyId("Company1")
                .busId("Bus1")
                .fromStop("StopA")
                .toStop("StopB")
                .status(TripStatus.UNPROCESSABLE)
                .chargeAmount(BigDecimal.valueOf(10))
                .reason("PAN is mismatched")
                .build();

        List<TripData> tripDataList = new ArrayList<>();
        tripDataList.add(unprocessedTripData);

        when(tripService.getTripData()).thenReturn(tripDataList);

        //WHEN
        fileService.writeToUnprocessableTripFile();

        //THEN
        String pathToFile = parentPath + fileName;
        String[] HEADERS = {"started", "finished", "DurationSec", "fromStopId", "toStopId", "ChargeAmount", "CompanyId", "BusId", "HashedPan", "Status"};
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();
        try (Reader reader = Files.newBufferedReader(Paths.get(pathToFile));
             CSVParser csvParser = new CSVParser(reader, csvFormat)) {

            int index = 0;
            for (CSVRecord csvRecord : csvParser) {
                // Assert each record in the CSV file
                assertEquals(tripDataList.get(index).getFromStop(), csvRecord.get(3));
                assertEquals(tripDataList.get(index).getToStop(), csvRecord.get(4));
                assertEquals(tripDataList.get(index).getChargeAmount(), BigDecimal.valueOf(Long.parseLong(csvRecord.get(5))));
                assertEquals(tripDataList.get(index).getCompanyId(), csvRecord.get(6));
                assertEquals(tripDataList.get(index).getBusId(), csvRecord.get(7));
                assertEquals(tripDataList.get(index).getHashedPan(), csvRecord.get(8));
                assertEquals(tripDataList.get(index).getStatus(), TripStatus.valueOf(csvRecord.get(9)));
                index++;
            }
        } catch (Exception exception) {
            assertNotNull(exception);
        } finally {
            // Clean up: Delete the temporary CSV file
            deleteTestingFile(pathToFile);

        }
    }

    @Test
    void testWriteToSummaryFile_whenDataFine_thenCreateCsvFile() throws IOException {
        //GIVEN
        //Mocking Data File Config
        //The file name is generated random for each test and remove after test
        String fileName = String.format("%s-summary.csv", UUID.randomUUID());
        String parentPath = "src/test/resources/file-service/out/";
        AppConfig.Output output = new AppConfig.Output();
        output.setParentPath(parentPath);
        output.setSummaryFileName(fileName);
        AppConfig.DataFile dataFile = new AppConfig.DataFile();
        dataFile.setOutput(output);
        dataFile.setDatetimeFormat(inputDataTimeFormat);
        when(appConfig.getDataFile()).thenReturn(dataFile);

        //Creating 3 types of trip
        TripSummary tripSummary = TripSummary.builder()
                .tripDate(Date.valueOf("2024-02-28"))
                .companyId("Company1")
                .busId("Bus1")
                .completeTripCount(10L)
                .incompleteTripCount(2L)
                .cancelledTripCount(3L)
                .totalCharges(BigDecimal.valueOf(20.3))
                .build();

        List<TripSummary> tripSummaries = new ArrayList<>();
        tripSummaries.add(tripSummary);

        when(tripService.getSummaryTripData()).thenReturn(tripSummaries);

        //WHEN
        fileService.writeToSummaryFile();

        //THEN
        String pathToFile = parentPath + fileName;
        String[] HEADERS = {"date", "CompanyId", "BusId", "CompleteTripCount", "IncompleteTripCount", "CancelledTripCount", "TotalCharges"};
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();
        try (Reader reader = Files.newBufferedReader(Paths.get(pathToFile));
             CSVParser csvParser = new CSVParser(reader, csvFormat)) {

            int index = 0;
            for (CSVRecord csvRecord : csvParser) {
                // Assert each record in the CSV file
                assertEquals(tripSummaries.get(index).getCompanyId(), csvRecord.get(1));
                assertEquals(tripSummaries.get(index).getBusId(), csvRecord.get(2));
                assertEquals(tripSummaries.get(index).getCompleteTripCount(), Long.parseLong(csvRecord.get(3)));
                assertEquals(tripSummaries.get(index).getIncompleteTripCount(), Long.parseLong(csvRecord.get(4)));
                assertEquals(tripSummaries.get(index).getCancelledTripCount(), Long.parseLong(csvRecord.get(5)));
                assertEquals(tripSummaries.get(index).getTotalCharges(), BigDecimal.valueOf(Long.parseLong(csvRecord.get(6))));
                index++;
            }
        } catch (Exception exception) {
            assertNotNull(exception);
        } finally {
            // Clean up: Delete the temporary CSV file
            deleteTestingFile(pathToFile);

        }
    }

    private void deleteTestingFile(String pathToFile) {
        try {
            Files.deleteIfExists(Paths.get(pathToFile));

        } catch (Exception ignored) {
            //Ignored if deleting file failed
        }
    }
}
