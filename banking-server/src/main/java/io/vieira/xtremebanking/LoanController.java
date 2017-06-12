package io.vieira.xtremebanking;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.loan.LoanRequestsBuffer;
import io.vieira.xtremebanking.loan.generation.BorrowerGenerator;
import io.vieira.xtremebanking.models.LoanBorrowerBucket;
import io.vieira.xtremebanking.models.LoanRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(LoanController.class);

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
                // TODO : fix this, in order to check that the loan is valid and that the buyer has enough money.
                // TODO : Actually, the .zipWith() call returns an empty observable straight away.
                /*.zipWith(this.borrowers, (request, loanBorrowers) -> {
                    Optional<LoanBorrower> borrower = loanBorrowers.getBorrowers().stream().filter(loanBorrower -> loanBorrower.getId().equals(request.getLoan())).findFirst();
                    if(!borrower.isPresent()) {
                        throw new LoanNotFoundException(request);
                    }
                    if(!this.fundsManager.hasEnoughFunds(request.getBuyer(), borrower.getAmount()) {
                        throw new NotEnoughFundsException(request);
                    }
                    return request;
                })*/
                .doOnNext(loanRequestsBuffer::newLoanRequested)
                .next()
                .flatMap(request -> Mono.just(ResponseEntity.accepted().build()));
    }

    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<LoanBorrowerBucket> currentBorrowers() {
        return this.borrowers;
    }
}
