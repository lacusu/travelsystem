package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.constant.TouchType;
import com.hv.exercise.travelsystem.entity.TouchOff;
import com.hv.exercise.travelsystem.entity.TouchOn;
import com.hv.exercise.travelsystem.model.TouchDataModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PreprocessDataService {
    private FileService fileService;

    private TouchService touchService;

    public void importTouchData() throws IOException {
        String pathToFile = "src/main/resources/static/input/touchData.csv";
        List<TouchDataModel> touchDataModels = fileService.readTouchFile(pathToFile);
        List<TouchOn> touchOns = new ArrayList<>();
        List<TouchOff> touchOffs = new ArrayList<>();
        touchDataModels.forEach(touchDataModel -> {
            if (TouchType.ON == touchDataModel.getTouchType()) {
                touchOns.add(buildTouchOn(touchDataModel));
            } else {
                touchOffs.add(buildTouchOff(touchDataModel));
            }
        });
        touchService.importTouchOn(touchOns);
        touchService.importTouchOff(touchOffs);
    }

    private TouchOn buildTouchOn(TouchDataModel touchDataModel) {
        TouchOn touchOn = new TouchOn();
        touchOn.setStopId(touchDataModel.getStopId());
        touchOn.setDatetime(touchDataModel.getDatetime());
        touchOn.setCompanyId(touchDataModel.getCompanyId());
        touchOn.setBusId(touchDataModel.getBusId());
        touchOn.setPan(touchDataModel.getPan());
        return touchOn;
    }

    private TouchOff buildTouchOff(TouchDataModel touchDataModel) {
        TouchOff touchOff = new TouchOff();
        touchOff.setStopId(touchDataModel.getStopId());
        touchOff.setDatetime(touchDataModel.getDatetime());
        touchOff.setCompanyId(touchDataModel.getCompanyId());
        touchOff.setBusId(touchDataModel.getBusId());
        touchOff.setPan(touchDataModel.getPan());
        return touchOff;
    }
}
