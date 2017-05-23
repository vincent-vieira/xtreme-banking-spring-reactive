package io.vieira.xtremebanking.loan;

import io.vieira.xtremebanking.models.LoanRequest;
import reactor.core.publisher.Flux;

import java.util.List;

public interface LoansBuffer {
    void newLoanRequested(LoanRequest request);
    Flux<? extends List<LoanRequest>> startBuffering();
}
