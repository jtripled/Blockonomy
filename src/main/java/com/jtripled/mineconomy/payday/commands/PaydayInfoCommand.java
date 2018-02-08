package com.jtripled.mineconomy.payday.commands;

import com.jtripled.sponge.util.TextUtil;
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
import com.jtripled.mineconomy.payday.service.PaydayService;
import com.jtripled.mineconomy.payday.PaydayText;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.format.TextColors;

/**
 *
 * @author jtripled
 */
public class PaydayInfoCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Display current paycheck amount, interval, and join bonus."))
        .permission("mineconomy.payday.admin")
        .executor(new PaydayInfoCommand())
        .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        Optional<ProviderRegistration<PaydayService>> opPayday
                = Sponge.getServiceManager().getRegistration(PaydayService.class);
        
        /* Could not find payday service. */
        if (!opPayday.isPresent())
        {
            src.sendMessage(TextUtil.serviceNotFound("PaydayService"));
            return CommandResult.empty();
        }
        
        Optional<ProviderRegistration<EconomyService>> opEconomy
                = Sponge.getServiceManager().getRegistration(EconomyService.class);
        
        /* Could not find economy service. */
        if (!opEconomy.isPresent())
        {
            src.sendMessage(TextUtil.serviceNotFound("EconomyService"));
            return CommandResult.empty();
        }
        
        PaydayService payday = opPayday.get().getProvider();
        EconomyService economy = opEconomy.get().getProvider();
        
        /* Create pagination contents. */
        List<Text> contents = new ArrayList<>();
        contents.add(PaydayText.infoFrequencyText(payday.getFrequency()));
        contents.add(PaydayText.infoAmountText(payday.getAmount(), economy));
        contents.add(PaydayText.infoJoinBonusText(payday.getJoinBonus(), economy));
        
        /* Send contents to command sender. */
        PaginationList.builder()
                .title(Text.of(TextColors.GREEN, "Payday Info"))
                .padding(Text.of(TextColors.GREEN, "="))
                .contents(contents)
                .sendTo(src);
        
        return CommandResult.success();
    }
}
