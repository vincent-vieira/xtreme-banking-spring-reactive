package io.vieira.xtremebanking.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class LoanRequest {

    @JsonCreator
    public LoanRequest(@JsonProperty("buyer") String buyer, @JsonProperty("loan") String loan) {
        this.buyer = buyer;
        this.loan = loan;
    }

    @NotEmpty
    private String buyer;

    @NotEmpty
    private final String loan;

    public String getBuyer() {
        return buyer;
    }

    public String getLoan() {
        return loan;
    }

    @Override
    public String toString() {
        return "LoanRequest{buyer="+ this.buyer + ", loan="+ this.loan +"}";
    }
}
