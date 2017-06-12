package io.vieira.xtremebanking;

import io.vieira.xtremebanking.exception.GlobalExceptionHandler;
import io.vieira.xtremebanking.funds.InMemoryFundsManager;
import io.vieira.xtremebanking.loan.DefaultLoanRequestsBuffer;
import io.vieira.xtremebanking.loan.generation.BorrowerGenerator;
import io.vieira.xtremebanking.time.TimeConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO : map all @Values to @ConfigurationProperty ?
@SpringBootApplication(scanBasePackageClasses = {
        TimeConfiguration.class,
        BorrowerGenerator.class,
        DefaultLoanRequestsBuffer.class,
        LoanController.class,
        InMemoryFundsManager.class,
        GlobalExceptionHandler.class
})
public class BankingServer {

    public static void main(String[] args) {
        new SpringApplication(BankingServer.class).run(args);
    }
}
