package com.jtripled.mineconomy.lottery.task;

import com.jtripled.mineconomy.lottery.Lottery;
import com.jtripled.mineconomy.lottery.LotteryText;
import com.jtripled.mineconomy.lottery.service.LotteryService;
import com.jtripled.sponge.util.TextUtil;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.text.Text;

/**
 *
 * @author jtripled
 */
public class LotteryStartTask implements Runnable
{
    private final Lottery lottery;
    
    public LotteryStartTask(Lottery lottery)
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
        
        LotteryService lotteryService = opLottery.get().getProvider();
        
        lotteryService.waitLottery(this.lottery.getMinutesRemaining() + 1);
        
        Text msg = LotteryText.beginText();
        Sponge.getServer().getOnlinePlayers().forEach((Player player) -> {
            player.sendMessage(msg);
        });
    }
}
