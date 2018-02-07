package com.jtripled.mineconomy.payday.commands;

import com.jtripled.sponge.util.TextUtil;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import com.jtripled.mineconomy.payday.PaydayService;

/**
 *
 * @author jtripled
 */
public class InfoCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Display current paycheck amount, interval, and join bonus."))
        .permission("mineconomy.payday.admin")
        .executor(new InfoCommand())
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
        
        BigDecimal joinBonus = payday.getJoinBonus();
        BigDecimal paycheck = payday.getAmount();
        
        Text topBorder = Text.of("==================== ", TextColors.GREEN, "Payday Info", TextColors.WHITE, " =====================");
        Text freqMsg = Text.of(TextColors.AQUA, "Frequency", TextColors.WHITE, " = ", TextUtil.pluralize(payday.getFrequency(), "minute", "minutes"));
        Text amountMsg = Text.of(TextColors.AQUA, "Amount", TextColors.WHITE, " = ", TextUtil.pluralize(paycheck, economy.getDefaultCurrency().getDisplayName().toPlain(), economy.getDefaultCurrency().getPluralDisplayName().toPlain(), new DecimalFormat("#0.00")));
        Text joinBonusMsg = Text.of(TextColors.AQUA, "Join Bonus", TextColors.WHITE, " = ", TextUtil.pluralize(joinBonus, economy.getDefaultCurrency().getDisplayName().toPlain(), economy.getDefaultCurrency().getPluralDisplayName().toPlain(), new DecimalFormat("#0.00")));
        Text bottomBorder = Text.of("=====================================================");
        src.sendMessages(topBorder, freqMsg, amountMsg, joinBonusMsg, bottomBorder);
        
        return CommandResult.success();
    }
}
