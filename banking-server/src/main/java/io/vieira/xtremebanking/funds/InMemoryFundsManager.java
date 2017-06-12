package io.vieira.xtremebanking.funds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryFundsManager implements FundsManager {

    private final Double initialFunds;
    private final Map<String, Double> funds = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryFundsManager.class);

    public InMemoryFundsManager(@Value("${xtreme-banking.initial-cash:100000}") Double initialFunds) {
        this.initialFunds = initialFunds;
    }

    @Override
    public void tryNewBuyer(String buyerId) {
        if(this.funds.putIfAbsent(buyerId, this.initialFunds) == null) {
            LOGGER.info("New buyer '{}' added to fund management.", buyerId);
        }
    }

    @Override
    public Map<String, Double> getCurrentFunds() {
        return this.funds;
    }

    @Override
    public void spend(String buyerId, double toSpend) {
        if(!this.funds.containsKey(buyerId)) {
            throw new BuyerNotFoundException(buyerId);
        }
        // We're in a stock exchange bank. We don't care about spending more money than we have. ;)
        LOGGER.info("Buyer '{}' now has {}$", buyerId, this.funds.compute(buyerId, (buyer, funds) -> funds - toSpend));
    }

    @Override
    public boolean hasEnoughFunds(String buyerId, double required) {
        if(!this.funds.containsKey(buyerId)) {
            throw new BuyerNotFoundException(buyerId);
        }
        return this.funds.get(buyerId) >= required;
    }

    @Override
    public void addFunds(String buyerId, double revenue) {
        if(!this.funds.containsKey(buyerId)) {
            throw new BuyerNotFoundException(buyerId);
        }

        LOGGER.info("Buyer '{}' now has {}$", buyerId, this.funds.compute(buyerId, (buyer, funds) -> funds + revenue));
    }
}
