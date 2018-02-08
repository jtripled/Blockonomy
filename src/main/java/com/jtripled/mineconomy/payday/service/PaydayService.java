package com.jtripled.mineconomy.payday.service;

import java.io.IOException;
import java.math.BigDecimal;
import org.spongepowered.api.entity.living.player.Player;

/**
 *
 * @author jtripled
 */
public interface PaydayService
{
    public void setFrequency(int minutes) throws IOException;
    
    public void setAmount(BigDecimal amount) throws IOException;
    
    public void setJoinBonus(BigDecimal joinBonus) throws IOException;
    
    public int getFrequency();
    
    public BigDecimal getAmount();
    
    public BigDecimal getJoinBonus();
    
    public boolean decrementCooldown(Player player);
    
    public int getCooldown(Player player);
    
    public void resetCooldown(Player player);
    
    public boolean isAFK(Player player);
}
