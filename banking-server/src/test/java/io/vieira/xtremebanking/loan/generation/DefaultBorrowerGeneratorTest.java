package io.vieira.xtremebanking.loan.generation;

import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import io.vieira.xtremebanking.models.LoanBorrowerBucket;
import io.vieira.xtremebanking.time.YearGenerator;
import org.junit.runner.RunWith;
import reactor.test.StepVerifier;

import java.util.function.Function;
import java.util.function.Predicate;

@RunWith(ZohhakRunner.class)
public class DefaultBorrowerGeneratorTest {

    @TestWith({
            "10",
            "20",
            "50"
    })
    public void each_year_borrowers_should_be_generated(int maxBorrowers) throws Exception {
        Function<Integer, Predicate<LoanBorrowerBucket>> borrowersChecker = year -> {
            return loanBorrowerBucket -> loanBorrowerBucket.getYear() == year && loanBorrowerBucket.getBorrowers().size() == maxBorrowers;
        };

        StepVerifier
                .withVirtualTime(() -> new DefaultBorrowerGenerator(YearGenerator.max(5).create(), maxBorrowers).getGenerator())
                .thenAwait(YearGenerator.getDuration())
                .expectNextMatches(borrowersChecker.apply(1))
                .thenAwait(YearGenerator.getDuration())
                .expectNextMatches(borrowersChecker.apply(2))
                .thenAwait(YearGenerator.getDuration())
                .expectNextMatches(borrowersChecker.apply(3))
                .thenAwait(YearGenerator.getDuration())
                .expectNextMatches(borrowersChecker.apply(4))
                .thenAwait(YearGenerator.getDuration())
                .expectNextMatches(borrowersChecker.apply(5))
                .verifyComplete();
    }
}
