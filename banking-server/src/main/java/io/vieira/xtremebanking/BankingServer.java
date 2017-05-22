package io.vieira.xtremebanking;

import io.vieira.xtremebanking.loan.LoansBuffer;
import io.vieira.xtremebanking.time.TimeConfiguration;
import io.vieira.xtremebanking.time.YearLooper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {
        YearLooper.class,
        TimeConfiguration.class,
        LoansBuffer.class,
        LoanController.class
})
public class BankingServer {

    public static void main(String[] args) {
        new SpringApplication(BankingServer.class).run(args);
    }
}
