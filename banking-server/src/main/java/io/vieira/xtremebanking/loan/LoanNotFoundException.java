package io.vieira.xtremebanking.loan;

import io.vieira.xtremebanking.models.LoanRequest;

public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(LoanRequest request) {
        super(String.format("Unable to find loan '%s'", request.getLoan()));
    }
}
