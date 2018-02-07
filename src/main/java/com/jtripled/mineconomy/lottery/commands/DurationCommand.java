package com.jtripled.mineconomy.lottery.commands;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.lottery.LotteryService;
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
import org.spongepowered.api.text.format.TextColors;

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
        Optional<ProviderRegistration<LotteryService>> opService = Sponge.getServiceManager().getRegistration(LotteryService.class);
        
        /* Could not find payday service. */
        if (!opService.isPresent())
        {
            return CommandResult.empty();
        }
        
        LotteryService lottery = opService.get().getProvider();
        
        int minutes = (Integer) args.getOne("minutes").get();
        if (minutes < 1)
        {
            return CommandResult.empty();
        }
        
        try
        {
            lottery.setDuration(minutes);
            Text msg = Text.of(TextColors.GREEN, "You've set the lottery duration to ", TextColors.YELLOW, minutes, " minutes", TextColors.GREEN, ".");
            src.sendMessage(msg);
        }
        catch (IOException ex)
        {
            Mineconomy.getLogger().error(null, ex);
        }
        
        return CommandResult.success();
    }
}
