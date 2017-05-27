package io.vieira.xtremebanking.loan;

import io.vieira.xtremebanking.models.LoanRequest;
import io.vieira.xtremebanking.time.YearGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import reactor.test.StepVerifier;

import static io.vieira.xtremebanking.time.YearGenerator.getDuration;

@RunWith(BlockJUnit4ClassRunner.class)
@SuppressWarnings("unchecked")
public class DefaultLoanRequestsBufferHandlerTest {

    private LoanRequestsBuffer loanRequestsBuffer;
    private final LoanRequest sampleLoanRequest = new LoanRequest("someone", 150);

    @Test
    public void should_buffer_properly_throughout_a_year() {
        // We cannot inline the LoanRequestsBuffer instanciation inside a @Before block as the virtual scheduler will be put
        // only on the LoanRequestsBuffer Observable, and not on the YearGenerator.
        StepVerifier
                .withVirtualTime(() -> {
                    this.loanRequestsBuffer = new DefaultLoanRequestsBuffer(YearGenerator.max(1).create());
                    return this.loanRequestsBuffer.startBuffering();
                })
                .then(() -> this.loanRequestsBuffer.newLoanRequested(sampleLoanRequest))
                .thenAwait(getDuration())
                .expectNextMatches(loanRequestBucket -> loanRequestBucket.getRequests().size() == 1)
                .expectComplete()
                .verify();
    }

    @Test
    public void should_buffer_properly_throughout_multiple_years() {
        StepVerifier
                .withVirtualTime(() -> {
                    this.loanRequestsBuffer = new DefaultLoanRequestsBuffer(YearGenerator.max(2).create());
                    return this.loanRequestsBuffer.startBuffering();
                })
                .then(() -> this.loanRequestsBuffer.newLoanRequested(sampleLoanRequest))
                .thenAwait(getDuration())
                .then(() -> this.loanRequestsBuffer.newLoanRequested(sampleLoanRequest))
                .thenAwait(getDuration())
                .expectNextMatches(loanRequestBucket -> loanRequestBucket.getRequests().size() == 1 && loanRequestBucket.getYear() == 1)
                .expectNextMatches(loanRequestBucket -> loanRequestBucket.getRequests().size() == 1 && loanRequestBucket.getYear() == 2)
                .expectComplete()
                .verify();
    }

    @Test
    public void should_not_buffer_anything_unless_invoked() {
        StepVerifier
                .withVirtualTime(() -> {
                    this.loanRequestsBuffer = new DefaultLoanRequestsBuffer(YearGenerator.max(1).create());
                    return this.loanRequestsBuffer.startBuffering();
                })
                .thenAwait(getDuration())
                .expectNextMatches(loanRequestBucket -> loanRequestBucket.getRequests().isEmpty())
                .expectComplete()
                .verify();
    }
}
