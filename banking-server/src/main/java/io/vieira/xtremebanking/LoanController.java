package io.vieira.xtremebanking;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.funds.NotEnoughFundsException;
import io.vieira.xtremebanking.loan.LoanRequestsBuffer;
import io.vieira.xtremebanking.models.LoanRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.Duration;

@RestController
public class LoanController {

    private final LoanRequestsBuffer loanRequestsBuffer;
    private final FundsManager fundsManager;
    private final Integer callCost;

    public LoanController(LoanRequestsBuffer buffer, FundsManager fundsManager, @Value("${xtreme-banking.call-cost:10}") Integer callCost) {
        this.loanRequestsBuffer = buffer;
        this.fundsManager = fundsManager;
        this.callCost = callCost;
    }

    @PostMapping(value = "/loan", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<LoanRequest> loanStream(@RequestBody @Valid Mono<LoanRequest> loanRequest) {
        return loanRequest
                // Always make sure a new client has its initial funds
                .doOnNext(request -> this.fundsManager.tryNewBuyer(request.getBuyer()))
                .flatMap(request -> {
                    if(!this.fundsManager.hasEnoughFunds(request.getBuyer(), request.getOffer())) {
                        return Mono.error(new NotEnoughFundsException(request));
                    }
                    return Mono.just(request);
                })
                .doOnNext(request -> this.fundsManager.spend(request.getBuyer(), this.callCost))
                .doOnSuccess(loanRequestsBuffer::newLoanRequested);
    }

    @GetMapping(value = "/loan", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Object> eventStream() {
        return Flux.interval(Duration.ofSeconds(2)).map(aLong -> new LoanRequest("etst", 150));
    }
}
