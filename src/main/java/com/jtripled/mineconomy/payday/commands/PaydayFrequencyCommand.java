package com.jtripled.mineconomy.payday.commands;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.payday.PaydayText;
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
import com.jtripled.mineconomy.payday.service.PaydayService;
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
        Optional<ProviderRegistration<PaydayService>> opPayday
                = Sponge.getServiceManager().getRegistration(PaydayService.class);
        
        /* Could not find payday service. */
        if (!opPayday.isPresent())
        {
            src.sendMessage(TextUtil.serviceNotFound("PaydayService"));
            return CommandResult.empty();
        }
        
        PaydayService payday = opPayday.get().getProvider();
        
        int minutes = (int) args.getOne("minutes").get();
        
        /* Can't set frequency below 1 minute. */
        if (minutes < 1)
        {
            src.sendMessage(PaydayText.invalidFrequencyText());
            return CommandResult.empty();
        }
        
        try
        {
            payday.setFrequency(minutes);
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
