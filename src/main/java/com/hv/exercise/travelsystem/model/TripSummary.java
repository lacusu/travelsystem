package com.hv.exercise.travelsystem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TripSummary {
    private Date tripDate;

    private String companyId;

    private String busId;

    private long completeTripCount;

    private long incompleteTripCount;

    private long cancelledTripCount;

    private BigDecimal totalCharges;
}