package com.jtripled.mineconomy.lottery.task;

import com.jtripled.mineconomy.lottery.Lottery;
import com.jtripled.mineconomy.lottery.LotteryText;
import com.jtripled.mineconomy.lottery.service.LotteryService;
import com.jtripled.sponge.util.TextUtil;
import io.github.nucleuspowered.nucleus.api.service.NucleusAFKService;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.text.Text;

/**
 *
 * @author jtripled
 */
public class LotteryWaitTask implements Runnable
{
    private int waitTime;
    private Lottery lottery;
    
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
        
        if (this.lottery != null)
        {
            if (this.lottery.decrementMinutesRemaining())
            {
                lotteryService.endLottery();
                this.waitTime = lotteryService.getFrequency();
                this.lottery = null;
            }
            else
            {
                Text msg = LotteryText.countdownText(this.lottery.getMinutesRemaining());
                Sponge.getServer().getOnlinePlayers().forEach((Player player) -> {
                    player.sendMessage(msg);
                });
            }
        }
        else
        {
            if (--this.waitTime > 0)
            {
                return;
            }
            
            if (lotteryService.isLotteryRunning())
            {
                this.lottery = lotteryService.getLottery();
                this.setWaitTime(this.lottery.getMinutesRemaining()
                        + lotteryService.getFrequency());
                return;
            }

            Optional<ProviderRegistration<NucleusAFKService>> opAFK
                    = Sponge.getServiceManager().getRegistration(NucleusAFKService.class);

            int playerCount = 0;
            for (Player player : Sponge.getServer().getOnlinePlayers())
                if (player.isOnline() && (!opAFK.isPresent() || !opAFK.get().getProvider().isAFK(player)))
                    playerCount += 1;

            /* If not enough active players, wait. */
            if (playerCount < 2)
            {
                this.setWaitTime(1);
                return;
            }

            this.lottery = new Lottery(lotteryService.getDuration(), lotteryService.getRandomPrize());

            this.waitTime = this.lottery.getMinutesRemaining()
                    + lotteryService.getFrequency();
            lotteryService.startLottery(this.lottery);
        }
    }
    
    public int getWaitTime()
    {
        return this.waitTime;
    }
    
    public void setWaitTime(int duration)
    {
        this.waitTime = duration;
    }
    
    public void setLottery(Lottery lottery)
    {
        this.lottery = lottery;
    }
}
