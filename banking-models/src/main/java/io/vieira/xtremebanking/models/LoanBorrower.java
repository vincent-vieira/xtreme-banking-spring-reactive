package io.vieira.xtremebanking.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoanBorrower {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("borrowed")
    private final Integer amount;

    @JsonProperty("immediate")
    private final Integer immediatePayment;

    @JsonCreator
    public LoanBorrower(@JsonProperty("id") String id, @JsonProperty("borrowed") Integer amount, @JsonProperty("immediate") Integer immediatePayment) {
        this.id = id;
        this.amount = amount;
        this.immediatePayment = immediatePayment;
    }

    public String getId() {
        return id;
    }

    public Integer getAmount() {
        return amount;
    }

    public Integer getImmediatePayment() {
        return immediatePayment;
    }

    @Override
    public String toString() {
        return "LoanBorrower{id="+this.id+", amount="+this.amount+", immediatePayment="+this.immediatePayment+"}";
    }
}
