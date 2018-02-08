package com.jtripled.mineconomy.lottery;

import com.jtripled.mineconomy.lottery.service.LotteryService;
import com.jtripled.mineconomy.lottery.service.LotteryServiceProvider;
import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.lottery.commands.LotteryCommand;
import java.io.IOException;
import org.spongepowered.api.Sponge;

/**
 *
 * @author jtripled
 */
public class LotteryModule
{
    private final LotteryService lotteryService;
    
    public LotteryModule() throws IOException
    {
        /* Initialize lottery service. */
        this.lotteryService = new LotteryServiceProvider();
        
        /* Register lottery service. */
        Sponge.getServiceManager().setProvider(Mineconomy.INSTANCE, LotteryService.class, this.lotteryService);
        
        /* Register lottery events. */
        Sponge.getEventManager().registerListeners(Mineconomy.INSTANCE, this.lotteryService);
        
        /* Register lottery commands. */
        Sponge.getCommandManager().register(Mineconomy.INSTANCE, LotteryCommand.SPEC, "lottery", "lotto");
    }
}
