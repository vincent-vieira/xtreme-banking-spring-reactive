package io.vieira.xtremebanking.funds;

import java.util.Map;

public interface FundsManager {
    void tryNewBuyer(String buyerId);
    Map<String, Integer> getCurrentFunds();
    void spend(String buyerId, int toSpend);
    boolean hasEnoughFunds(String buyerId, int required);

    class BuyerNotFoundException extends RuntimeException {

        private final String buyer;

        public BuyerNotFoundException(String buyer) {
            super(String.format("Buyer '%s' has not been found.", buyer));
            this.buyer = buyer;
        }

        public String getBuyer() {
            return buyer;
        }
    }
}
