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
public class FrequencyCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Set the lottery frequency."))
        .permission("mineconomy.admin")
        .executor(new FrequencyCommand())
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
            src.sendMessage(LotteryText.invalidFrequencyText());
            return CommandResult.empty();
        }
        
        try
        {
            src.sendMessage(LotteryText.setFrequencyText(minutes));
            lottery.setFrequency(minutes);
            return CommandResult.success();
        }
        catch (IOException ex)
        {
            src.sendMessage(LotteryText.setFrequencyErrorText());
            Mineconomy.getLogger().error(null, ex);
            return CommandResult.empty();
        }
    }
}
