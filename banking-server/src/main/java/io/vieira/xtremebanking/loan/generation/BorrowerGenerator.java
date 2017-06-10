package io.vieira.xtremebanking.loan.generation;

import io.vieira.xtremebanking.models.LoanBorrower;
import reactor.core.publisher.Flux;

import java.util.List;

public interface BorrowerGenerator {
    Flux<List<LoanBorrower>> generate();
}
