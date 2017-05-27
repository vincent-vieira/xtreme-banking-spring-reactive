package io.vieira.xtremebanking.loan.payment;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.models.LoanRequest;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DefaultLoanPayer implements LoanPayer {

    private final FundsManager fundsManager;
    private final Double baseRate;
    private final Double specialRate;
    private final Double insuranceFee;
    private final Double rateMultiplier;

    public DefaultLoanPayer(FundsManager fundsManager,
                            @Value("${xtreme-banking.base-rate:0.15}") Double baseRate,
                            @Value("${xtreme-banking.special-rate:0.2}") Double specialRate,
                            @Value("${xtreme-banking.insurance-fee:250}") Double insuranceFee) {
        this.fundsManager = fundsManager;
        this.baseRate = baseRate;
        this.specialRate = specialRate;
        this.insuranceFee = insuranceFee;
        this.rateMultiplier = 1 + this.baseRate;
    }

    @Override
    public Publisher<Double> staggerPaymentForDayAndRequest(int yearNumber, LoanRequest request) {
        Publisher<Double> toReturn = null;

        switch(yearNumber) {
            case 1:
                toReturn = Mono
                        .just(request.getOffer())
                        .delayElement(yearDuration)
                        .map(offer -> offer * rateMultiplier);
                break;
            case 2:
                toReturn = Mono.just(request.getOffer())
                        .repeat(monthsInAYear)
                        .delayElements(monthDuration)
                        .map(offer -> offer * (this.baseRate / monthsInAYear));
                break;
            case 3:
            case 4:
            case 5:
                toReturn = Mono.just(request.getOffer())
                        .repeat(daysInAYear)
                        .delayElements(dayDuration)
                        .map(offer -> offer * ((yearNumber == 5 ? this.baseRate + this.specialRate : this.baseRate) / daysInAYear))
                        .doOnNext(amountToAdd -> {
                            if(yearNumber == 4 && !this.fundsManager.hasEnoughFunds(request.getBuyer(), 100000)) {
                                // Fine him.
                                this.fundsManager.spend(request.getBuyer(), 5000);
                            }
                        });
                break;
            case 6:
                toReturn = Mono.just(request.getOffer())
                        .repeat(monthsInAYear)
                        .delayElements(monthDuration)
                        .map(offer -> offer * (this.baseRate / monthsInAYear))
                        // Insurance
                        .doOnNext(amountToAdd -> this.fundsManager.spend(request.getBuyer(), this.insuranceFee));
                break;
            default:
                toReturn = Mono.empty();
                break;
        }

        return toReturn;
    }
}
