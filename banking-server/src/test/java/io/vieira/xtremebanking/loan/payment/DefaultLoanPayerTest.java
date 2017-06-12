package io.vieira.xtremebanking.loan.payment;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.funds.InMemoryFundsManager;
import io.vieira.xtremebanking.models.ConcludedLoanDeal;
import io.vieira.xtremebanking.models.LoanBorrower;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static io.vieira.xtremebanking.loan.payment.LoanPayer.daysInAYear;
import static io.vieira.xtremebanking.loan.payment.LoanPayer.monthsInAYear;
import static io.vieira.xtremebanking.time.YearGenerator.getDuration;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(BlockJUnit4ClassRunner.class)
public class DefaultLoanPayerTest {

    @Spy
    private final FundsManager fundsManager = new InMemoryFundsManager(500000D);

    private final double baseRate = 0.25;

    private final double specialRate = 0.5;

    private final double insuranceFee = 200;

    private LoanPayer loanPayer;

    private final ConcludedLoanDeal concludedDeal = new ConcludedLoanDeal("test", new LoanBorrower(
            "loan",
            1500000,
            20000
    ));

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        fundsManager.tryNewBuyer(concludedDeal.getBuyer());
        this.loanPayer = new DefaultLoanPayer(this.fundsManager, this.baseRate, this.specialRate, this.insuranceFee);
        this.fundsManager.spend(concludedDeal.getBuyer(), concludedDeal.getBorrower().getAmount() - concludedDeal.getBorrower().getImmediatePayment());
    }

    @Test
    public void year_one_must_stagger_a_yearly_payment() {
        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(1, concludedDeal))
                .thenAwait(getDuration())
                .expectNext(concludedDeal.getBorrower().getAmount() * (1 + this.baseRate))
                .verifyComplete();
    }

    @Test
    public void year_two_must_stagger_a_monthly_payment() {
        Double[] monthRates = new Double[monthsInAYear];
        Arrays.setAll(monthRates, value -> concludedDeal.getBorrower().getAmount() * (this.baseRate / monthsInAYear));

        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(2, concludedDeal))
                .thenAwait(getDuration())
                .expectNext(monthRates)
                .verifyComplete();
    }

    @Test
    public void year_three_must_stagger_a_daily_payment() {
        Double[] dayRates = new Double[(int) daysInAYear];
        Arrays.setAll(dayRates, value -> concludedDeal.getBorrower().getAmount() * (this.baseRate / daysInAYear));

        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(3, concludedDeal))
                .thenAwait(getDuration())
                .expectNext(dayRates)
                .verifyComplete();
    }

    @Test
    public void year_four_must_stagger_a_daily_payment_with_unexpected_fund_checks() {
        Double[] dayRates = new Double[(int) daysInAYear];
        Arrays.setAll(dayRates, value -> concludedDeal.getBorrower().getAmount() * (this.baseRate / daysInAYear));

        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(4, concludedDeal))
                .thenAwait(getDuration())
                .expectNext(dayRates)
                .verifyComplete();

        verify(fundsManager, times(monthsInAYear)).hasEnoughFunds(eq(concludedDeal.getBuyer()), eq(100000D));
    }


    @Test
    public void year_four_must_stagger_a_daily_payment_with_unexpected_fund_checks_and_fine_the_buyer_when_hes_not_following_regulations() {
        fundsManager.spend(concludedDeal.getBuyer(), 401000D);
        Double[] dayRates = new Double[(int) daysInAYear];
        Arrays.setAll(dayRates, value -> concludedDeal.getBorrower().getAmount() * (this.baseRate / daysInAYear));

        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(4, concludedDeal))
                .thenAwait(getDuration())
                .expectNext(dayRates)
                .verifyComplete();

        verify(fundsManager, times(monthsInAYear)).spend(eq(concludedDeal.getBuyer()), eq(5000D));
    }

    @Test
    public void year_five_must_stagger_a_daily_payment_with_special_rates() {
        Double[] dayRates = new Double[(int) daysInAYear];
        Arrays.setAll(dayRates, value -> concludedDeal.getBorrower().getAmount() * ((this.baseRate + this.specialRate) / daysInAYear));

        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(5, concludedDeal))
                .thenAwait(getDuration())
                .expectNext(dayRates)
                .verifyComplete();
    }

    @Test
    public void year_six_must_stagger_a_monthly_payment_with_insurance() {
        Double[] monthRates = new Double[monthsInAYear];
        Arrays.setAll(monthRates, value -> concludedDeal.getBorrower().getAmount() * (this.baseRate / monthsInAYear));

        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(6, concludedDeal))
                .thenAwait(getDuration())
                .expectNext(monthRates)
                .verifyComplete();

        verify(fundsManager, times(monthsInAYear)).spend(eq(concludedDeal.getBuyer()), eq(this.insuranceFee));
    }

    @Test()
    public void another_year_should_not_be_eligible_to_any_payment() {
        StepVerifier.withVirtualTime(() -> loanPayer.staggerPaymentForYearAndRequest(7, concludedDeal))
                .expectComplete()
                .verify();
    }
}
