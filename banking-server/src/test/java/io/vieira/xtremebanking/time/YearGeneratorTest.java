package io.vieira.xtremebanking.time;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import reactor.test.StepVerifier;

import static io.vieira.xtremebanking.time.YearGenerator.getDuration;

@RunWith(BlockJUnit4ClassRunner.class)
public class YearGeneratorTest {

    @Test
    public void years_begin_should_be_generated_7_times() {
        StepVerifier.withVirtualTime(() -> YearGenerator.max(7, YearGenerator.EmitMode.BEGIN).create())
                .thenAwait(getDuration().dividedBy(2))
                .expectNext(1)
                .thenAwait(getDuration())
                .expectNext(2)
                .thenAwait(getDuration())
                .expectNext(3)
                .thenAwait(getDuration())
                .expectNext(4)
                .thenAwait(getDuration())
                .expectNext(5)
                .thenAwait(getDuration())
                .expectNext(6)
                .thenAwait(getDuration())
                .expectNext(7)
                .verifyComplete();
    }


    @Test
    public void a_single_year_start_should_be_also_working() {
        StepVerifier.withVirtualTime(() -> YearGenerator.max(1, YearGenerator.EmitMode.BEGIN).create())
                .thenAwait(getDuration().dividedBy(2))
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    public void years_end_should_be_generated_7_times() {
        StepVerifier.withVirtualTime(() -> YearGenerator.max(7).create())
                .thenAwait(getDuration())
                .expectNext(1)
                .thenAwait(getDuration())
                .expectNext(2)
                .thenAwait(getDuration())
                .expectNext(3)
                .thenAwait(getDuration())
                .expectNext(4)
                .thenAwait(getDuration())
                .expectNext(5)
                .thenAwait(getDuration())
                .expectNext(6)
                .thenAwait(getDuration())
                .expectNext(7)
                .verifyComplete();
    }

    @Test
    public void a_single_year_end_should_be_also_working() {
        StepVerifier.withVirtualTime(() -> YearGenerator.max(1).create())
                .thenAwait(getDuration())
                .expectNext(1)
                .verifyComplete();
    }
}
