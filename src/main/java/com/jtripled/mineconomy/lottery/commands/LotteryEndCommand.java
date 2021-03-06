package com.jtripled.mineconomy.lottery.commands;

import com.jtripled.mineconomy.lottery.LotteryText;
import com.jtripled.mineconomy.lottery.service.LotteryService;
import com.jtripled.sponge.util.TextUtil;
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
public class LotteryEndCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Immediately end a lottery."))
        .permission("mineconomy.lottery.admin")
        .executor(new LotteryEndCommand())
        .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("quantity"))))
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
        
        LotteryService lotterySrv = opLottery.get().getProvider();
        
        if (!lotterySrv.isLotteryRunning())
        {
            src.sendMessage(LotteryText.noRunningLotteryText());
            return CommandResult.empty();
        }
        
        src.sendMessage(LotteryText.endLotteryCmdText());
        lotterySrv.endLottery();
        return CommandResult.success();
    }
}
