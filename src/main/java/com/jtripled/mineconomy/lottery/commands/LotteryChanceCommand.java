package com.jtripled.mineconomy.lottery.commands;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.lottery.service.LotteryService;
import com.jtripled.mineconomy.lottery.LotteryText;
import com.jtripled.sponge.util.TextUtil;
import java.io.IOException;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.text.Text;

/**
 *
 * @author jtripled
 */
public class LotteryChanceCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Set the lottery chance."))
        .permission("mineconomy.admin")
        .executor(new LotteryChanceCommand())
        .arguments(GenericArguments.doubleNum(Text.of("chance")))
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
        
        LotteryService lottery = opLottery.get().getProvider();
        
        double chance = (Double) args.getOne("chance").get();
        if (chance <= 0.00d || chance > 1.00d)
        {
            src.sendMessage(LotteryText.invalidChanceText());
            return CommandResult.empty();
        }
        
        try
        {
            src.sendMessage(LotteryText.setChanceText(chance));
            lottery.setChance(chance);
            return CommandResult.success();
        }
        catch (IOException ex)
        {
            src.sendMessage(LotteryText.setChanceErrorText());
            Mineconomy.getLogger().error(null, ex);
            return CommandResult.empty();
        }
    }
}
