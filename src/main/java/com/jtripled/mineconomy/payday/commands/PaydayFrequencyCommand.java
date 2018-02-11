package com.jtripled.mineconomy.payday.commands;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.payday.PaydayText;
import java.io.IOException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.entity.living.player.Player;

/**
 *
 * @author jtripled
 */
public class PaydayFrequencyCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Set the payday frequency in minutes."))
        .permission("mineconomy.payday.admin")
        .executor(new PaydayFrequencyCommand())
        .arguments(GenericArguments.integer(Text.of("minutes")))
        .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        int minutes = (int) args.getOne("minutes").get();
        
        if (minutes < 1)
        {
            src.sendMessage(PaydayText.invalidFrequencyText());
            return CommandResult.empty();
        }
        
        try
        {
            Mineconomy.getPayday().setFrequency(minutes);
            src.sendMessage(PaydayText.setFrequencyText(minutes));
            return CommandResult.success();
        }
        catch (IOException ex)
        {
            if (src instanceof Player)
                src.sendMessage(PaydayText.setFrequencyErrorText());
            Mineconomy.getLogger().error(null, ex);
            return CommandResult.empty();
        }
    }
}
