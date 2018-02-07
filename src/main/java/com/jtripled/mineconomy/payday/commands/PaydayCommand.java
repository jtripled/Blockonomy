package com.jtripled.mineconomy.payday.commands;

import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

/**
 *
 * @author jtripled
 */
public class PaydayCommand
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Display current paycheck amount, frequency, and join bonus."))
        .permission("mineconomy.payday.admin")
        .executor(new InfoCommand())
        .child(InfoCommand.SPEC, "info", "i")
        .child(FrequencyCommand.SPEC, "frequency", "freq", "f")
        .child(JoinBonusCommand.SPEC, "joinbonus", "join", "j")
        .child(AmountCommand.SPEC, "amount", "a")
        .build();
}
