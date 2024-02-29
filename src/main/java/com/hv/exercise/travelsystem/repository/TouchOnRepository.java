package com.hv.exercise.travelsystem.repository;

import com.hv.exercise.travelsystem.entity.TouchOn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TouchOnRepository extends JpaRepository<TouchOn, Long> {
}
