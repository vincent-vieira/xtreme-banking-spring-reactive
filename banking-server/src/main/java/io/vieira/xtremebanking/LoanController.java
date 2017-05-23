package io.vieira.xtremebanking;

import io.vieira.xtremebanking.loan.DefaultLoansBuffer;
import io.vieira.xtremebanking.models.LoanRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
public class LoanController {

    private final DefaultLoansBuffer loansBuffer;

    public LoanController(DefaultLoansBuffer buffer) {
        this.loansBuffer = buffer;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "/loan")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void loanStream(@RequestBody @Valid Mono<LoanRequest> loanRequest) {
        loanRequest.subscribe(loansBuffer::newLoanRequested).dispose();
    }
}
