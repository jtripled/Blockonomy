package com.jtripled.mineconomy.lottery;

import com.jtripled.mineconomy.Mineconomy;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 *
 * @author jtripled
 */
public class LotteryPrizeSet
{
    private Text text;
    private final BigDecimal credits;
    private final ItemStack[] items;
    
    public LotteryPrizeSet(BigDecimal credits, ItemStack[] items)
    {
        this.credits = credits;
        this.items = items;
        this.text = null;
        
        Optional<ProviderRegistration<EconomyService>> opEconomy = Sponge.getServiceManager().getRegistration(EconomyService.class);
        
        /* Could not find economy service. */
        if (opEconomy.isPresent())
        {
            EconomyService economy = opEconomy.get().getProvider();
            if (this.credits.compareTo(BigDecimal.ZERO) > 0)
            {
                DecimalFormat format = new DecimalFormat("#0.00");
                this.text = Text.of(format.format(this.credits), " ",
                    this.credits.compareTo(BigDecimal.ONE) != 0 ? economy.getDefaultCurrency().getPluralDisplayName().toPlain() : economy.getDefaultCurrency().getDisplayName().toPlain());
            }
        }
        
        if (items != null)
        {
            for (ItemStack item : this.items)
            {
                
            }
        }
    }
    
    public void award(Player player, BigDecimal bonus)
    {
        Optional<ProviderRegistration<EconomyService>> opEconomy = Sponge.getServiceManager().getRegistration(EconomyService.class);
        
        if (!opEconomy.isPresent())
        {
            return;
        }
        
        EconomyService economy = opEconomy.get().getProvider();
        Optional<UniqueAccount> opAccount = economy.getOrCreateAccount(player.getUniqueId());
        if (opAccount.isPresent())
        {
            UniqueAccount account = opAccount.get();
            account.deposit(economy.getDefaultCurrency(), bonus.add(this.credits), Cause.of(EventContext.empty(), Mineconomy.INSTANCE));
        }
        
        if (this.items != null)
        {
            for (ItemStack item : this.items)
            {
                player.getInventory().offer(item);
            }
        }
        
        Text out = Text.join(Text.of(TextColors.GREEN, "You've won ", TextColors.YELLOW, getText()));
        if (bonus.compareTo(BigDecimal.ZERO) > 0)
        {
            DecimalFormat format = new DecimalFormat("#0.00");
            out = Text.join(out, Text.of(TextColors.GREEN, " and a bonus of ", TextColors.YELLOW, format.format(bonus), " ",
                bonus.compareTo(BigDecimal.ONE) != 0 ? economy.getDefaultCurrency().getPluralDisplayName().toPlain() : economy.getDefaultCurrency().getDisplayName().toPlain(),
                TextColors.GREEN, Text.of(".")));
        }
        player.sendMessage(out);
    }
    
    public BigDecimal getMoney()
    {
        return credits;
    }
    
    public Text getText()
    {
        return text;
    }
}
