package io.vieira.xtremebanking.funds;

import io.vieira.xtremebanking.models.LoanRequest;

public class NotEnoughFundsException extends RuntimeException {

    public NotEnoughFundsException(LoanRequest faultyRequest) {
        super(String.format("Buyer '%s' made a request with amount %d, but doesn't have sufficient funds.", faultyRequest.getBuyer(), faultyRequest.getOffer()));
    }

    public NotEnoughFundsException(String buyerId, double requiredAmount, double funds) {
        super(String.format("Buyer '%s' made a request with amount %f, but doesn't have sufficient funds (currently having %f).", buyerId, requiredAmount, funds));
    }
}
