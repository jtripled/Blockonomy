package com.jtripled.mineconomy.lottery.commands;

import com.jtripled.mineconomy.lottery.service.LotteryService;
import com.jtripled.mineconomy.lottery.LotteryText;
import com.jtripled.sponge.util.TextUtil;
import java.text.DecimalFormat;
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
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 *
 * @author jtripled
 */
public class LotteryInfoCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Set the lottery chance."))
        .permission("mineconomy.admin")
        .executor(new LotteryInfoCommand())
        .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        Optional<ProviderRegistration<LotteryService>> opLottery
                = Sponge.getServiceManager().getRegistration(LotteryService.class);
        
        /* Could not find payday service. */
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
        
        LotteryService lottery = opLottery.get().getProvider();
        EconomyService economy = opEconomy.get().getProvider();
        
        /* Create pagination contents. */
        List<Text> contents = new ArrayList<>();
        contents.add(LotteryText.infoFrequencyText(lottery.getFrequency()));
        contents.add(LotteryText.infoDurationText(lottery.getDuration()));
        contents.add(LotteryText.infoChanceText(lottery.getChance()));
        contents.add(LotteryText.infoCostText(lottery.getCost(), economy));
        
        /* Send contents to command sender. */
        PaginationList.builder()
                .title(Text.of(TextColors.GREEN, "Lottery Info"))
                .padding(Text.of(TextColors.GREEN, "="))
                .contents(contents)
                .sendTo(src);
        
        return CommandResult.success();
    }
}
