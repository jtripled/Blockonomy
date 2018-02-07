package com.jtripled.mineconomy.economy;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.economy.commands.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;

/**
 *
 * @author jtripled
 */
public class EconomyModule
{
    @Listener
    public void onInitialization(GameInitializationEvent event)
    {
        Sponge.getCommandManager().register(Mineconomy.INSTANCE, BalanceCommand.SPEC, "balance", "money", "bal");
        Sponge.getCommandManager().register(Mineconomy.INSTANCE, PayCommand.SPEC, "pay");
        Sponge.getCommandManager().register(Mineconomy.INSTANCE, AdminCommand.SPEC, "economy");
    }
}
