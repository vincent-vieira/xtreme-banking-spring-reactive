package io.vieira.xtremebanking.loan;

import io.vieira.xtremebanking.models.LoanRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

import java.util.List;

@Component
public class LoansBuffer {

    //Buffer is 256 by default
    private final ReplayProcessor<LoanRequest> loanRequestProcessor = ReplayProcessor.create();
    private final Flux<Integer> partitionFlux;

    public LoansBuffer(Flux<Integer> partitionFlux) {
        this.partitionFlux = partitionFlux;
    }

    public void newLoanRequested(LoanRequest request) {
        this.loanRequestProcessor.onNext(request);
    }

    public Flux<List<LoanRequest>> startBuffering() {
        return this.loanRequestProcessor.buffer(this.partitionFlux);
    }
}
