package io.vieira.xtremebanking;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.funds.NotEnoughFundsException;
import io.vieira.xtremebanking.loan.LoansBuffer;
import io.vieira.xtremebanking.models.LoanRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
public class LoanController {

    private final LoansBuffer loansBuffer;
    private final FundsManager fundsManager;
    private final Integer callCost;

    public LoanController(LoansBuffer buffer, FundsManager fundsManager, @Value("${xtreme-banking.call-cost:10}") Integer callCost) {
        this.loansBuffer = buffer;
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
                .doOnSuccess(loansBuffer::newLoanRequested);
    }

    // TODO : SSE endpoint to notify clients
}
