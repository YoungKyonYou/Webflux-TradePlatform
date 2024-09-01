package com.youyk.customerservice.exceptions;

public class InsufficientShareException extends RuntimeException{
    private static final String MESSAGE = "Customer [id=%d] does not have enough shares to complete the transaction";

    public InsufficientShareException(Integer customerId){
        super(MESSAGE.formatted(customerId));
    }
}
