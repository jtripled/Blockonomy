package com.jtripled.mineconomy.payday;

import com.jtripled.mineconomy.Mineconomy;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;

/**
 *
 * @author jtripled
 */
public class PaydayServiceProvider implements PaydayService
{
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode rootNode;
    
    private BigDecimal joinBonus;
    private BigDecimal paycheck;
    private int interval;
    private final Map<Player, Integer> cooldowns;
    
    public PaydayServiceProvider() throws IOException
    {
        loadConfig();
        this.cooldowns = new HashMap<>();
    }
    
    @Listener
    public void onReload(GameReloadEvent event) throws IOException
    {
        loadConfig();
    }
    
    private void loadConfig() throws IOException
    {
        Path configPath = Mineconomy.getConfigDirectory().resolve("payday.conf");
        if (this.loader == null)
            loader = HoconConfigurationLoader.builder().setPath(configPath).build();
        if (Files.notExists(configPath))
        {
            Sponge.getAssetManager().getAsset(Mineconomy.INSTANCE, "payday.conf").get().copyToDirectory(Mineconomy.getConfigDirectory());
        }
        rootNode = loader.load();
        Asset asset = Sponge.getAssetManager().getAsset(Mineconomy.INSTANCE, "payday.conf").get();
        rootNode.mergeValuesFrom(loader.load());
        loader.save(rootNode);
        this.joinBonus = BigDecimal.valueOf(rootNode.getNode("join-bonus").getDouble());
        this.paycheck = BigDecimal.valueOf(rootNode.getNode("amount").getDouble());
        this.interval = rootNode.getNode("frequency").getInt();
        if (this.joinBonus.compareTo(BigDecimal.ZERO) < 0)
            setJoinBonus(BigDecimal.ZERO);
        if (this.paycheck.compareTo(BigDecimal.ZERO) < 0)
            setPaycheck(BigDecimal.ZERO);
        if (this.interval < 1)
            setInterval(1);
    }
    
    private void saveConfig() throws IOException
    {
        loader.save(rootNode);
    }
    
    @Override
    public boolean decrementCooldown(Player player)
    {
        int current = cooldowns.containsKey(player) ? cooldowns.get(player) : getInterval();
        if (current > getInterval())
            current = getInterval();
        cooldowns.put(player, current - 1);
        return current - 1 <= 0;
    }
    
    @Override
    public void setJoinBonus(BigDecimal joinBonus) throws IOException
    {
        if (joinBonus.compareTo(BigDecimal.ZERO) >= 0)
        {
            this.joinBonus = joinBonus;
            this.rootNode.getNode("join-bonus").setValue(joinBonus);
            saveConfig();
        }
    }

    @Override
    public void setPaycheck(BigDecimal amount) throws IOException
    {
        if (amount.compareTo(BigDecimal.ZERO) >= 0)
        {
            this.paycheck = amount;
            this.rootNode.getNode("amount").setValue(amount);
            saveConfig();
        }
    }

    @Override
    public void setInterval(int minutes) throws IOException
    {
        if  (minutes >= 1)
        {
            this.interval = minutes;
            this.rootNode.getNode("frequency").setValue(minutes);
            saveConfig();
        }
    }
    
    @Override
    public int getCooldown(Player player)
    {
        return cooldowns.containsKey(player) ? cooldowns.get(player) : getInterval();
    }

    @Override
    public BigDecimal getJoinBonus()
    {
        return joinBonus;
    }

    @Override
    public BigDecimal getPaycheck()
    {
        return paycheck;
    }

    @Override
    public int getInterval()
    {
        return interval;
    }
}
