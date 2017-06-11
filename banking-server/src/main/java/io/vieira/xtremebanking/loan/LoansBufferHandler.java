package io.vieira.xtremebanking.loan;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.loan.generation.BorrowerGenerator;
import io.vieira.xtremebanking.loan.payment.LoanPayer;
import io.vieira.xtremebanking.models.LoanBorrower;
import io.vieira.xtremebanking.models.LoanBorrowerBucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class LoansBufferHandler implements SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoansBufferHandler.class);
    private final LoanRequestsBuffer loanRequestsBuffer;
    private boolean running = false;
    private Disposable loansSubscription;
    private LoanPayer loanPayer;
    private FundsManager fundsManager;
    private final Flux<LoanBorrowerBucket> borrowers;

    public LoansBufferHandler(LoanRequestsBuffer buffer,
                              LoanPayer loanPayer,
                              FundsManager fundsManager,
                              BorrowerGenerator borrowers) {
        this.loanRequestsBuffer = buffer;
        this.loanPayer = loanPayer;
        this.fundsManager = fundsManager;
        this.borrowers = borrowers.getGenerator();
    }

    @Override
    public void start() {
        this.running = true;
        LOGGER.info("Game on ! Starting year 1 and loan requests collection");
        this.loansSubscription = loanRequestsBuffer
                .startBuffering()
                .zipWith(this.borrowers)
                .map(bucketAndBorrowers -> {
                    List<LoanBorrower> borrowers = bucketAndBorrowers.getT2().getBorrowers();
                    LoanRequestBucket bucket = bucketAndBorrowers.getT1();
                    return new LoanRequestBucket(bucket.getYear(), borrowers.stream()
                            .map(borrower -> bucket.getRequests()
                                    .stream()
                                    .filter(request -> request.getLoan().equals(borrower.getId()))
                                    // TODO : add filtering based on theorical cashback ?
                                    .findFirst()
                                    .orElse(null)
                            )
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
                    );
                })
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
