package io.vieira.xtremebanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = YearsLoop.class)
public class BankingServer {

    public static void main(String[] args) {
        new SpringApplication(BankingServer.class).run(args);
    }
}
