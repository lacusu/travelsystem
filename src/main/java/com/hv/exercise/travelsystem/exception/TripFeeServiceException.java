package com.hv.exercise.travelsystem.exception;

import lombok.Getter;

@Getter
public class TripFeeServiceException extends BasedException {

    public TripFeeServiceException() {
        super("Failed to get fee for the provided trip");
    }

    public TripFeeServiceException(String var1) {
        super(var1);
    }

    public TripFeeServiceException(String var1, Throwable var2) {
        super(var1, var2);
    }

    public TripFeeServiceException(Throwable var1) {
        super(var1);
    }

}
