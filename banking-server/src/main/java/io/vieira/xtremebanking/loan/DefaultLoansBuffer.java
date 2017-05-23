package io.vieira.xtremebanking.loan;

import io.vieira.xtremebanking.models.LoanRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

import java.util.LinkedList;

@Component
public class DefaultLoansBuffer implements LoansBuffer {

    //Buffer is 256 by default
    private final ReplayProcessor<LoanRequest> loanRequestProcessor = ReplayProcessor.create();
    private final Flux<Integer> partitionFlux;
    private final ConfigurableApplicationContext applicationContext;
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLoansBuffer.class);

    public DefaultLoansBuffer(Flux<Integer> partitionFlux, ConfigurableApplicationContext applicationContext) {
        this.partitionFlux = partitionFlux;
        this.applicationContext = applicationContext;
    }

    public void newLoanRequested(LoanRequest request) {
        this.loanRequestProcessor.onNext(request);
    }

    public Flux<LinkedList<LoanRequest>> startBuffering() {
        return this.loanRequestProcessor.buffer(
                this.partitionFlux
                        .doOnComplete(applicationContext::close)
                        .doOnNext(yearValue -> LOGGER.info("Year {} just finished.", yearValue)),
                LinkedList::new
        ).switchIfEmpty(Mono.just(new LinkedList<>()));
    }
}
