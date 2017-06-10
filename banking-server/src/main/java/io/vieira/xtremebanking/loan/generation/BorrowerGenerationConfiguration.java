package io.vieira.xtremebanking.loan.generation;

import io.vieira.xtremebanking.models.LoanBorrower;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;

@Configuration
public class BorrowerGenerationConfiguration {

    public Flux<List<LoanBorrower>> loanBorrowerFlux(Flux<Integer> yearGenerator,
                                                     @Value("${xtreme-banking.max-borrowers-per-year:10}") Integer maxBorrowers) {
        return new DefaultBorrowerGenerator(yearGenerator, maxBorrowers).generate();
    }
}
