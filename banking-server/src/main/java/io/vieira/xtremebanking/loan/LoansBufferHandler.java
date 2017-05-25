package io.vieira.xtremebanking.loan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;

@Component
public class LoansBufferHandler implements SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoansBufferHandler.class);
    private final LoansBuffer loansBuffer;
    private boolean running = false;
    private Disposable loansSubscription;

    public LoansBufferHandler(LoansBuffer buffer) {
        this.loansBuffer = buffer;
    }

    @Override
    public void start() {
        this.running = true;
        LOGGER.info("Game on ! Starting year 1 and loan requests collection");
        // TODO : calculations and such ?
        this.loansSubscription = loansBuffer
                .startBuffering()
                .subscribe(loanRequests -> LOGGER.info("Loans : {}", loanRequests));
    }

    @Override
    public void stop() {
        this.running = false;
        LOGGER.info("Stopping game, and loan requests collection");
        if(!this.loansSubscription.isDisposed()) {
            this.loansSubscription.dispose();
        }
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        if(callback != null) {
            callback.run();
        }
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
