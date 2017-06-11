package io.vieira.xtremebanking.time;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

@Configuration
public class TimeConfiguration {

    @Bean
    public Flux<Integer> yearEndGenerator(@Value("${xtreme-banking.max-years:6}") int maxYears) {
        return YearGenerator.max(maxYears).create();
    }

    @Bean
    public Flux<Integer> yearBeginGenerator(@Value("${xtreme-banking.max-years:6}") int maxYears) {
        return YearGenerator.max(maxYears, YearGenerator.EmitMode.BEGIN).create();
    }
}
