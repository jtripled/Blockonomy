package com.jtripled.mineconomy.payday;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.sponge.util.TextUtil;
import java.math.BigDecimal;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

/**
 *
 * @author jtripled
 */
public class PaydayTask implements Runnable
{
    @Override
    public void run()
    {
        PaydayModule payday = Mineconomy.getPayday();
        EconomyService economy = Mineconomy.getEconomy();

        if (payday.getAmount().compareTo(BigDecimal.ZERO) > 0)
        {
            Sponge.getServer().getOnlinePlayers().forEach((Player player) -> {
                if (!payday.isAFK(player) && payday.decrementCooldown(player))
                {
                    Optional<UniqueAccount> opAccount = economy.getOrCreateAccount(player.getUniqueId());

                    /* Could not find player's account. */
                    if (!opAccount.isPresent())
                    {
                        Sponge.getServer().getConsole().sendMessage(TextUtil.playerAccountNotFound(player));
                        return;
                    }

                    UniqueAccount account = opAccount.get();

                    /* Award paycheck. */
                    account.deposit(
                            economy.getDefaultCurrency(),
                            payday.getAmount(),
                            Cause.of(EventContext.empty(), Mineconomy.INSTANCE)
                    );
                    player.sendMessage(PaydayText.paydayText(payday.getAmount()));

                    /* Reset cooldown. */
                    payday.resetCooldown(player);
                }
            });
        }
    }
}
