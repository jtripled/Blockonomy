package com.jtripled.mineconomy.payday;

import java.io.IOException;
import java.math.BigDecimal;
import org.spongepowered.api.entity.living.player.Player;

/**
 *
 * @author jtripled
 */
public interface PaydayService
{
    public boolean decrementCooldown(Player player);
    
    public void setJoinBonus(BigDecimal joinBonus) throws IOException;
    
    public void setInterval(int minutes) throws IOException;
    
    public void setPaycheck(BigDecimal amount) throws IOException;
    
    public int getCooldown(Player player);
    
    public BigDecimal getJoinBonus();
    
    public int getInterval();
    
    public BigDecimal getPaycheck();
}
