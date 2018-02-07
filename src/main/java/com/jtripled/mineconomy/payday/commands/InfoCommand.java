package com.jtripled.mineconomy.payday.commands;

import com.jtripled.mineconomy.payday.PaydayService;
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
        
        BigDecimal joinBonus = payday.getJoinBonus();
        BigDecimal paycheck = payday.getPaycheck();
        
        DecimalFormat format = new DecimalFormat("#0.00");
        Text topBorder = Text.of("==================== ", TextColors.GREEN, "Payday Info", TextColors.WHITE, " =====================");
        Text joinBonusMsg = Text.of(TextColors.AQUA, "Join Bonus", TextColors.WHITE, " = ", format.format(joinBonus), " ",
            (joinBonus.compareTo(BigDecimal.ONE) != 0 ? economy.getDefaultCurrency().getPluralDisplayName().toPlain() : economy.getDefaultCurrency().getDisplayName().toPlain()));
        Text paycheckMsg = Text.of(TextColors.AQUA, "Paycheck", TextColors.WHITE, " = ", format.format(paycheck), " ",
            (paycheck.compareTo(BigDecimal.ONE) != 0 ? economy.getDefaultCurrency().getPluralDisplayName().toPlain() : economy.getDefaultCurrency().getDisplayName().toPlain()));
        Text intervalMsg = Text.of(TextColors.AQUA, "Frequency", TextColors.WHITE, " = ", payday.getInterval(), " minutes");
        Text bottomBorder = Text.of("=====================================================");
        
        src.sendMessages(topBorder, joinBonusMsg, paycheckMsg, intervalMsg, bottomBorder);
        
        return CommandResult.success();
    }
}
