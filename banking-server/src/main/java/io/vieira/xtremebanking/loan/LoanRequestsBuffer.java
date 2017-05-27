package io.vieira.xtremebanking.loan;

import io.vieira.xtremebanking.models.LoanRequest;
import reactor.core.publisher.Flux;

public interface LoanRequestsBuffer {
    void newLoanRequested(LoanRequest request);
    Flux<LoanRequestBucket> startBuffering();
}
