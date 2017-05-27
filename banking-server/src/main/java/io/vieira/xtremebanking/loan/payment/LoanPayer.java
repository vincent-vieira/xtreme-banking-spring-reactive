package io.vieira.xtremebanking.loan.payment;

import io.vieira.xtremebanking.models.LoanRequest;
import org.reactivestreams.Publisher;

import java.time.Duration;

import static io.vieira.xtremebanking.time.YearGenerator.getDuration;

@FunctionalInterface
public interface LoanPayer {
    Duration yearDuration = getDuration();
    int monthsInAYear = 12;
    Duration monthDuration = getDuration().dividedBy(monthsInAYear);
    // https://www.quora.com/What-is-the-average-number-of-days-in-a-month
    long daysInAMonth = (long) 30.44;
    long daysInAYear = daysInAMonth * monthsInAYear;
    Duration dayDuration = monthDuration.dividedBy(daysInAMonth);

    // The payment can be either in one-shot, or staggered across multiple times. We need the base interface,
    // then, we can't choose between Flux and Mono.
    Publisher<Double> staggerPaymentForDayAndRequest(int yearNumber, LoanRequest request);
}
