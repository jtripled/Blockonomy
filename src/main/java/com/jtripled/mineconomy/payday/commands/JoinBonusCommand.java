package com.jtripled.mineconomy.payday.commands;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.payday.PaydayText;
import com.jtripled.sponge.util.TextUtil;
import java.io.IOException;
import java.math.BigDecimal;
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
import com.jtripled.mineconomy.payday.PaydayService;

/**
 *
 * @author jtripled
 */
public class JoinBonusCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Set the payday first-time join bonus."))
        .permission("mineconomy.payday.admin")
        .executor(new JoinBonusCommand())
        .arguments(GenericArguments.doubleNum(Text.of("amount")))
        .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        Optional<ProviderRegistration<PaydayService>> opPayday = Sponge.getServiceManager().getRegistration(PaydayService.class);
        
        /* Could not find payday service. */
        if (!opPayday.isPresent())
        {
            src.sendMessage(TextUtil.serviceNotFound("PaydayService"));
            return CommandResult.empty();
        }
        
        Optional<ProviderRegistration<EconomyService>> opEconomy = Sponge.getServiceManager().getRegistration(EconomyService.class);
        
        /* Could not find economy service. */
        if (!opEconomy.isPresent())
        {
            src.sendMessage(TextUtil.serviceNotFound("EconomyService"));
            return CommandResult.empty();
        }
        
        PaydayService payday = opPayday.get().getProvider();
        EconomyService economy = opEconomy.get().getProvider();
        
        BigDecimal bonus = BigDecimal.valueOf((Double) args.getOne("amount").get());
        if (bonus.compareTo(BigDecimal.ZERO) < 0)
        {
            src.sendMessage(PaydayText.invalidJoinBonusText(economy.getDefaultCurrency().getPluralDisplayName().toPlain()));
            return CommandResult.empty();
        }
        
        try
        {
            payday.setJoinBonus(bonus);
            src.sendMessage(PaydayText.setJoinBonusText(bonus, economy.getDefaultCurrency().getDisplayName().toPlain(), economy.getDefaultCurrency().getPluralDisplayName().toPlain()));
            return CommandResult.success();
        }
        catch (IOException ex)
        {
            src.sendMessage(PaydayText.setJoinBonusErrorText());
            Mineconomy.getLogger().error(null, ex);
            return CommandResult.empty();
        }
    }
}
