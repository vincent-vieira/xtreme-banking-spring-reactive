package io.vieira.xtremebanking;

import io.vieira.xtremebanking.exception.GlobalExceptionHandler;
import io.vieira.xtremebanking.funds.InMemoryFundsManager;
import io.vieira.xtremebanking.loan.DefaultLoansBuffer;
import io.vieira.xtremebanking.time.TimeConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
        TimeConfiguration.class,
        DefaultLoansBuffer.class,
        LoanController.class,
        InMemoryFundsManager.class,
        GlobalExceptionHandler.class
})
public class BankingServer {

    public static void main(String[] args) {
        new SpringApplication(BankingServer.class).run(args);
    }
}
