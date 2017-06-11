package io.vieira.xtremebanking;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.models.LenderDeclarationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RequestMapping("/lender")
@RestController
public class LenderController {

    private final FundsManager fundsManager;

    public LenderController(FundsManager fundsManager) {
        this.fundsManager = fundsManager;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void register(@RequestBody @Valid Mono<LenderDeclarationRequest> loanRequest) {
        loanRequest
                .subscribe(lenderDeclarationRequest -> this.fundsManager.tryNewBuyer(lenderDeclarationRequest.getBuyer()))
                .dispose();
    }
}
