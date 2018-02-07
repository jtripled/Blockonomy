package com.jtripled.mineconomy.lottery.commands;

import com.jtripled.mineconomy.lottery.LotteryService;
import com.jtripled.mineconomy.lottery.LotteryText;
import com.jtripled.sponge.util.TextUtil;
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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

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
            src.sendMessage(TextUtil.playerOnly());
            return CommandResult.empty();
        }
        
        Player player = (Player) src;
        
        Optional<ProviderRegistration<LotteryService>> opLottery = Sponge.getServiceManager().getRegistration(LotteryService.class);
        
        /* Could not find lottery service. */
        if (!opLottery.isPresent())
        {
            src.sendMessage(TextUtil.serviceNotFound("LotteryService"));
            return CommandResult.empty();
        }
        
        Optional<ProviderRegistration<EconomyService>> opEconomy = Sponge.getServiceManager().getRegistration(EconomyService.class);
        
        /* Could not find economy service. */
        if (!opEconomy.isPresent())
        {
            src.sendMessage(TextUtil.serviceNotFound("EconomyService"));
            return CommandResult.empty();
        }
        
        LotteryService lottery = opLottery.get().getProvider();
        EconomyService economy = opEconomy.get().getProvider();
        
        int quantity = args.hasAny("quantity") ? (int) args.getOne("quantity").get() : 1;
        
        BigDecimal cost = lottery.getCost().multiply(BigDecimal.valueOf(quantity));
        
        if (!lottery.buyTickets(player, quantity))
        {
            src.sendMessage(LotteryText.buyTicketInsufficientFundsText());
            return CommandResult.empty();
        }
        
        src.sendMessage(LotteryText.buyTicketText(quantity, cost, economy.getDefaultCurrency().getDisplayName().toPlain(), economy.getDefaultCurrency().getPluralDisplayName().toPlain()));
        
        return CommandResult.success();
    }
}
