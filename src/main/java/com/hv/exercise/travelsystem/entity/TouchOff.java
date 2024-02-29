package com.hv.exercise.travelsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "touch_off")
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class TouchOff extends TouchDataBased{
}
