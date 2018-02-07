package com.jtripled.mineconomy.payday.commands;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.payday.PaydayService;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 *
 * @author jtripled
 */
public class JoinBonusCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Set the first-time join bonus."))
        .permission("mineconomy.payday.admin")
        .executor(new JoinBonusCommand())
        .arguments(GenericArguments.doubleNum(Text.of("amount")))
        .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        Optional<ProviderRegistration<PaydayService>> opService = Sponge.getServiceManager().getRegistration(PaydayService.class);
        
        /* Could not find payday service. */
        if (!opService.isPresent())
        {
            return CommandResult.empty();
        }
        
        Optional<ProviderRegistration<EconomyService>> opEconomy = Sponge.getServiceManager().getRegistration(EconomyService.class);
        
        /* Could not find economy service. */
        if (!opEconomy.isPresent())
        {
            return CommandResult.empty();
        }
        
        PaydayService payday = opService.get().getProvider();
        EconomyService economy = opEconomy.get().getProvider();
        
        BigDecimal bonus = BigDecimal.valueOf((Double) args.getOne("amount").get());
        if (bonus.compareTo(BigDecimal.ZERO) < 0)
        {
            return CommandResult.empty();
        }
        
        try
        {
            payday.setJoinBonus(bonus);
            DecimalFormat format = new DecimalFormat("#0.00");
            Text msg = Text.of(TextColors.GREEN, "You've set the payday join bonus to ", TextColors.YELLOW, format.format(bonus), " ",
                    bonus.compareTo(BigDecimal.ONE) != 0 ? economy.getDefaultCurrency().getPluralDisplayName().toPlain() : economy.getDefaultCurrency().getDisplayName().toPlain(),
                    TextColors.GREEN, ".");
            src.sendMessage(msg);
            return CommandResult.success();
        }
        catch (IOException ex)
        {
            Mineconomy.getLogger().error(null, ex);
            return CommandResult.empty();
        }
    }
}
