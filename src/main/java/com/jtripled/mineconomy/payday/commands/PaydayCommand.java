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
        .description(Text.of("Display current payday amount, frequency, and join bonus."))
        .permission("mineconomy.payday.admin")
        .executor(new PaydayInfoCommand())
        .child(PaydayInfoCommand.SPEC, "info", "i")
        .child(PaydayFrequencyCommand.SPEC, "frequency", "freq", "f")
        .child(PaydayJoinBonusCommand.SPEC, "joinbonus", "join", "j")
        .child(PaydayAmountCommand.SPEC, "amount", "a")
        .build();
}
