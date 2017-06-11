package io.vieira.xtremebanking.time;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static io.vieira.xtremebanking.time.YearGenerator.EmitMode.END;

@FunctionalInterface
public interface YearGenerator {

    enum EmitMode {
        BEGIN,
        END
    }

    Flux<Integer> create();

    static YearGenerator max(int limit) {
        return max(limit, END);
    }

    static YearGenerator max(int limit, EmitMode mode) {
        switch(mode) {
            case END:
                return () -> Flux.range(1, limit).delayElements(getDuration());
            case BEGIN:
                return () -> Flux.range(1, limit).concatMap(year -> Mono.just(year).delayElement(year > 1 ? getDuration() : Duration.ofNanos(0)));
            default:
                throw new IllegalStateException("wat");
        }
    }

    static Duration getDuration(){
        return Duration.ofSeconds(10);
    }
}
