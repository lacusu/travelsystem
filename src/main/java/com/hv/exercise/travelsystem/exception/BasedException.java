package com.hv.exercise.travelsystem.exception;

import lombok.Getter;

@Getter
public class BasedException extends RuntimeException {

    public BasedException(String var1) {
        super(var1);
    }

    public BasedException(String var1, Throwable var2) {
        super(var1, var2);
    }

    public BasedException(Throwable var1) {
        super(var1);
    }

}
