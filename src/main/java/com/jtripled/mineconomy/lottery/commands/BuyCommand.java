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
import org.spongepowered.api.command.args.GenericArguments;
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
public class BuyCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Purchase tickets for the current lottery."))
        .permission("mineconomy.lottery.buy")
        .executor(new BuyCommand())
        .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("quantity"))))
        .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        if (!(src instanceof Player))
        {
            src.sendMessage(Text.of("This command can only be send by players."));
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
        
        EconomyService economy = opEconomy.get().getProvider();
        LotteryService lottery = opService.get().getProvider();
        
        int quantity = 1;
        
        if (args.hasAny("quantity"))
            quantity = (int) args.getOne("quantity").get();
        
        BigDecimal cost = lottery.getCost().multiply(BigDecimal.valueOf(quantity));
        
        if (!lottery.buyTickets(player, quantity))
        {
            src.sendMessage(Text.of(TextColors.RED, "You cannot afford that many tickets."));
            return CommandResult.empty();
        }
        
        DecimalFormat format = new DecimalFormat("#0.00");
        src.sendMessage(Text.of(TextColors.GREEN, "You've purchased ", TextColors.YELLOW, quantity, " tickets ",
                TextColors.GREEN, "for ", TextColors.YELLOW, format.format(cost), " ",
                cost.compareTo(BigDecimal.ONE) != 0 ? economy.getDefaultCurrency().getPluralDisplayName().toPlain() : economy.getDefaultCurrency().getDisplayName().toPlain(),
                TextColors.GREEN, "."));
        
        return CommandResult.success();
    }
}
