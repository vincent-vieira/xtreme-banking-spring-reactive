package io.vieira.xtremebanking.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@Component
public class YearLooper implements SmartLifecycle {

    public static final int PHASE = Integer.MAX_VALUE;
    private final int maxYears;

    private boolean started = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(YearLooper.class);
    private final Flux<Integer> yearFlux;
    private Disposable yearSubscription;

    public YearLooper(ConfigurableApplicationContext applicationContext, Flux<Integer> generator, @Value("${xtreme-banking.max-years:7}") int maxYears) {
        this.maxYears = maxYears;
        this.yearFlux = generator.doOnComplete(applicationContext::close);
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
        LOGGER.info("Game on ! Starting year 1...");
        this.started = true;
        this.yearSubscription = this.yearFlux.subscribe(yearValue -> {
            if(yearValue < maxYears) LOGGER.info("Year {} just finished. Starting year {}...", yearValue, yearValue + 1);
            else LOGGER.info("Year {} just finished. Quitting game...", yearValue);
        });
    }

    @Override
    public void stop() {
        LOGGER.info("Game is now done ! Stopping year generation");
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
        return PHASE;
    }
}
