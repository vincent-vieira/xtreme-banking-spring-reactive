package io.vieira.xtremebanking.loan;

import io.vieira.xtremebanking.models.LoanRequest;
import io.vieira.xtremebanking.time.YearGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

// FIXME : tests
@RunWith(BlockJUnit4ClassRunner.class)
@SuppressWarnings("unchecked")
public class LoansBufferHandlerTest {

    private LoansBuffer loansBuffer;
    private final List<LoanRequest> sampleEmptyList = Collections.emptyList();
    private final List<LoanRequest> sampleList = Collections.singletonList(new LoanRequest());

    @Before
    public void initYearGenerator() {
        this.loansBuffer = new LoansBuffer(YearGenerator.max(1).create());
    }

    @Test
    public void should_buffer_properly_throughout_a_year() {
        StepVerifier
                .withVirtualTime(() -> this.loansBuffer.startBuffering())
                .then(() -> this.loansBuffer.newLoanRequested(new LoanRequest()))
                .thenAwait(Duration.ofSeconds(10))
                .expectNext(sampleList)
                .expectComplete()
                .verify();
    }

    @Test
    public void should_buffer_properly_throughout_multiple_years() {
        StepVerifier
                .withVirtualTime(() -> this.loansBuffer.startBuffering())
                .then(() -> this.loansBuffer.newLoanRequested(new LoanRequest()))
                .thenAwait(Duration.ofSeconds(10))
                .then(() -> this.loansBuffer.newLoanRequested(new LoanRequest()))
                .thenAwait(Duration.ofSeconds(10))
                .expectNext(sampleList)
                .expectNext(sampleList)
                .expectComplete()
                .verify();
    }

    @Test
    public void should_not_buffer_anything_unless_invoked() {
        StepVerifier.withVirtualTime(() -> this.loansBuffer.startBuffering())
                .thenAwait(Duration.ofSeconds(10))
                .expectNext(sampleEmptyList)
                .expectComplete()
                .verify();
    }
}
