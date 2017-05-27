package io.vieira.xtremebanking.funds;

import java.util.Map;

public interface FundsManager {
    void tryNewBuyer(String buyerId);
    Map<String, Double> getCurrentFunds();
    void spend(String buyerId, double toSpend);
    boolean hasEnoughFunds(String buyerId, double required);
    void addFunds(String buyerId, double revenue);

    class BuyerNotFoundException extends RuntimeException {

        private final String buyer;

        BuyerNotFoundException(String buyer) {
            super(String.format("Buyer '%s' has not been found.", buyer));
            this.buyer = buyer;
        }

        public String getBuyer() {
            return buyer;
        }
    }
}
