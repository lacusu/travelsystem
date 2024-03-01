package com.hv.exercise.travelsystem.entity;

import com.hv.exercise.travelsystem.constant.TripStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TripData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private LocalDateTime startDatetime;

    private LocalDateTime endDatetime;

    private String fromStop;

    private String toStop;

    private String companyId;

    private String busId;

    private String hashedPan;

    private BigDecimal chargeAmount;

    @Enumerated(value = EnumType.STRING)
    private TripStatus status;

    private String reason;
}
