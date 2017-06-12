package io.vieira.xtremebanking.loan.generation;

import io.vieira.xtremebanking.models.LoanBorrower;
import io.vieira.xtremebanking.models.LoanBorrowerBucket;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class DefaultBorrowerGenerator implements BorrowerGenerator {

    private final Flux<Integer> yearGenerator;
    private final Integer maxBorrowersPerYear;
    private final Flux<LoanBorrowerBucket> generator;
    private final static Logger LOGGER = LoggerFactory.getLogger(BorrowerGenerator.class);

    public DefaultBorrowerGenerator(@Qualifier("yearBeginGenerator") Flux<Integer> yearGenerator,
                                    @Value("${xtreme-banking.borrowers.max-per-year:10}") Integer maxBorrowersPerYear,
                                    @Value("${xtreme-banking.borrowers.loan.start-amount:15000}") Integer loanStartAmount,
                                    @Value("${xtreme-banking.borrowers.loan.end-amount:50000}") Integer loanLimitAmount,
                                    @Value("${xtreme-banking.borrowers.loan.immediate-cashback.start-amount:1000}") Integer cashbackStartAmount,
                                    @Value("${xtreme-banking.borrowers.loan.immediate-cashback.start-amount:10000}") Integer cashbackLimitAmount) {
        this.yearGenerator = yearGenerator;
        this.maxBorrowersPerYear = maxBorrowersPerYear;
        this.generator = this.yearGenerator
                .map(year -> new LoanBorrowerBucket(
                        year,
                        IntStream
                                .range(0, this.maxBorrowersPerYear)
                                .mapToObj(value -> new LoanBorrower(
                                        UUID.randomUUID().toString(),
                                        RandomUtils.nextInt(loanStartAmount, loanLimitAmount),
                                        RandomUtils.nextInt(cashbackStartAmount, cashbackLimitAmount)
                                ))
                                .collect(Collectors.toList())
                    )
                )
                .doOnNext(borrowerBucket -> LOGGER.info(
                        "Generated {} fake borrowers for year {} : {}",
                        this.maxBorrowersPerYear,
                        borrowerBucket.getYear(),
                        borrowerBucket.getBorrowers()
                ))
                .share();
    }

    @Override
    public Flux<LoanBorrowerBucket> getGenerator() {
        return this.generator;
    }
}
