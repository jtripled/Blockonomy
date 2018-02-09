package com.jtripled.mineconomy.lottery.commands;

import com.jtripled.mineconomy.lottery.Lottery;
import com.jtripled.mineconomy.lottery.service.LotteryService;
import com.jtripled.mineconomy.lottery.LotteryText;
import com.jtripled.sponge.util.TextUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 *
 * @author jtripled
 */
public class LotteryCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Display information about the current lottery."))
        .permission("mineconomy.lottery")
        .executor(new LotteryCommand())
        .child(LotteryBuyCommand.SPEC, "buy", "b")
        .child(LotteryDurationCommand.SPEC, "duration", "dur", "d")
        .child(LotteryFrequencyCommand.SPEC, "frequency", "frequency", "freq", "f")
        .child(LotteryInfoCommand.SPEC, "info", "i")
        .child(LotteryPrizeCommand.SPEC, "prize", "p")
        .child(LotteryEndCommand.SPEC, "end", "e")
        .child(LotteryStartCommand.SPEC, "start", "s")
        .build();
    
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        Optional<ProviderRegistration<LotteryService>> opLottery
                = Sponge.getServiceManager().getRegistration(LotteryService.class);
        
        /* Could not find lottery service. */
        if (!opLottery.isPresent())
        {
            src.sendMessage(TextUtil.serviceNotFound("LotteryService"));
            return CommandResult.empty();
        }
        
        Optional<ProviderRegistration<EconomyService>> opEconomy
                = Sponge.getServiceManager().getRegistration(EconomyService.class);
        
        /* Could not find economy service. */
        if (!opEconomy.isPresent())
        {
            src.sendMessage(TextUtil.serviceNotFound("EconomyService"));
            return CommandResult.empty();
        }
        
        LotteryService lotterySrv = opLottery.get().getProvider();
        EconomyService economySrv = opEconomy.get().getProvider();
        
        if (!lotterySrv.isLotteryRunning() || lotterySrv.getLottery() == null)
        {
            src.sendMessage(LotteryText.noRunningLotteryText());
            return CommandResult.success();
        }
        
        Lottery lottery = lotterySrv.getLottery();
        
        /* Create pagination contents. */
        List<Text> contents = new ArrayList<>();
        contents.add(LotteryText.lotteryTimeRemainingText(lottery.getMinutesRemaining()));
        contents.add(LotteryText.lotteryTicketCostText(lottery.getTicketCost(), economySrv));
        contents.add((src instanceof Player)
            ? LotteryText.lotteryCurrentTicketText(lottery.getPlayerTicketCount((Player) src))
            : LotteryText.lotteryTotalTicketText(lottery.getTotalTicketCount()));
        contents.add(LotteryText.lotteryPrizeText(lottery, economySrv));
        if (src instanceof Player)
        {
            contents.add(Text.EMPTY);
            contents.add(LotteryText.buyText(lottery.getTicketCost(), economySrv));
        }
        
        /* Send contents to command sender. */
        PaginationList.builder()
                .title(Text.of(TextColors.GREEN, "Lottery"))
                .padding(Text.of(TextColors.GREEN, "="))
                .contents(contents)
                .sendTo(src);
        
        return CommandResult.success();
    }
}
