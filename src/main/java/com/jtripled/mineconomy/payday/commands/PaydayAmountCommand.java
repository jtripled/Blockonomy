package com.jtripled.mineconomy.payday.commands;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.payday.PaydayText;
import java.io.IOException;
import java.math.BigDecimal;
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
public class PaydayAmountCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Set the default payday amount."))
        .permission("mineconomy.payday.admin")
        .executor(new PaydayAmountCommand())
        .arguments(GenericArguments.doubleNum(Text.of("amount")))
        .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        BigDecimal amount = BigDecimal.valueOf((Double) args.getOne("amount").get());
        
        if (amount.compareTo(BigDecimal.ZERO) < 0)
        {
            src.sendMessage(PaydayText.invalidAmountText());
            return CommandResult.empty();
        }
        
        try
        {
            Mineconomy.getPayday().setAmount(amount);
            src.sendMessage(PaydayText.setAmountText(amount));
            return CommandResult.success();
        }
        catch (IOException ex)
        {
            if (src instanceof Player)
                src.sendMessage(PaydayText.setAmountErrorText());
            Mineconomy.getLogger().error(null, ex);
            return CommandResult.empty();
        }
    }
}
