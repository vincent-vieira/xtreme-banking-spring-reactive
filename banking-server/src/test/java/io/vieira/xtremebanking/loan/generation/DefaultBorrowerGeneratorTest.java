package io.vieira.xtremebanking.loan.generation;

import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import io.vieira.xtremebanking.models.LoanBorrower;
import io.vieira.xtremebanking.time.YearGenerator;
import org.junit.runner.RunWith;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Predicate;

@RunWith(ZohhakRunner.class)
public class DefaultBorrowerGeneratorTest {

    @TestWith({
            "10",
            "20",
            "50"
    })
    public void each_year_borrowers_should_be_generated(int maxBorrowers) throws Exception {
        final Predicate<List<LoanBorrower>> borrowersChecker = loanBorrowers -> loanBorrowers.size() == maxBorrowers;
        StepVerifier
                .withVirtualTime(() -> new DefaultBorrowerGenerator(YearGenerator.max(5).create(), maxBorrowers).generate())
                .thenAwait(YearGenerator.getDuration())
                .expectNextMatches(borrowersChecker)
                .thenAwait(YearGenerator.getDuration())
                .expectNextMatches(borrowersChecker)
                .thenAwait(YearGenerator.getDuration())
                .expectNextMatches(borrowersChecker)
                .thenAwait(YearGenerator.getDuration())
                .expectNextMatches(borrowersChecker)
                .thenAwait(YearGenerator.getDuration())
                .expectNextMatches(borrowersChecker)
                .verifyComplete();
    }
}
