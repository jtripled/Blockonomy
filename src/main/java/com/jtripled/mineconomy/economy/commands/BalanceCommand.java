package com.jtripled.mineconomy.economy.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

/**
 *
 * @author jtripled
 */
public class BalanceCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Displays the account balance of the user or another player."))
        .permission("mineconomy.balance")
        .executor(new BalanceCommand())
        .arguments(GenericArguments.optional(GenericArguments.user(Text.of("account"))))
        .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        return CommandResult.success();
    }
}
