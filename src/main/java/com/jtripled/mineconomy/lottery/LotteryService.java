package com.jtripled.mineconomy.lottery;

import java.io.IOException;
import java.math.BigDecimal;
import org.spongepowered.api.entity.living.player.Player;

/**
 *
 * @author jtripled
 */
public interface LotteryService
{
    public boolean decrementCooldown();
    
    public boolean isLotteryRunning();
    
    public int minutesRemaining();
    
    public boolean shouldLotteryStart();
    
    public void startLottery(LotteryPrizeSet prize);
    
    public void endLottery();
    
    public int getPlayerTicketCount(Player player);
    
    public int getTotalTicketCount();
    
    public LotteryPrizeSet getPrize();
    
    public boolean buyTickets(Player player, int quantity);
    
    public void setFrequency(int minutes) throws IOException;
    
    public void setDuration(int minutes) throws IOException;
    
    public void setChance(double chance) throws IOException;
    
    public void setCost(BigDecimal cost) throws IOException;
    
    public int getFrequency();
    
    public int getDuration();
    
    public double getChance();
    
    public BigDecimal getCost();
}
