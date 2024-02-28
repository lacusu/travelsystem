package com.hv.exercise.travelsystem.service.impl;

import com.hv.exercise.travelsystem.constant.TouchType;
import com.hv.exercise.travelsystem.model.TouchDataModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ContextConfiguration(classes = {CsvFileServiceImpl.class})
@ExtendWith(SpringExtension.class)
class CsvFileServiceImplTest {
    @Autowired
    private CsvFileServiceImpl csvFileService;

    @Test
    void readTechFile_whenGoodFile_thenReturnItems() throws IOException {
        //GIVEN
        String pathToFile = "src/test/resources/file-service/touchData.csv";

        //WHEN
        List<TouchDataModel> touchDataModels = csvFileService.readTouchFile(pathToFile);

        //THEN
        assertNotNull(touchDataModels);
        assertEquals(2, touchDataModels.size());

        //Verifying for 1st record
        int firstRecordIndex = 0;
        assertEquals(1L, touchDataModels.get(firstRecordIndex).getId());
        assertEquals(LocalDateTime.of(2024, 2, 28, 10, 10, 10),
                touchDataModels.get(firstRecordIndex).getDatetime());
        assertEquals(TouchType.ON, touchDataModels.get(firstRecordIndex).getTouchType());
        assertEquals("Company1", touchDataModels.get(firstRecordIndex).getCompanyId());
        assertEquals("Bus10", touchDataModels.get(firstRecordIndex).getBusId());
        assertEquals("StopA", touchDataModels.get(firstRecordIndex).getStopId());
        assertEquals("1234556789", touchDataModels.get(firstRecordIndex).getPan());

        //Verifying for 2nd record
        int secondRecordIndex = 1;
        assertEquals(1L, touchDataModels.get(secondRecordIndex).getId());
        assertEquals(LocalDateTime.of(2024, 2, 28, 10, 20, 10),
                touchDataModels.get(secondRecordIndex).getDatetime());
        assertEquals(TouchType.OFF, touchDataModels.get(secondRecordIndex).getTouchType());
        assertEquals("Company1", touchDataModels.get(secondRecordIndex).getCompanyId());
        assertEquals("Bus10", touchDataModels.get(secondRecordIndex).getBusId());
        assertEquals("StopA", touchDataModels.get(secondRecordIndex).getStopId());
        assertEquals("1234556789", touchDataModels.get(secondRecordIndex).getPan());

    }

    @Test
    void readTechFile_whenNoRecord_thenReturnEmptyList() throws IOException {
        //GIVEN
        String pathToFile = "src/test/resources/file-service/touchDataNoRecord.csv";
        //WHEN
        List<TouchDataModel> touchDataModels = csvFileService.readTouchFile(pathToFile);

        //THEN
        assertNotNull(touchDataModels);
        assertEquals(0, touchDataModels.size());

    }

    @Test
    void readTechFile_whenEmptyFile_thenReturnEmptyList() throws IOException {
        //GIVEN
        String pathToFile = "src/test/resources/file-service/touchDataEmpty.csv";

        //WHEN
        List<TouchDataModel> touchDataModels = csvFileService.readTouchFile(pathToFile);

        //THEN
        assertNotNull(touchDataModels);
        assertEquals(0, touchDataModels.size());

    }

    @Test
    void readTechFile_whenFileNotFound_thenThrowException() {
        //GIVEN
        String pathToFile = "PathToFile";

        //WHEN & THEN
        assertThrows(Exception.class, () -> csvFileService.readTouchFile(pathToFile));

    }
}
