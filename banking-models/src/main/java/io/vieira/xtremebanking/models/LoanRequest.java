package io.vieira.xtremebanking.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class LoanRequest implements Comparable<LoanRequest> {

    @JsonCreator
    public LoanRequest(@JsonProperty("buyer") String buyer, @JsonProperty("offer") int offer) {
        this.buyer = buyer;
        this.offer = offer;
    }

    @NotNull
    private String buyer;

    private int offer;

    public String getBuyer() {
        return buyer;
    }

    public int getOffer() {
        return offer;
    }

    @Override
    public String toString() {
        return "LoanRequest{buyer="+ this.buyer + ", offer="+ this.offer +"}";
    }

    @Override
    public int compareTo(LoanRequest o) {
        if(o == null) return 1;
        return Integer.compare(o.getOffer(), this.offer);
    }
}
