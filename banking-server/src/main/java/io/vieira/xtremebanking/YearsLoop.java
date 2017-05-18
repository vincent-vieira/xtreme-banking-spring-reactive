package io.vieira.xtremebanking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class YearsLoop implements SmartLifecycle {

    // Chaining Mono and Flux is a tip to avoid waiting the initial delay before the first emission.
    // Also note that we can safely construct the Flux here as nothing will happen until the .subscribe() method will be called.
    static Flux<Integer> getGenerator() {
        return Mono.just(1).concatWith(Flux.range(2, 6).delayElements(Duration.ofSeconds(10)));
    }

    private boolean started = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(YearsLoop.class);
    private final Flux<Integer> yearFlux;
    private Disposable yearSubscription;

    public YearsLoop(ConfigurableApplicationContext applicationContext) {
        this.yearFlux = getGenerator().doOnComplete(applicationContext::close);
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        if(callback != null) callback.run();
    }

    @Override
    public void start() {
        LOGGER.info("Game on ! Starting year generation");
        this.started = true;
        this.yearSubscription = this.yearFlux.subscribe(dayValue -> LOGGER.info("Currently on year {}", dayValue));
    }

    @Override
    public void stop() {
        LOGGER.info("Game is now over ! Stopping year generation");
        this.started = false;
        if(!yearSubscription.isDisposed()) {
            yearSubscription.dispose();
        }
    }

    @Override
    public boolean isRunning() {
        return this.started;
    }

    /**
     * Returning Integer.MAX_VALUE only suggests that we will be the first bean to shutdown.
     */
    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
