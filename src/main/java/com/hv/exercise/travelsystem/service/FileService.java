package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.entity.TripData;
import com.hv.exercise.travelsystem.model.TouchDataModel;
import com.hv.exercise.travelsystem.model.TripSummary;

import java.io.IOException;
import java.util.List;

public interface FileService {
    List<TouchDataModel> readTouchFile(String filePath) throws IOException;

    void writeToTripFile(List<TripData> tripDataList);

//    void writeToUnprocceedFile(List<TripData> tripDataList);

    void writeToUnprocessableTripFile(List<TripData> tripDataList);

    void writeToSummaryFile(List<TripSummary> tripSummaries);
}
