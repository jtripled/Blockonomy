package com.jtripled.mineconomy.payday;

import com.jtripled.mineconomy.payday.service.PaydayServiceProvider;
import com.jtripled.mineconomy.payday.service.PaydayService;
import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.payday.commands.PaydayCommand;
import com.jtripled.mineconomy.payday.task.PaydayCheckTask;
import com.jtripled.mineconomy.payday.task.PaydayJoinTask;
import io.github.nucleuspowered.nucleus.api.events.NucleusFirstJoinEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;

/**
 *
 * @author jtripled
 */
public class PaydayModule
{
    private final PaydayService paydayService;
    
    public PaydayModule() throws IOException
    {
        /* Initialize our payday services. */
        this.paydayService = new PaydayServiceProvider();
        
        /* Register payday service. */
        Sponge.getServiceManager().setProvider(Mineconomy.INSTANCE, PaydayService.class, this.paydayService);
        
        /* Register payday service events. */
        Sponge.getEventManager().registerListeners(Mineconomy.INSTANCE, this.paydayService);
        
        /* Register payday commands. */
        Sponge.getCommandManager().register(Mineconomy.INSTANCE, PaydayCommand.SPEC, "payday");
        
        /* Add check task. */
        Sponge.getScheduler().createTaskBuilder()
                .execute(new PaydayCheckTask())
                .interval(1, TimeUnit.MINUTES)
                .name("Payday Check")
                .submit(Mineconomy.INSTANCE);
    }
    
    /* Give join bonus if enabled. */
    @Listener
    public void onPlayerFirstJoin(NucleusFirstJoinEvent event)
    {
        /* Add join bonus task. */
        Sponge.getScheduler().createTaskBuilder()
                .execute(new PaydayJoinTask(event.getTargetEntity()))
                .name("Payday Join: " + event.getTargetEntity().getName())
                .submit(Mineconomy.INSTANCE);
    }
}
