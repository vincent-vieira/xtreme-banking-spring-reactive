package io.vieira.xtremebanking.loan.payment;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.funds.InMemoryFundsManager;
import io.vieira.xtremebanking.models.LoanRequest;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Spy;

@RunWith(BlockJUnit4ClassRunner.class)
public class DefaultLoanPayerTest {

    @Spy
    private final FundsManager fundsManager = new InMemoryFundsManager(500000D);

    private final double baseRate = 0.25;

    private final double specialRate = 0.5;

    private final double insuranceFee = 200;

    private LoanPayer loanPayer;

    private final LoanRequest sampleLoanRequest = new LoanRequest("test", "loan");

    /*@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        fundsManager.tryNewBuyer(sampleLoanRequest.getBuyer());
        this.loanPayer = new DefaultLoanPayer(this.fundsManager, this.baseRate, this.specialRate, this.insuranceFee);
    }

    @Test
    public void year_one_must_stagger_a_yearly_payment() {
        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(1, sampleLoanRequest))
                .thenAwait(getDuration())
                .expectNext(sampleLoanRequest.getOffer() * (1 + this.baseRate))
                .verifyComplete();
    }

    @Test
    public void year_two_must_stagger_a_monthly_payment() {
        Double[] monthRates = new Double[monthsInAYear];
        Arrays.setAll(monthRates, value -> sampleLoanRequest.getOffer() * (this.baseRate / monthsInAYear));

        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(2, this.sampleLoanRequest))
                .thenAwait(getDuration())
                .expectNext(monthRates)
                .verifyComplete();
    }

    @Test
    public void year_three_must_stagger_a_daily_payment() {
        Double[] dayRates = new Double[(int) daysInAYear];
        Arrays.setAll(dayRates, value -> sampleLoanRequest.getOffer() * (this.baseRate / daysInAYear));

        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(3, this.sampleLoanRequest))
                .thenAwait(getDuration())
                .expectNext(dayRates)
                .verifyComplete();
    }

    @Test
    public void year_four_must_stagger_a_daily_payment_with_unexpected_fund_checks() {
        Double[] dayRates = new Double[(int) daysInAYear];
        Arrays.setAll(dayRates, value -> sampleLoanRequest.getOffer() * (this.baseRate / daysInAYear));

        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(4, this.sampleLoanRequest))
                .thenAwait(getDuration())
                .expectNext(dayRates)
                .verifyComplete();

        verify(fundsManager, times(monthsInAYear)).hasEnoughFunds(eq(sampleLoanRequest.getBuyer()), eq(100000D));
    }


    @Test
    public void year_four_must_stagger_a_daily_payment_with_unexpected_fund_checks_and_fine_the_buyer_when_hes_not_following_regulations() {
        fundsManager.spend(sampleLoanRequest.getBuyer(), 401000D);
        Double[] dayRates = new Double[(int) daysInAYear];
        Arrays.setAll(dayRates, value -> sampleLoanRequest.getOffer() * (this.baseRate / daysInAYear));

        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(4, this.sampleLoanRequest))
                .thenAwait(getDuration())
                .expectNext(dayRates)
                .verifyComplete();

        verify(fundsManager, times(monthsInAYear)).spend(eq(sampleLoanRequest.getBuyer()), eq(5000D));
    }

    @Test
    public void year_five_must_stagger_a_daily_payment_with_special_rates() {
        Double[] dayRates = new Double[(int) daysInAYear];
        Arrays.setAll(dayRates, value -> sampleLoanRequest.getOffer() * ((this.baseRate + this.specialRate) / daysInAYear));

        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(5, this.sampleLoanRequest))
                .thenAwait(getDuration())
                .expectNext(dayRates)
                .verifyComplete();
    }

    @Test
    public void year_six_must_stagger_a_monthly_payment_with_insurance() {
        Double[] monthRates = new Double[monthsInAYear];
        Arrays.setAll(monthRates, value -> sampleLoanRequest.getOffer() * (this.baseRate / monthsInAYear));

        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(6, this.sampleLoanRequest))
                .thenAwait(getDuration())
                .expectNext(monthRates)
                .verifyComplete();

        verify(fundsManager, times(monthsInAYear)).spend(eq(sampleLoanRequest.getBuyer()), eq(this.insuranceFee));
    }

    @Test()
    public void another_year_should_not_be_eligible_to_any_payment() {
        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(7, this.sampleLoanRequest))
                .expectComplete()
                .verify();
    }*/
}
