package com.jtripled.mineconomy.payday.task;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.payday.PaydayText;
import com.jtripled.mineconomy.payday.service.PaydayService;
import com.jtripled.sponge.util.TextUtil;
import java.math.BigDecimal;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

/**
 *
 * @author jtripled
 */
public class PaydayJoinTask implements Runnable
{
    private final Player player;
    
    public PaydayJoinTask(Player player)
    {
        this.player = player;
    }
    
    @Override
    public void run()
    {
        Optional<ProviderRegistration<PaydayService>> opPayday
                = Sponge.getServiceManager().getRegistration(PaydayService.class);
        
        /* Could not find payday service. */
        if (!opPayday.isPresent())
        {
            Sponge.getServer().getConsole().sendMessage(TextUtil.serviceNotFound("PaydayService"));
            return;
        }
        
        Optional<ProviderRegistration<EconomyService>> opEconomy
                = Sponge.getServiceManager().getRegistration(EconomyService.class);
        
        /* Could not find economy service. */
        if (!opEconomy.isPresent())
        {
            Sponge.getServer().getConsole().sendMessage(TextUtil.serviceNotFound("EconomyService"));
        }
        
        PaydayService payday = opPayday.get().getProvider();
        EconomyService economy = opEconomy.get().getProvider();
        
        BigDecimal joinBonus = payday.getJoinBonus();
        
        /* Join bonus is disabled. */
        if (joinBonus.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        
        Optional<UniqueAccount> opAccount = economy.getOrCreateAccount(player.getUniqueId());
        
        /* Could not find player's account. */
        if (!opAccount.isPresent())
        {
            Sponge.getServer().getConsole().sendMessage(TextUtil.playerAccountNotFound(player));
            return;
        }
        
        UniqueAccount account = opAccount.get();
        
        /* Award join bonus. */
        account.deposit(economy.getDefaultCurrency(), joinBonus, Cause.of(EventContext.empty(), Mineconomy.INSTANCE));
        player.sendMessage(PaydayText.joinBonusText(joinBonus, economy));
    
    }
}
