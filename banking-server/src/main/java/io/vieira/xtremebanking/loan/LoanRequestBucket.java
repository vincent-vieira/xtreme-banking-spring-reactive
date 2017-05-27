package io.vieira.xtremebanking.loan;

import io.vieira.xtremebanking.models.LoanRequest;

import java.util.List;
import java.util.Objects;

public class LoanRequestBucket {

    private final int year;

    private final List<LoanRequest> requests;

    public LoanRequestBucket(int year, List<LoanRequest> requests) {
        this.year = year;
        this.requests = requests;
    }

    public int getYear() {
        return year;
    }

    public List<LoanRequest> getRequests() {
        return requests;
    }

    @Override
    public String toString() {
        return "LoanRequestBucket{requests=" + Objects.toString(this.requests) + ", year="+ this.year+ "}";
    }
}
