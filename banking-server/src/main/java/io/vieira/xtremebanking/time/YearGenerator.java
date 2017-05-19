package io.vieira.xtremebanking.time;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@FunctionalInterface
public interface YearGenerator {

    Flux<Integer> create();

    static YearGenerator max(int limit) {
        // Chaining Mono and Flux is a tip to avoid waiting the initial delay before the first emission.
        // Also note that we can safely construct the Flux here as nothing will happen until the .subscribe() method will be called.
        return () -> Mono.just(1).concatWith(Flux.range(2, limit - 1).delayElements(Duration.ofSeconds(10)));
    }
}
