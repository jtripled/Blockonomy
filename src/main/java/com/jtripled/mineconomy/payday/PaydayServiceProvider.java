package com.jtripled.mineconomy.payday;

import com.jtripled.mineconomy.Mineconomy;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private BigDecimal amount;
    private int frequency;
    private final Map<Player, Integer> cooldowns;
    
    public PaydayServiceProvider() throws IOException
    {
        this.loadConfig();
        this.cooldowns = new HashMap<>();
    }
    
    @Listener
    public void onReload(GameReloadEvent event) throws IOException
    {
        this.loadConfig();
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
        this.amount = BigDecimal.valueOf(rootNode.getNode("amount").getDouble());
        this.frequency = rootNode.getNode("frequency").getInt();
        if (this.joinBonus.compareTo(BigDecimal.ZERO) < 0)
            setJoinBonus(BigDecimal.ZERO);
        if (this.amount.compareTo(BigDecimal.ZERO) < 0)
            setAmount(BigDecimal.ZERO);
        if (this.frequency < 1)
            setFrequency(1);
    }
    
    private void saveConfig() throws IOException
    {
        this.loader.save(this.rootNode);
    }
    
    @Override
    public void setJoinBonus(BigDecimal joinBonus) throws IOException
    {
        if (joinBonus.compareTo(BigDecimal.ZERO) >= 0)
        {
            this.joinBonus = joinBonus;
            this.rootNode.getNode("join-bonus").setValue(joinBonus);
            this.saveConfig();
        }
    }

    @Override
    public void setAmount(BigDecimal amount) throws IOException
    {
        if (amount.compareTo(BigDecimal.ZERO) >= 0)
        {
            this.amount = amount;
            this.rootNode.getNode("amount").setValue(amount);
            this.saveConfig();
        }
    }

    @Override
    public void setFrequency(int minutes) throws IOException
    {
        if  (minutes >= 1)
        {
            this.frequency = minutes;
            this.rootNode.getNode("frequency").setValue(minutes);
            this.saveConfig();
        }
    }

    @Override
    public BigDecimal getJoinBonus()
    {
        return this.joinBonus;
    }

    @Override
    public BigDecimal getAmount()
    {
        return this.amount;
    }

    @Override
    public int getFrequency()
    {
        return this.frequency;
    }
    
    @Override
    public boolean decrementCooldown(Player player)
    {
        int current = this.getCooldown(player);
        if (current > this.getFrequency())
            current = this.getFrequency();
        this.cooldowns.put(player, current - 1);
        return current - 1 <= 0;
    }
    
    @Override
    public int getCooldown(Player player)
    {
        return this.cooldowns.containsKey(player) ? this.cooldowns.get(player) : this.getFrequency();
    }
    
    public void resetCooldown(Player player)
    {
        this.cooldowns.put(player, this.getFrequency());
    }
}
