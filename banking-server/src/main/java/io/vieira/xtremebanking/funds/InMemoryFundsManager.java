package io.vieira.xtremebanking.funds;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryFundsManager implements FundsManager {

    private final Integer initialFunds;
    private final Map<String, Integer> funds = new ConcurrentHashMap<>();

    public InMemoryFundsManager(@Value("${xtreme-banking.initial-cash:100000}") Integer initialFunds) {
        this.initialFunds = initialFunds;
    }

    @Override
    public void tryNewBuyer(String buyerId) {
        this.funds.putIfAbsent(buyerId, this.initialFunds);
    }

    @Override
    public Map<String, Integer> getCurrentFunds() {
        return this.funds;
    }

    @Override
    public void spend(String buyerId, int toSpend) {
        if(this.hasEnoughFunds(buyerId, toSpend)) this.funds.computeIfPresent(buyerId, (buyer, funds) -> funds - toSpend);
    }

    @Override
    public boolean hasEnoughFunds(String buyerId, int required) {
        return this.funds.getOrDefault(buyerId, 0) >= required;
    }
}
