package io.vieira.xtremebanking.loan.generation;

import io.vieira.xtremebanking.models.LoanBorrowerBucket;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface BorrowerGenerator {
    Flux<LoanBorrowerBucket> getGenerator();
}
