package com.hv.exercise.travelsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "touch_on")
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class TouchOn extends TouchDataBased{
}
