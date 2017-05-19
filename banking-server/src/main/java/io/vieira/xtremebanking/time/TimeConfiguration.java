package io.vieira.xtremebanking.time;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

@Configuration
public class TimeConfiguration {

    @Bean
    public Flux<Integer> generator(@Value("${xtreme-banking.max-years:7}") int maxYears) {
        return YearGenerator.max(maxYears).create();
    }
}
