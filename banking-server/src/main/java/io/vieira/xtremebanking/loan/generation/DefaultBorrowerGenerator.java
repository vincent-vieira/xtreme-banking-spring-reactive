package io.vieira.xtremebanking.loan.generation;

import io.vieira.xtremebanking.models.LoanBorrower;
import org.apache.commons.lang3.RandomUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultBorrowerGenerator implements BorrowerGenerator {

    private final Flux<Integer> yearGenerator;
    private final Integer maxBorrowersPerYear;

    public DefaultBorrowerGenerator(Flux<Integer> yearGenerator, Integer maxBorrowersPerYear) {
        this.yearGenerator = yearGenerator;
        this.maxBorrowersPerYear = maxBorrowersPerYear;
    }

    @Override
    public Flux<List<LoanBorrower>> generate() {
        return Mono.just(1)
                .concatWith(this.yearGenerator)
                // As we're appending an initial generation, we need to squeeze the last one associated with the last year end.
                .skipLast(1)
                .map(year -> IntStream
                        .range(0, this.maxBorrowersPerYear)
                        .mapToObj(value -> new LoanBorrower(
                                UUID.randomUUID().toString(),
                                RandomUtils.nextInt(200000, 500000),
                                RandomUtils.nextInt(1000, 10000)
                        ))
                        .collect(Collectors.toList())
                );
    }
}
