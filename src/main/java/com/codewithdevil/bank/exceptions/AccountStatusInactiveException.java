package com.codewithdevil.bank.exceptions;

public class AccountStatusInactiveException extends RuntimeException{
    public AccountStatusInactiveException(String message){
        super(message);
    }
}
