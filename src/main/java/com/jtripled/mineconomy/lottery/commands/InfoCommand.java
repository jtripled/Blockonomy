package com.jtripled.mineconomy.lottery.commands;

import com.jtripled.mineconomy.lottery.LotteryService;
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
        .description(Text.of("Set the lottery chance."))
        .permission("mineconomy.admin")
        .executor(new InfoCommand())
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
        
        Optional<ProviderRegistration<EconomyService>> opEconomy = Sponge.getServiceManager().getRegistration(EconomyService.class);
        
        /* Could not find economy service. */
        if (!opEconomy.isPresent())
        {
            return CommandResult.empty();
        }
        
        LotteryService lottery = opService.get().getProvider();
        EconomyService economy = opEconomy.get().getProvider();
        
        DecimalFormat format = new DecimalFormat("#0.00");
        Text topBorder = Text.of("==================== ", TextColors.GREEN, "Lottery Info", TextColors.WHITE, " =====================");
        Text intervalMsg = Text.of(TextColors.AQUA, "Frequency", TextColors.WHITE, " = ", lottery.getFrequency(), " minutes");
        Text durationMsg = Text.of(TextColors.AQUA, "Duration", TextColors.WHITE, " = ", lottery.getDuration(), " minutes");
        Text chanceMsg = Text.of(TextColors.AQUA, "Chance", TextColors.WHITE, " = ", String.format("%.0f", lottery.getChance() * 100), "%");
        Text costMsg = Text.of(TextColors.AQUA, "Cost", TextColors.WHITE, " = ", format.format(lottery.getCost()), " ",
            (lottery.getCost().compareTo(BigDecimal.ONE) != 0 ? economy.getDefaultCurrency().getPluralDisplayName().toPlain() : economy.getDefaultCurrency().getDisplayName().toPlain()));
        Text bottomBorder = Text.of("=====================================================");
        
        src.sendMessages(topBorder, intervalMsg, durationMsg, chanceMsg, costMsg, bottomBorder);
        
        return CommandResult.success();
    }
}
