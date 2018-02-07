package com.jtripled.mineconomy.economy.commands;

import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

/**
 *
 * @author jtripled
 */
public class AdminCommand
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Set the balance of the specified account."))
        .permission("mineconomy.admin")
        .child(SetBalanceCommand.SPEC, "set")
        .child(AdjustBalanceCommand.SPEC, "adjust")
        .build();
}
