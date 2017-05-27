package io.vieira.xtremebanking.funds;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(BlockJUnit4ClassRunner.class)
public class InMemoryFundsManagerTest {

    @Test
    public void should_throw_exception_when_buyer_doesnt_exist() {
        InMemoryFundsManager manager = new InMemoryFundsManager(50);
        assertThat(catchThrowable(() -> manager.hasEnoughFunds("test", 40)))
                .isInstanceOf(FundsManager.BuyerNotFoundException.class)
                .hasMessageContaining("has not been found");
    }

    @Test
    public void should_add_proper_funds_to_a_new_buyer() {
        InMemoryFundsManager manager = new InMemoryFundsManager(50);
        manager.tryNewBuyer("test");

        assertThat(manager.getCurrentFunds()).containsEntry("test", 50).doesNotContainKey("test2");
    }

    @Test
    public void should_check_for_funds_properly() {
        InMemoryFundsManager manager = new InMemoryFundsManager(50);
        manager.tryNewBuyer("test");

        assertThat(manager.hasEnoughFunds("test", 40)).isTrue();
        assertThat(manager.hasEnoughFunds("test", 70)).isFalse();
    }

    @Test
    public void should_spend_funds_properly() {
        InMemoryFundsManager manager = new InMemoryFundsManager(50);
        manager.tryNewBuyer("test");

        //It shouldn't reset the buyer's funds when accidently re-adding him.
        manager.spend("test", 20);
        manager.tryNewBuyer("test");
        assertThat(manager.getCurrentFunds()).containsEntry("test", 30);
        assertThat(manager.hasEnoughFunds("test", 40)).isFalse();
        assertThat(manager.hasEnoughFunds("test", 10)).isTrue();

        //Spending more than the buyer's have on purpose should do nothing
        manager.spend("test", 150);
        assertThat(manager.getCurrentFunds()).containsEntry("test", 30);
    }
}
