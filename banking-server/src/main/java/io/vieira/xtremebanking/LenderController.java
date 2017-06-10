package io.vieira.xtremebanking;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.models.LenderDeclarationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
public class LenderController {

    private final FundsManager fundsManager;

    public LenderController(FundsManager fundsManager) {
        this.fundsManager = fundsManager;
    }

    @PostMapping(value = "/lender", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void registerNew(@RequestBody @Valid Mono<LenderDeclarationRequest> loanRequest) {
        loanRequest
                .subscribe(lenderDeclarationRequest -> this.fundsManager.tryNewBuyer(lenderDeclarationRequest.getBuyer()))
                .dispose();
    }
}
