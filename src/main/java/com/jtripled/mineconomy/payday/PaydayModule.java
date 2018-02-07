package com.jtripled.mineconomy.payday;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.payday.commands.PaydayCommand;
import io.github.nucleuspowered.nucleus.api.events.NucleusFirstJoinEvent;
import io.github.nucleuspowered.nucleus.api.service.NucleusAFKService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

/**
 *
 * @author jtripled
 */
public class PaydayModule
{
    private final PaydayService paydayService;
    private final Task paydayTask;
    
    private NucleusAFKService afkService;
    
    public PaydayModule() throws IOException
    {
        this.paydayService = new PaydayServiceProvider();
        Sponge.getServiceManager().setProvider(Mineconomy.INSTANCE, PaydayService.class, this.paydayService);
        Sponge.getEventManager().registerListeners(Mineconomy.INSTANCE, this.paydayService);
        Sponge.getCommandManager().register(Mineconomy.INSTANCE, PaydayCommand.SPEC, "payday");
        
        /* Add Payday task. */
        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        paydayTask = taskBuilder.execute((Task t1) -> {
            BigDecimal paycheck = this.paydayService.getAmount();
            if (paycheck.compareTo(BigDecimal.ZERO) > 0)
            {
                Optional<ProviderRegistration<EconomyService>> opEconomy = Sponge.getServiceManager().getRegistration(EconomyService.class);

                /* Could not find economy service. */
                if (!opEconomy.isPresent())
                {
                    return;
                }

                EconomyService economy = opEconomy.get().getProvider();
                
                Sponge.getServer().getOnlinePlayers().forEach((Player player) -> {
                    if ((this.afkService == null || !this.afkService.isAFK(player))
                        && this.paydayService.decrementCooldown(player))
                    {
                        Optional<UniqueAccount> opAccount = economy.getOrCreateAccount(player.getUniqueId());

                        /* Could not find player's account. */
                        if (!opAccount.isPresent())
                        {
                            return;
                        }

                        UniqueAccount account = opAccount.get();
        
                        /* Award paycheck. */
                        account.deposit(economy.getDefaultCurrency(), paycheck, Cause.of(EventContext.empty(), Mineconomy.INSTANCE));
                        player.sendMessage(PaydayText.paydayText(paycheck, economy.getDefaultCurrency().getDisplayName().toPlain(), economy.getDefaultCurrency().getPluralDisplayName().toPlain()));
                        
                        /* Reset cooldown. */
                        this.paydayService.resetCooldown(player);
                    }
                });
            }
        }).interval(1, TimeUnit.MINUTES).name("Payday - Check").submit(Mineconomy.INSTANCE);
    }
    
    /* Update service providers. */
    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event)
    {
        if (event.getService() == NucleusAFKService.class)
        {
            afkService = (NucleusAFKService) event.getNewProvider();
        }
    }
    
    /* Give join bonus if enabled. */
    @Listener
    public void onPlayerFirstJoin(NucleusFirstJoinEvent event)
    {
        Player player = event.getTargetEntity();
        
        BigDecimal joinBonus = paydayService.getJoinBonus();
        
        /* Join bonus is disabled. */
        if (joinBonus.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        
        Optional<ProviderRegistration<EconomyService>> opEconomy = Sponge.getServiceManager().getRegistration(EconomyService.class);
        
        /* Could not find economy service. */
        if (!opEconomy.isPresent())
        {
            return;
        }
        
        EconomyService economy = opEconomy.get().getProvider();
        
        Optional<UniqueAccount> opAccount = economy.getOrCreateAccount(player.getUniqueId());
        
        /* Could not find player's account. */
        if (!opAccount.isPresent())
        {
            return;
        }
        
        UniqueAccount account = opAccount.get();
        
        /* Award join bonus. */
        account.deposit(economy.getDefaultCurrency(), joinBonus, Cause.of(EventContext.empty(), Mineconomy.INSTANCE));
        player.sendMessage(PaydayText.joinBonusText(joinBonus, economy.getDefaultCurrency().getDisplayName().toPlain(), economy.getDefaultCurrency().getPluralDisplayName().toPlain()));
    }
}
