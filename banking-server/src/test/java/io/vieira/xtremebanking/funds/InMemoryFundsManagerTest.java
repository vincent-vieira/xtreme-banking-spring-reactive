package io.vieira.xtremebanking.funds;

import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(BlockJUnit4ClassRunner.class)
public class InMemoryFundsManagerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final FundsManager manager = new InMemoryFundsManager(50D);

    @Test
    public void should_throw_exception_when_buyer_doesnt_exist() {
        assertThat(catchThrowable(() -> manager.hasEnoughFunds("test", 40)))
                .isInstanceOf(FundsManager.BuyerNotFoundException.class)
                .hasMessageContaining("has not been found");
    }

    @Test
    public void should_add_proper_funds_to_a_new_buyer() {
        manager.tryNewBuyer("test");

        assertThat(manager.getCurrentFunds()).containsEntry("test", 50D).doesNotContainKey("test2");
    }

    @Test
    public void should_check_for_funds_properly() {
        manager.tryNewBuyer("test");

        assertThat(manager.hasEnoughFunds("test", 40)).isTrue();
        assertThat(manager.hasEnoughFunds("test", 70)).isFalse();
    }

    @Test
    public void should_spend_funds_properly() {
        manager.tryNewBuyer("test");

        //It shouldn't reset the buyer's funds when accidently re-adding him.
        manager.spend("test", 20);
        manager.tryNewBuyer("test");
        assertThat(manager.getCurrentFunds()).containsEntry("test", 30D);
        assertThat(manager.hasEnoughFunds("test", 40)).isFalse();
        assertThat(manager.hasEnoughFunds("test", 10)).isTrue();

        expectedException.expect(NotEnoughFundsException.class);
        expectedException.expectMessage(new StringContains("but doesn't have sufficient funds"));
        manager.spend("test", 150);
    }

    @Test
    public void adding_funds_should_work() {
        manager.tryNewBuyer("test");

        manager.addFunds("test", 550D);
        assertThat(manager.getCurrentFunds()).containsEntry("test", 600D);
    }
}
