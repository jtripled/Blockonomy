package com.jtripled.mineconomy.lottery.service;

import com.jtripled.mineconomy.lottery.Lottery;
import com.jtripled.mineconomy.lottery.LotteryPrize;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 *
 * @author jtripled
 */
public interface LotteryService
{
    public boolean isLotteryRunning();
    
    public Lottery getLottery();
    
    public void waitLottery(int duration);
    
    public void startLottery(Lottery lottery);
    
    public void endLottery();
    
    public void setFrequency(int minutes) throws IOException;
    
    public void setDuration(int minutes) throws IOException;
    
    public int getFrequency();
    
    public int getDuration();
    
    public void createPrize(String name, int weight, BigDecimal ticketCost, BigDecimal money, List<ItemStack> items) throws IOException;
    
    public void deletePrize(String name) throws IOException;
    
    public LotteryPrize getRandomPrize();
    
    public LotteryPrize getNamedPrize(String name);
    
    public Map<String, LotteryPrize> getPrizes();
}
