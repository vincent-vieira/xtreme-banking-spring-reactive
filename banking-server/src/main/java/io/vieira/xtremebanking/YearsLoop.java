package io.vieira.xtremebanking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@Component
public class YearsLoop implements SmartLifecycle {

    private boolean started = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(YearsLoop.class);
    private final Flux<Integer> yearFlux;
    private Disposable yearSubscription;

    public YearsLoop(ConfigurableApplicationContext applicationContext, Flux<Integer> generator) {
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
