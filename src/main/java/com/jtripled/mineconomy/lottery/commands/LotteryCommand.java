package com.jtripled.mineconomy.lottery.commands;

import com.jtripled.mineconomy.lottery.LotteryService;
import com.jtripled.mineconomy.lottery.LotteryText;
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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 *
 * @author jtripled
 */
public class LotteryCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Display information about the current lottery."))
        .permission("mineconomy.lottery")
        .executor(new LotteryCommand())
        .child(BuyCommand.SPEC, "buy", "b")
        .child(ChanceCommand.SPEC, "chance")
        .child(CostCommand.SPEC, "cost")
        .child(DurationCommand.SPEC, "duration", "dur", "d")
        .child(FrequencyCommand.SPEC, "frequency", "frequency", "freq", "f")
        .child(InfoCommand.SPEC, "info", "i")
        .build();
    
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        if (!(src instanceof Player))
        {
            return CommandResult.empty();
        }
        
        Player player = (Player) src;
        
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
        
        if (!lottery.isLotteryRunning())
        {
            Text msg = Text.of(TextColors.RED, "There is no lottery currently running.");
            src.sendMessage(msg);
            return CommandResult.success();
        }
        
        DecimalFormat format = new DecimalFormat("#0.00");
        Text topBorder = Text.of("====================== ", TextColors.GREEN, "Lottery", TextColors.WHITE, " =======================");
        Text timeMsg = Text.of(TextColors.GREEN, "Time remaining: ", TextColors.YELLOW, lottery.minutesRemaining(), " minutes");
        Text costMsg = Text.of(TextColors.GREEN, "Ticket cost: ", TextColors.YELLOW, format.format(lottery.getCost()), " ",
            (lottery.getCost().compareTo(BigDecimal.ONE) != 0 ? economy.getDefaultCurrency().getPluralDisplayName().toPlain() : economy.getDefaultCurrency().getDisplayName().toPlain()));
        Text currentMsg = Text.of(TextColors.GREEN, "Current tickets: ", TextColors.YELLOW, lottery.getPlayerTicketCount(player));
        Text prizeMsg = Text.of(TextColors.GREEN, "Prize: ", TextColors.YELLOW, lottery.getPrize().getText());
        Text bottomBorder = Text.of("=====================================================");
        
        src.sendMessages(topBorder, timeMsg, costMsg, currentMsg, prizeMsg, Text.of(""), LotteryText.BUY_TEXT, bottomBorder);
        return CommandResult.success();
    }
}
