package com.hv.exercise.travelsystem.model;

import com.hv.exercise.travelsystem.constant.TouchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TouchDataModel {
    private long id;
    private LocalDateTime datetime;
    private TouchType touchType;
    private String stopId;
    private String companyId;
    private String busId;
    private String pan;
}
