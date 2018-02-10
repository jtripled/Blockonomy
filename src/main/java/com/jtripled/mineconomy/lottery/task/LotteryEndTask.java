package com.jtripled.mineconomy.lottery.task;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.lottery.Lottery;
import com.jtripled.mineconomy.lottery.LotteryText;
import com.jtripled.mineconomy.lottery.service.LotteryService;
import com.jtripled.sponge.util.TextUtil;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 *
 * @author jtripled
 */
public class LotteryEndTask implements Runnable
{
    private final Lottery lottery;
    
    public LotteryEndTask(Lottery lottery)
    {
        this.lottery = lottery;
    }
    
    @Override
    public void run()
    {
        Optional<ProviderRegistration<LotteryService>> opLottery
                = Sponge.getServiceManager().getRegistration(LotteryService.class);
        
        /* Could not find lottery service. */
        if (!opLottery.isPresent())
        {
            Sponge.getServer().getConsole().sendMessage(TextUtil.serviceNotFound("LotteryService"));
            return;
        }
        
        Optional<ProviderRegistration<EconomyService>> opEconomy
                = Sponge.getServiceManager().getRegistration(EconomyService.class);
        
        /* Could not find economy service. */
        if (!opEconomy.isPresent())
        {
            Sponge.getServer().getConsole().sendMessage(TextUtil.serviceNotFound("EconomyService"));
            return;
        }
        
        LotteryService lotterySrv = opLottery.get().getProvider();
        EconomyService economySrv = opEconomy.get().getProvider();
        
        lotterySrv.waitLottery(lotterySrv.getFrequency());
        
        Player winner = this.getWinner();
        
        if (winner != null)
        {
            Text msg = LotteryText.winnerText(winner);
            Sponge.getServer().getOnlinePlayers().forEach((Player player) -> {
                player.sendMessage(msg);
            });
            
            BigDecimal bonus = this.getBonus(winner);
            
            winner.sendMessage(Text.of(TextColors.GREEN, "You've won ",
                    TextColors.YELLOW, LotteryText.prizeText(this.lottery, economySrv),
                    TextColors.GREEN, " with a bonus of ",
                    TextColors.YELLOW, TextUtil.money(bonus, economySrv),
                    TextColors.GREEN, "!"));
            this.award(winner, bonus);
        }
        else
        {
            Text msg = LotteryText.noWinnerText();
            Sponge.getServer().getOnlinePlayers().forEach((Player player) -> {
                player.sendMessage(msg);
            });
            this.refund();
        }
    }
    
    private BigDecimal getBonus(Player winner)
    {
        int winnerCount = this.lottery.getPlayerTicketCount(winner);
        int nextCount = 0;
        
        for (Integer count : this.lottery.getTickets().values())
        {
            if (count < winnerCount && count > nextCount)
            {
                nextCount = count;
            }
        }
        
        return BigDecimal.valueOf(nextCount).multiply(this.lottery.getTicketCost());
    }
    
    private Player getWinner()
    {
        int ticketCount = this.lottery.getTotalTicketCount();
        Map<UUID, Integer> tickets = this.lottery.getTickets();
        
        Player winner = null;
        
        if (ticketCount > 0 && tickets.keySet().size() > 1)
        {
            do
            {
                int random = new Random().nextInt(ticketCount) + 1;
                for (UUID player : tickets.keySet())
                {
                    int t = tickets.get(player);
                    random -= t;
                    if (random <= 0)
                    {
                        Optional<Player> opPlayer = Sponge.getServer().getPlayer(player);
                        if (opPlayer.isPresent())
                            winner = opPlayer.get();
                        break;
                    }
                }
                if (winner == null) break;
                if (!winner.isOnline())
                {
                    ticketCount -= tickets.get(winner.getUniqueId());
                    tickets.remove(winner.getUniqueId());
                }
            } while (!winner.isOnline());
        }
        
        return winner;
    }
    
    private void award(Player player, BigDecimal bonus)
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
            account.deposit(economy.getDefaultCurrency(), bonus.add(this.lottery.getMoney()), Cause.of(EventContext.empty(), Mineconomy.INSTANCE));
        }
        
        if (this.lottery.getItems() != null)
        {
            this.lottery.getItems().forEach((item) -> {
                player.getInventory().offer(item);
            });
        }
    }
    
    private void refund()
    {
        
        Optional<ProviderRegistration<EconomyService>> opEconomy
                = Sponge.getServiceManager().getRegistration(EconomyService.class);
        
        /* Could not find economy service. */
        if (!opEconomy.isPresent())
        {
            Sponge.getServer().getConsole().sendMessage(TextUtil.serviceNotFound("EconomyService"));
            return;
        }
        
        EconomyService economySrv = opEconomy.get().getProvider();
        
        Sponge.getServer().getOnlinePlayers().forEach((Player player) -> {
            if (this.lottery.getPlayerTicketCount(player) > 0)
            {
                BigDecimal refund =
                        BigDecimal.valueOf(this.lottery.getPlayerTicketCount(player))
                        .multiply(this.lottery.getTicketCost());

                Optional<UniqueAccount> opAccount = economySrv.getOrCreateAccount(player.getUniqueId());

                /* Could not find player's account. */
                if (opAccount.isPresent())
                {
                    UniqueAccount account = opAccount.get();
                    account.deposit(economySrv.getDefaultCurrency(),
                            refund, Cause.of(EventContext.empty(), Mineconomy.INSTANCE));
                    player.sendMessage(LotteryText.refundText(refund, economySrv));
                }
            }
        });
    }
}
