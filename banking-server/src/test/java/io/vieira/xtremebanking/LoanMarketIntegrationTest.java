package io.vieira.xtremebanking;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.loan.payment.LoanPayer;
import io.vieira.xtremebanking.models.LenderDeclarationRequest;
import io.vieira.xtremebanking.models.LoanBorrower;
import io.vieira.xtremebanking.models.LoanBorrowerBucket;
import io.vieira.xtremebanking.models.LoanRequest;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.TimeUnit;

import static io.vieira.xtremebanking.time.YearGenerator.getDuration;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port=8080",
                "xtreme-banking.initial-cash=1000000",
                "xtreme-banking.call-cost=150"
        }
)
@AutoConfigureWebTestClient
public class LoanMarketIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @SpyBean
    private FundsManager fundsManager;

    @SpyBean
    private LoanPayer payer;

    private final WebClient webClient = WebClient.create("http://localhost:8080");

    @Before
    public void setupTest() {
        webTestClient
                .post()
                .uri("lender")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(new LenderDeclarationRequest("buyer1")))
                .exchange()
                .expectStatus()
                .isAccepted();
    }

    @Test
    public void first_bidder_on_same_loan_should_earn_money_at_the_end_of_the_year() throws Exception {
        LoanBorrower borrower = webClient
                .get()
                .uri("loans")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .flatMapMany(response -> response.bodyToFlux(LoanBorrowerBucket.class))
                .map(LoanBorrowerBucket::getBorrowers)
                .map(borrowers -> borrowers.get(RandomUtils.nextInt(0, borrowers.size())))
                .take(1)
                .blockFirst();

        webTestClient
                .post()
                .uri("loans")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(new LoanRequest("buyer1", borrower.getId())))
                .exchange()
                .expectStatus()
                .isAccepted();

        await().atMost(getDuration().multipliedBy(2).getSeconds(), TimeUnit.SECONDS).untilAsserted(() -> {
            // Once for the call cost, once for the loan begin.
            verify(fundsManager, times(2)).spend(eq("buyer1"), anyDouble());
            verify(payer).staggerPaymentForYearAndRequest(anyInt(), argThat(concludedLoanDeal -> concludedLoanDeal.getBuyer().equals("buyer1")));
            verify(fundsManager, atLeastOnce()).addFunds(eq("buyer1"), anyDouble());
        });
    }
}
