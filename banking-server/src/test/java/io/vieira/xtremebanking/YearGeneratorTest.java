package io.vieira.xtremebanking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import reactor.test.StepVerifier;

import static io.vieira.xtremebanking.YearsLoop.getGenerator;

@RunWith(BlockJUnit4ClassRunner.class)
public class YearGeneratorTest {

    @Test
    public void years_should_be_generated_7_times() {
        // 70 = 7 * 10 seconds delay, skipped very rapidly thanks to relative time.
        StepVerifier.create(getGenerator(), 70)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .expectNext(6)
                .expectNext(7)
                .expectComplete();
    }
}
