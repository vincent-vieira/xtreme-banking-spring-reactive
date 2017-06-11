package io.vieira.xtremebanking.loan.generation;

import io.vieira.xtremebanking.models.LoanBorrower;
import io.vieira.xtremebanking.models.LoanBorrowerBucket;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class DefaultBorrowerGenerator implements BorrowerGenerator {

    private final Flux<Integer> yearGenerator;
    private final Integer maxBorrowersPerYear;
    private final Flux<LoanBorrowerBucket> generator;

    public DefaultBorrowerGenerator(Flux<Integer> yearGenerator, @Value("${xtreme-banking.max-borrowers-per-year:10}") Integer maxBorrowersPerYear) {
        this.yearGenerator = yearGenerator;
        this.maxBorrowersPerYear = maxBorrowersPerYear;
        this.generator = Mono.just(1)
                // As we're appending an initial generation, we need to squeeze the last one associated with the last year end.
                // And we also need to start years from two, not one !
                .mergeWith(this.yearGenerator.map(year -> year + 1).skipLast(1))
                .map(year -> new LoanBorrowerBucket(
                        year,
                        IntStream
                                .range(0, this.maxBorrowersPerYear)
                                .mapToObj(value -> new LoanBorrower(
                                        UUID.randomUUID().toString(),
                                        RandomUtils.nextInt(200000, 500000),
                                        RandomUtils.nextInt(1000, 10000)
                                ))
                                .collect(Collectors.toList())
                    )
                );
    }

    @Override
    public Flux<LoanBorrowerBucket> getGenerator() {
        return this.generator;
    }
}
