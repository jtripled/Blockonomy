package com.jtripled.mineconomy.lottery;

import com.jtripled.mineconomy.Mineconomy;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

/**
 *
 * @author jtripled
 */
public class Lottery
{
    private static int CURRENT_ID = 0;
    
    private final int id;
    private final LotteryPrize prize;
    private final Map<UUID, Integer> tickets;
    
    private int ticketCount;
    private int minutesRemaining;
    
    public Lottery(int duration, LotteryPrize prize)
    {
        this.id = CURRENT_ID++;
        this.prize = prize;
        this.tickets = new HashMap<>();
        
        this.ticketCount = 0;
        this.minutesRemaining = duration;
    }
    
    public int getID()
    {
        return this.id;
    }
    
    public BigDecimal getMoney()
    {
        return this.prize.getMoney();
    }
    
    public List<ItemStack> getItems()
    {
        return this.prize.getItems();
    }
    
    public Map<UUID, Integer> getTickets()
    {
        return this.tickets;
    }
    
    public BigDecimal getTicketCost()
    {
        return this.prize.getCost();
    }
    
    public int getTotalTicketCount()
    {
        return this.ticketCount;
    }
    
    public int getPlayerTicketCount(Player player)
    {
        return player != null && this.tickets.containsKey(player.getUniqueId())
                ? this.tickets.get(player.getUniqueId())
                : 0;
    }
    
    public void addTickets(Player player, int count)
    {
        if (player != null && player.isOnline())
        {
            this.ticketCount += count;
            this.tickets.put(player.getUniqueId(), getPlayerTicketCount(player) + count);
        }
    }
    
    public void removeTickets(Player player)
    {
        if (player != null)
        {
            this.ticketCount -= getPlayerTicketCount(player);
            this.tickets.remove(player.getUniqueId());
        }
    }
    
    public int getMinutesRemaining()
    {
        return this.minutesRemaining;
    }
    
    public boolean decrementMinutesRemaining()
    {
        this.minutesRemaining -= 1;
        return this.minutesRemaining <= 0;
    }
    
    public boolean buyTickets(Player player, int count)
    {
        Optional<ProviderRegistration<EconomyService>> opEconomy = Sponge.getServiceManager().getRegistration(EconomyService.class);
        
        /* Could not find economy service. */
        if (!opEconomy.isPresent())
        {
            return false;
        }
        
        EconomyService economy = opEconomy.get().getProvider();
        
        Optional<UniqueAccount> opAccount = economy.getOrCreateAccount(player.getUniqueId());

        /* Could not find player's account. */
        if (!opAccount.isPresent())
        {
            return false;
        }

        UniqueAccount account = opAccount.get();
        
        BigDecimal totalCost = getTicketCost().multiply(BigDecimal.valueOf(count));
        if (account.getBalance(economy.getDefaultCurrency()).compareTo(totalCost) < 0)
        {
            return false;
        }
        
        account.withdraw(economy.getDefaultCurrency(), totalCost, Cause.of(EventContext.empty(), Mineconomy.INSTANCE));
        
        this.addTickets(player, count);
        
        return true;
    }
}
