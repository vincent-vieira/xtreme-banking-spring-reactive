package io.vieira.xtremebanking;

import io.vieira.xtremebanking.exception.GlobalExceptionHandler;
import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.loan.LoanRequestsBuffer;
import io.vieira.xtremebanking.loan.generation.BorrowerGenerator;
import io.vieira.xtremebanking.models.LoanBorrower;
import io.vieira.xtremebanking.models.LoanBorrowerBucket;
import io.vieira.xtremebanking.models.LoanRequest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@WebFluxTest(LoanController.class)
@TestPropertySource(properties = {
        "xtreme-banking.initial-cash=500",
        "xtreme-banking.call-cost=150"
})
@ComponentScan(basePackageClasses = {
        FundsManager.class,
        GlobalExceptionHandler.class
})
public class LoanControllerTest {

    // This annotation is just pure magic.
    @TestComponent
    static class TestBorrowerGenerator implements BorrowerGenerator {

        @Override
        public Flux<LoanBorrowerBucket> getGenerator() {
            return Flux.just(new LoanBorrowerBucket(1, Collections.singletonList(new LoanBorrower("idloan", 10000, 102))));
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private LoanRequestsBuffer buffer;

    @Autowired
    private FundsManager fundsManager;

    @Before
    public void setup() {
        // The buyer should be already created
        fundsManager.tryNewBuyer("test");
    }

    @Test
    // TODO : uncomment when the .zipWith() issue on LoanController will be fixed.
    @Ignore
    public void calling_with_a_faulty_loan_request_should_decrement_the_funds_with_the_call_cost_only() throws Exception {
        webTestClient
                .post()
                .uri("loans")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(new LoanRequest("test", "loan")))
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void calling_with_a_loan_request_with_an_unknown_user_should_return_an_error() throws Exception {
        webTestClient
                .post()
                .uri("loans")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(new LoanRequest("test2", "idloan")))
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void calling_with_a_valid_loan_request_should_decrement_the_funds_with_the_call_cost_only() throws Exception {
        webTestClient
                .post()
                .uri("loans")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(new LoanRequest("test", "idloan")))
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.ACCEPTED);
        
        assertThat(fundsManager.getCurrentFunds()).containsEntry("test", 350D);
    }
}
