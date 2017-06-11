package io.vieira.xtremebanking;

import io.vieira.xtremebanking.exception.GlobalExceptionHandler;
import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.models.LenderDeclarationRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@WebFluxTest(LenderController.class)
@TestPropertySource(properties = {
        "xtreme-banking.initial-cash=500",
        "xtreme-banking.call-cost=150"
})
@ComponentScan(basePackageClasses = {
        FundsManager.class,
        GlobalExceptionHandler.class
})
public class LenderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private FundsManager fundsManager;

    @Test
    public void adding_a_new_lender_should_populate_funds_manager() throws Exception {
        webTestClient
                .post()
                .uri("lender")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(new LenderDeclarationRequest("test")))
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.ACCEPTED);
        assertThat(fundsManager.getCurrentFunds()).containsEntry("test", 500D);
    }
}
