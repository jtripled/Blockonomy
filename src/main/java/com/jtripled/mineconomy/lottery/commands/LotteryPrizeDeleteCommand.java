package com.jtripled.mineconomy.lottery.commands;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.lottery.LotteryText;
import com.jtripled.mineconomy.lottery.service.LotteryService;
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
public class LotteryPrizeDeleteCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Delete a prize set."))
        .permission("mineconomy.lottery.admin")
        .executor(new LotteryPrizeDeleteCommand())
        .arguments(GenericArguments.string(Text.of("name")))
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
        
        String name = (String) args.getOne("name").get();
        
        if (lotterySrv.getNamedPrize(name) == null)
        {
            src.sendMessage(LotteryText.prizeNotExists(name));
            return CommandResult.empty();
        }
        
        try
        {
            lotterySrv.deletePrize(name);
            src.sendMessage(LotteryText.prizeDeleted(name));
            return CommandResult.success();
        }
        catch (IOException ex)
        {
            Mineconomy.getLogger().error("Could not delete prize set: " + name + ".", ex);
            return CommandResult.empty();
        }
    }
}
