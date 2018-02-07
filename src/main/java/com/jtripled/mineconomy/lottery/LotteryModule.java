package com.jtripled.mineconomy.lottery;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.lottery.commands.LotteryCommand;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

/**
 *
 * @author jtripled
 */
public class LotteryModule
{
    private final LotteryService lotteryService;
    private final Task lotteryTask;
    
    public LotteryModule() throws IOException
    {
        this.lotteryService = new LotteryServiceProvider();
        Sponge.getServiceManager().setProvider(Mineconomy.INSTANCE, LotteryService.class, this.lotteryService);
        Sponge.getEventManager().registerListeners(Mineconomy.INSTANCE, this.lotteryService);
        Sponge.getCommandManager().register(Mineconomy.INSTANCE, LotteryCommand.SPEC, "lottery", "lotto");
        
        /* Add Lottery task. */
        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        this.lotteryTask = taskBuilder.execute((Task t1) -> {
            if (this.lotteryService.decrementCooldown() && this.lotteryService.shouldLotteryStart())
                this.lotteryService.startLottery(null);
        }).interval(1, TimeUnit.MINUTES).name("Lottery - Check").submit(Mineconomy.INSTANCE);
    }
}
