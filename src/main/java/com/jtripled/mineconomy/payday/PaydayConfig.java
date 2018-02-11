package com.jtripled.mineconomy.payday;

import com.jtripled.blockonomy.module.ModuleConfig;
import java.math.BigDecimal;

/**
 *
 * @author jtripled
 */
public class PaydayConfig extends ModuleConfig
{
    private int frequency;
    private BigDecimal amount;
    private BigDecimal joinBonus;
    
    public PaydayConfig()
    {
        super("payday.conf");
    }
    
    public int getFrequency()
    {
        return this.frequency;
    }
    
    public BigDecimal getAmount()
    {
        return this.amount;
    }
    
    public BigDecimal getJoinBonus()
    {
        return this.joinBonus;
    }
}
