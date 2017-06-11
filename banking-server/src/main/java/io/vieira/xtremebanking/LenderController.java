package io.vieira.xtremebanking;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.models.LenderDeclarationRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public Mono<ResponseEntity> register(@RequestBody @Valid Mono<LenderDeclarationRequest> loanRequest) {
        return loanRequest
                .doOnNext(lenderDeclarationRequest -> this.fundsManager.tryNewBuyer(lenderDeclarationRequest.getBuyer()))
                .then(Mono.just(ResponseEntity.accepted().build()));
    }
}
