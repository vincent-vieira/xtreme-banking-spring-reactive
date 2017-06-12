package io.vieira.xtremebanking.models;

public class ConcludedLoanDeal {

    private final String buyer;

    private final LoanBorrower borrower;

    public ConcludedLoanDeal(String buyer, LoanBorrower borrower) {
        this.buyer = buyer;
        this.borrower = borrower;
    }

    public String getBuyer() {
        return buyer;
    }

    public LoanBorrower getBorrower() {
        return borrower;
    }
}


