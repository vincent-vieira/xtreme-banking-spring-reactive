package io.vieira.xtremebanking.time;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import reactor.test.StepVerifier;

import java.time.Duration;

@RunWith(BlockJUnit4ClassRunner.class)
public class YearGeneratorTest {

    @Test
    public void years_should_be_generated_7_times() {
        StepVerifier.withVirtualTime(() -> YearGenerator.max(7).create())
                .thenAwait(Duration.ofSeconds(71))
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .expectNext(6)
                .expectNext(7)
                .expectComplete()
                .verify();
    }

    @Test
    public void a_single_year_should_be_also_working() {
        StepVerifier.withVirtualTime(() -> YearGenerator.max(1).create())
                .thenAwait(Duration.ofSeconds(10))
                .expectNext(1)
                .expectComplete()
                .verify();
    }
}