package io.vieira.xtremebanking.time;

import reactor.core.publisher.Flux;

import java.time.Duration;

@FunctionalInterface
public interface YearGenerator {

    Flux<Integer> create();

    static YearGenerator max(int limit) {
        return () -> Flux.range(1, limit).delayElements(getDuration());
    }

    static Duration getDuration(){
        return Duration.ofSeconds(10);
    }
}
