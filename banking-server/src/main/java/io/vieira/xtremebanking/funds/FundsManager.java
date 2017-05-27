package io.vieira.xtremebanking.funds;

import java.util.Map;

public interface FundsManager {
    void tryNewBuyer(String buyerId);
    Map<String, Integer> getCurrentFunds();
    void spend(String buyerId, int toSpend);
    boolean hasEnoughFunds(String buyerId, int required);
}
