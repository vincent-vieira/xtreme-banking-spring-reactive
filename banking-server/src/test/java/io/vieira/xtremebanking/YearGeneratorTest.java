package io.vieira.xtremebanking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import reactor.test.StepVerifier;

import java.time.Duration;

@RunWith(BlockJUnit4ClassRunner.class)
public class YearGeneratorTest {

    @Test
    public void years_should_be_generated_7_times() {
        // 61 seconds delay to skip 7 first items, and 10 seconds more in order to pass the last timeshift, skipped very rapidly thanks to relativity.
        StepVerifier.withVirtualTime(YearsLoop::getGenerator)
                .thenAwait(Duration.ofSeconds(61))
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .expectNext(6)
                .expectNext(7)
                .thenAwait(Duration.ofSeconds(10))
                .verifyComplete();
    }
}
