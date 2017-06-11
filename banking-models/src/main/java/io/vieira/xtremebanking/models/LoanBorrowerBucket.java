package io.vieira.xtremebanking.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class LoanBorrowerBucket {

    private final int year;
    private final List<LoanBorrower> borrowers;

    @JsonCreator
    public LoanBorrowerBucket(@JsonProperty("year") int year, @JsonProperty("borrowers") List<LoanBorrower> borrowers) {
        this.year = year;
        this.borrowers = borrowers;
    }

    public int getYear() {
        return year;
    }

    public List<LoanBorrower> getBorrowers() {
        return borrowers;
    }

    @Override
    public String toString() {
        return "LoanBorrowerBucket{year="+this.year+", borrowers="+ Objects.toString(this.borrowers)+"}";
    }
}
