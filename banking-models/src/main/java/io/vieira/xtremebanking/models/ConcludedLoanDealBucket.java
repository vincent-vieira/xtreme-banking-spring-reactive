package io.vieira.xtremebanking.models;

import java.util.List;

public class ConcludedLoanDealBucket {

   private final int year;

   private final List<ConcludedLoanDeal> concludedDeals;

    public ConcludedLoanDealBucket(int year, List<ConcludedLoanDeal> concludedDeals) {
        this.year = year;
        this.concludedDeals = concludedDeals;
    }

    public int getYear() {
        return year;
    }

    public List<ConcludedLoanDeal> getConcludedDeals() {
        return concludedDeals;
    }
}


