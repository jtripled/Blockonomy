package com.jtripled.mineconomy.payday.commands;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.payday.PaydayModule;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
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
        /* Create pagination contents. */
        List<Text> contents = new ArrayList<>();
        contents.add(PaydayText.infoFrequencyText(Mineconomy.getPayday().getFrequency()));
        contents.add(PaydayText.infoAmountText(Mineconomy.getPayday().getAmount()));
        contents.add(PaydayText.infoJoinBonusText(Mineconomy.getPayday().getJoinBonus()));
        
        /* Send contents to command sender. */
        PaginationList.builder()
                .title(Text.of(TextColors.GREEN, "Payday Info"))
                .padding(Text.of(TextColors.GREEN, "="))
                .contents(contents)
                .sendTo(src);
        
        return CommandResult.success();
    }
}
