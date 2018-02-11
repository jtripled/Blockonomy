package com.jtripled.mineconomy.lottery.commands;

import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

/**
 *
 * @author jtripled
 */
public class LotteryPrizeCommand
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Set your current inventory as a prize set."))
        .permission("mineconomy.lottery.admin")
        .child(LotteryPrizeCreateCommand.SPEC, "create", "c")
        .child(LotteryPrizeDeleteCommand.SPEC, "delete", "d")
        .child(LotteryPrizeDeleteCommand.SPEC, "info", "i")
        .build();
}
