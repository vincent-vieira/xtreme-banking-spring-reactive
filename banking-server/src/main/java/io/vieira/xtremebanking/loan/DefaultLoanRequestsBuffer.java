package io.vieira.xtremebanking.loan;

import io.vieira.xtremebanking.models.LoanRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

import java.util.LinkedList;
import java.util.stream.Collectors;

@Component
public class DefaultLoanRequestsBuffer implements LoanRequestsBuffer {

    //Buffer is 256 by default
    private final ReplayProcessor<LoanRequest> loanRequestProcessor = ReplayProcessor.create();
    private final Flux<Integer> partitionFlux;
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLoanRequestsBuffer.class);

    public DefaultLoanRequestsBuffer(Flux<Integer> partitionFlux) {
        this.partitionFlux = partitionFlux;
    }

    public void newLoanRequested(LoanRequest request) {
        this.loanRequestProcessor.onNext(request);
    }

    public Flux<LoanRequestBucket> startBuffering() {
        return this.loanRequestProcessor
                .window(this.partitionFlux)
                // As stated in the documentation, the window operator emit one last empty flux before completing. We need to skip it as it will produce invalid values.
                .skipLast(1)
                .flatMap(loanRequestsFlux -> loanRequestsFlux.collect(Collectors.toCollection(LinkedList::new)))
                .withLatestFrom(this.partitionFlux, (loanRequests, year) -> new LoanRequestBucket(year, loanRequests))
                .doOnNext(bucket -> LOGGER.info(
                        "Year {} just finished. {} request(s) collected.",
                        bucket.getYear(),
                        bucket.getRequests().isEmpty() ? "No" : bucket.getRequests().size()
                ));
    }
}
