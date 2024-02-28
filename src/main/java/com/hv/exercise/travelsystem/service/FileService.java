package com.hv.exercise.travelsystem.service;

import com.hv.exercise.travelsystem.model.TouchDataModel;

import java.io.IOException;
import java.util.List;

public interface FileService {
    List<TouchDataModel> readTouchFile(String filePath) throws IOException;
}
