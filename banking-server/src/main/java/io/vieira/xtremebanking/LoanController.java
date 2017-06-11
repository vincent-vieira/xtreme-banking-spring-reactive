package io.vieira.xtremebanking;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.loan.LoanNotFoundException;
import io.vieira.xtremebanking.loan.LoanRequestsBuffer;
import io.vieira.xtremebanking.loan.generation.BorrowerGenerator;
import io.vieira.xtremebanking.models.LoanBorrowerBucket;
import io.vieira.xtremebanking.models.LoanRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanRequestsBuffer loanRequestsBuffer;
    private final FundsManager fundsManager;
    private final Integer callCost;
    private final Flux<LoanBorrowerBucket> borrowers;

    public LoanController(LoanRequestsBuffer buffer,
                          FundsManager fundsManager,
                          @Value("${xtreme-banking.call-cost:10}") Integer callCost,
                          BorrowerGenerator borrowers) {
        this.loanRequestsBuffer = buffer;
        this.fundsManager = fundsManager;
        this.callCost = callCost;
        this.borrowers = borrowers.getGenerator();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<ResponseEntity> bid(@RequestBody @Valid Mono<LoanRequest> bidRequest) {
        return bidRequest
                .flux()
                .doOnNext(request -> this.fundsManager.spend(request.getBuyer(), this.callCost))
                .withLatestFrom(this.borrowers, (request, loanBorrowers) -> {
                    if(loanBorrowers.getBorrowers().stream().anyMatch(loanBorrower -> loanBorrower.getId().equals(request.getLoan()))) {
                        return request;
                    }
                    throw new LoanNotFoundException(request);
                })
                .doOnNext(loanRequestsBuffer::newLoanRequested)
                .then(Mono.just(ResponseEntity.accepted().build()));
    }

    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<LoanBorrowerBucket> currentBorrowers() {
        return this.borrowers;
    }
}
