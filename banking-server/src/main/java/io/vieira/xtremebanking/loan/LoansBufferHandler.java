package io.vieira.xtremebanking.loan;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.models.LoanRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;

import java.util.Optional;

@Component
public class LoansBufferHandler implements SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoansBufferHandler.class);
    private final LoansBuffer loansBuffer;
    private final FundsManager fundsManager;
    private boolean running = false;
    private Disposable loansSubscription;
    private ConfigurableApplicationContext applicationContext;

    public LoansBufferHandler(LoansBuffer buffer, FundsManager fundsManager, ConfigurableApplicationContext applicationContext) {
        this.loansBuffer = buffer;
        this.fundsManager = fundsManager;
        this.applicationContext = applicationContext;
    }

    @Override
    public void start() {
        this.running = true;
        LOGGER.info("Game on ! Starting year 1 and loan requests collection");
        this.loansSubscription = loansBuffer
                .startBuffering()
                // Always make sure a new client has its initial funds
                .doOnNext(loanRequestsBucket ->
                        loanRequestsBucket
                                .getRequests()
                                .stream()
                                .map(LoanRequest::getBuyer)
                                .forEach(fundsManager::tryNewBuyer)
                )
                .doOnComplete(applicationContext::close)
                .subscribe(loanRequestsBucket -> {
                    // TODO : calculate if needed and update
                    Optional<LoanRequest> winnerRequest = loanRequestsBucket.getRequests().stream().sorted().findFirst();
                    if(winnerRequest.isPresent()) {
                        LoanRequest winner = winnerRequest.get();
                        LOGGER.info("Winner for year {} is {} with a higher bid of {}", loanRequestsBucket.getYear(), winner.getBuyer(), winner.getOffer());
                        this.fundsManager.spend(winner.getBuyer(), winner.getOffer());
                    }
                });
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
