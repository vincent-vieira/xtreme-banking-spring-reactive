package io.vieira.xtremebanking.loan;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.loan.payment.LoanPayer;
import io.vieira.xtremebanking.models.LoanRequest;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class LoansBufferHandler implements SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoansBufferHandler.class);
    private final LoanRequestsBuffer loanRequestsBuffer;
    private boolean running = false;
    private Disposable loansSubscription;
    private ConfigurableApplicationContext applicationContext;
    private LoanPayer loanPayer;
    private FundsManager fundsManager;

    public LoansBufferHandler(LoanRequestsBuffer buffer, ConfigurableApplicationContext applicationContext, LoanPayer loanPayer, FundsManager fundsManager) {
        this.loanRequestsBuffer = buffer;
        this.applicationContext = applicationContext;
        this.loanPayer = loanPayer;
        this.fundsManager = fundsManager;
    }

    @Override
    public void start() {
        this.running = true;
        LOGGER.info("Game on ! Starting year 1 and loan requests collection");
        this.loansSubscription = loanRequestsBuffer
                .startBuffering()
                .doOnComplete(applicationContext::close)
                .flatMap(loanRequestsBucket ->  {
                    Optional<LoanRequest> winnerRequest = loanRequestsBucket.getRequests().stream().sorted().findFirst();
                    if(winnerRequest.isPresent()) {
                        LoanRequest winner = winnerRequest.get();
                        LOGGER.info("Winner for year {} is {} with a higher bid of {}", loanRequestsBucket.getYear(), winner.getBuyer(), winner.getOffer());
                        Publisher<Double> paymentStagger = this.loanPayer.staggerPaymentForDayAndRequest(loanRequestsBucket.getYear(), winner);
                        if(paymentStagger instanceof Flux) {
                            ((Flux<Double>) paymentStagger).reduce((double) winner.getOffer(), (offer, currentRate) -> offer + currentRate);
                            return paymentStagger;
                        }
                        else {
                            return paymentStagger;
                        }
                    }
                    return Mono.empty();
                })
                // TODO : on error, notify the client that he lost the game ?
                .doOnNext(loanRevenue -> this.fundsManager.addFunds("", loanRevenue))
                // TODO : .onComplete, notify the winner and the other clients ?
                .subscribe();
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
