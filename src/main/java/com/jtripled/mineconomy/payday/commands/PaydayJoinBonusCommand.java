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
public class PaydayJoinBonusCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Set the payday first-time join bonus."))
        .permission("mineconomy.payday.admin")
        .executor(new PaydayJoinBonusCommand())
        .arguments(GenericArguments.doubleNum(Text.of("amount")))
        .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        BigDecimal bonus = BigDecimal.valueOf((Double) args.getOne("amount").get());
        
        if (bonus.compareTo(BigDecimal.ZERO) < 0)
        {
            src.sendMessage(PaydayText.invalidJoinBonusText());
            return CommandResult.empty();
        }
        
        try
        {
            Mineconomy.getPayday().setJoinBonus(bonus);
            src.sendMessage(PaydayText.setJoinBonusText(bonus));
            return CommandResult.success();
        }
        catch (IOException ex)
        {
            if (src instanceof Player)
                src.sendMessage(PaydayText.setJoinBonusErrorText());
            Mineconomy.getLogger().error(null, ex);
            return CommandResult.empty();
        }
    }
}
