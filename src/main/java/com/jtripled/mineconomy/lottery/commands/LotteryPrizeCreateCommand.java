/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jtripled.mineconomy.lottery.commands;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.lottery.LotteryText;
import com.jtripled.mineconomy.lottery.service.LotteryService;
import com.jtripled.sponge.util.TextUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

/**
 *
 * @author jtripled
 */
public class LotteryPrizeCreateCommand implements CommandExecutor
{
    public static final CommandSpec SPEC = CommandSpec.builder()
        .description(Text.of("Set your current inventory as a prize set."))
        .permission("mineconomy.lottery.admin")
        .executor(new LotteryPrizeCreateCommand())
        .arguments(GenericArguments.string(Text.of("name")),
                GenericArguments.doubleNum(Text.of("cost")),
                GenericArguments.doubleNum(Text.of("money")),
                GenericArguments.integer(Text.of("weight")))
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
        
        Optional<ProviderRegistration<LotteryService>> opLottery
                = Sponge.getServiceManager().getRegistration(LotteryService.class);
        
        /* Could not find lottery service. */
        if (!opLottery.isPresent())
        {
            src.sendMessage(TextUtil.serviceNotFound("LotteryService"));
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
        
        LotteryService lotterySrv = opLottery.get().getProvider();
        EconomyService economySrv = opEconomy.get().getProvider();
        
        String name = (String) args.getOne("name").get();
        
        if (lotterySrv.getNamedPrize(name) != null)
        {
            src.sendMessage(LotteryText.prizeAlreadyExists(name));
            return CommandResult.empty();
        }
        
        List<ItemStack> items = new ArrayList<>();
        Optional<ItemStack> poll = player.getInventory().poll();
        while (poll.isPresent())
        {
            items.add(poll.get());
            poll = player.getInventory().poll();
        }
        
        try
        {
            lotterySrv.createPrize(name,
                    (int) args.getOne("weight").get(),
                    BigDecimal.valueOf((double) args.getOne("cost").get()),
                    BigDecimal.valueOf((double) args.getOne("money").get()),
                    items);
            src.sendMessage(LotteryText.prizeCreated(name));
            return CommandResult.success();
        }
        catch (IOException ex)
        {
            Mineconomy.getLogger().error("Could not created prize set.", ex);
            return CommandResult.empty();
        }
    }
}
