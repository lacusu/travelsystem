package com.hv.exercise.travelsystem.repository;

import com.hv.exercise.travelsystem.entity.TripFee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class TripFeeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TripFeeRepository tripFeeRepository;

    @Test
    void testFindTripFee_whenFromAToC_thenReturnRightValue() {
        //WHEN
        Optional<TripFee> tripFeeOptional = tripFeeRepository.findTripFee("StopA", "StopC");

        //THEN
        assertTrue(tripFeeOptional.isPresent());
        assertEquals(BigDecimal.valueOf(8.45), tripFeeOptional.get().getFee());
    }

    @Test
    void testFindTripFee_whenOnlyFromProvided_thenReturnMaxFeePossible() {
        //WHEN
        Optional<TripFee> tripFeeOptional = tripFeeRepository.findMaxTripFeeBySingleStop("StopA");

        //THEN
        assertTrue(tripFeeOptional.isPresent());
        assertEquals(BigDecimal.valueOf(8.45), tripFeeOptional.get().getFee());
    }
}
