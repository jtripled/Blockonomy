package com.jtripled.mineconomy.lottery.commands;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.lottery.LotteryService;
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
public class DurationCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Set the lottery duration."))
        .permission("mineconomy.admin")
        .executor(new DurationCommand())
        .arguments(GenericArguments.integer(Text.of("minutes")))
        .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        Optional<ProviderRegistration<LotteryService>> opLottery = Sponge.getServiceManager().getRegistration(LotteryService.class);
        
        /* Could not find lottery service. */
        if (!opLottery.isPresent())
        {
            src.sendMessage(TextUtil.serviceNotFound("LotteryService"));
            return CommandResult.empty();
        }
        
        LotteryService lottery = opLottery.get().getProvider();
        
        int minutes = (Integer) args.getOne("minutes").get();
        if (minutes < 1)
        {
            src.sendMessage(LotteryText.invalidDurationText());
            return CommandResult.empty();
        }
        
        try
        {
            src.sendMessage(LotteryText.setDurationText(minutes));
            lottery.setDuration(minutes);
            return CommandResult.success();
        }
        catch (IOException ex)
        {
            src.sendMessage(LotteryText.setDurationErrorText());
            Mineconomy.getLogger().error(null, ex);
            return CommandResult.empty();
        }
    }
}
