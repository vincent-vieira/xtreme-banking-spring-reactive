package io.vieira.xtremebanking.funds;

import io.vieira.xtremebanking.models.LoanRequest;

public class NotEnoughFundsException extends Throwable {

    private final LoanRequest faultyRequest;

    public NotEnoughFundsException(LoanRequest faultyRequest) {
        super(String.format("Buyer '%s' made a request with amount %d, but doesn't have sufficient funds.", faultyRequest.getBuyer(), faultyRequest.getOffer()));
        this.faultyRequest = faultyRequest;
    }

    public LoanRequest getFaultyRequest() {
        return faultyRequest;
    }
}
