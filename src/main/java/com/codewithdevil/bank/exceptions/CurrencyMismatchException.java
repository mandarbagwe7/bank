package com.codewithdevil.bank.exceptions;

public class CurrencyMismatchException extends RuntimeException{
    public CurrencyMismatchException(String message){
        super(message);
    }
}
