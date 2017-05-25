package io.vieira.xtremebanking.loan;

import io.vieira.xtremebanking.models.LoanRequest;
import io.vieira.xtremebanking.time.YearGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mockito;
import org.springframework.context.ConfigurableApplicationContext;
import reactor.test.StepVerifier;

import java.time.Duration;

@RunWith(BlockJUnit4ClassRunner.class)
@SuppressWarnings("unchecked")
public class DefaultLoansBufferHandlerTest {

    private LoansBuffer loansBuffer;
    private final ConfigurableApplicationContext applicationContext = Mockito.mock(ConfigurableApplicationContext.class);

    @Test
    public void should_buffer_properly_throughout_a_year() {
        // We cannot inline the LoansBuffer instanciation inside a @Before block as the virtual scheduler will be put
        // only on the LoansBuffer Observable, and not on the YearGenerator.
        StepVerifier
                .withVirtualTime(() -> {
                    this.loansBuffer = new DefaultLoansBuffer(YearGenerator.max(1).create(), applicationContext);
                    return this.loansBuffer.startBuffering();
                })
                .then(() -> this.loansBuffer.newLoanRequested(new LoanRequest()))
                .thenAwait(Duration.ofSeconds(10))
                .expectNextMatches(loanRequestBucket -> loanRequestBucket.getRequests().size() == 1)
                .expectComplete()
                .verify();
    }

    @Test
    public void should_buffer_properly_throughout_multiple_years() {
        StepVerifier
                .withVirtualTime(() -> {
                    this.loansBuffer = new DefaultLoansBuffer(YearGenerator.max(2).create(), applicationContext);
                    return this.loansBuffer.startBuffering();
                })
                .then(() -> this.loansBuffer.newLoanRequested(new LoanRequest()))
                .thenAwait(Duration.ofSeconds(10))
                .then(() -> this.loansBuffer.newLoanRequested(new LoanRequest()))
                .thenAwait(Duration.ofSeconds(10))
                .expectNextMatches(loanRequestBucket -> loanRequestBucket.getRequests().size() == 1 && loanRequestBucket.getYear() == 1)
                .expectNextMatches(loanRequestBucket -> loanRequestBucket.getRequests().size() == 1 && loanRequestBucket.getYear() == 2)
                .expectComplete()
                .verify();
    }

    @Test
    public void should_not_buffer_anything_unless_invoked() {
        StepVerifier
                .withVirtualTime(() -> {
                    this.loansBuffer = new DefaultLoansBuffer(YearGenerator.max(1).create(), applicationContext);
                    return this.loansBuffer.startBuffering();
                })
                .thenAwait(Duration.ofSeconds(10))
                .expectNextMatches(loanRequestBucket -> loanRequestBucket.getRequests().isEmpty())
                .expectComplete()
                .verify();
    }
}
