package com.jtripled.mineconomy.lottery.service;

import com.google.common.reflect.TypeToken;
import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.lottery.Lottery;
import com.jtripled.mineconomy.lottery.LotteryPrize;
import com.jtripled.mineconomy.lottery.task.LotteryEndTask;
import com.jtripled.mineconomy.lottery.task.LotteryStartTask;
import com.jtripled.mineconomy.lottery.task.LotteryWaitTask;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 *
 * @author jtripled
 */
public final class LotteryServiceProvider implements LotteryService
{
    private final LotteryWaitTask waitTask;
    
    private Lottery lottery;
    
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode rootNode;
    
    private int frequency;
    private int duration;
    
    public LotteryServiceProvider() throws IOException
    {
        loadConfig();
        this.waitTask = new LotteryWaitTask();
        this.waitTask.setWaitTime(this.getFrequency() + 1);
        this.waitTask.setLottery(null);
        Sponge.getScheduler().createTaskBuilder()
            .execute(this.waitTask)
            .interval(1, TimeUnit.MINUTES)
            .name("Lottery: Wait")
            .submit(Mineconomy.INSTANCE);
    }
    
    @Listener
    public void onReload(GameReloadEvent event) throws IOException
    {
        loadConfig();
    }
    
    private void loadConfig() throws IOException
    {
        Path configPath = Mineconomy.getConfigDirectory().resolve("lottery.conf");
        if (this.loader == null)
            loader = HoconConfigurationLoader.builder().setPath(configPath).build();
        if (Files.notExists(configPath))
        {
            Sponge.getAssetManager().getAsset(Mineconomy.INSTANCE, "lottery.conf").get().copyToDirectory(Mineconomy.getConfigDirectory());
        }
        rootNode = loader.load();
        Asset asset = Sponge.getAssetManager().getAsset(Mineconomy.INSTANCE, "lottery.conf").get();
        rootNode.mergeValuesFrom(loader.load());
        loader.save(rootNode);
        
        /* Load values. */
        this.frequency = rootNode.getNode("frequency").getInt();
        this.duration = rootNode.getNode("duration").getInt();
        
        /* Validate values. */
        if (this.frequency < 1)
            setFrequency(1);
        if (this.duration < 1)
            setFrequency(1);
    }
    
    private void saveConfig() throws IOException
    {
        loader.save(rootNode);
    }
    
    @Override
    public boolean isLotteryRunning()
    {
        return this.lottery != null;
    }
    
    @Override
    public Lottery getLottery()
    {
        return this.lottery;
    }
    
    @Override
    public void waitLottery(int duration)
    {
        this.waitTask.setWaitTime(duration);
    }

    @Override
    public void startLottery(Lottery lottery)
    {
        if (!this.isLotteryRunning())
        {
            this.lottery = lottery;
            Sponge.getScheduler().createTaskBuilder()
                .execute(new LotteryStartTask(this.lottery))
                .name("Lottery " + this.lottery.getID() + ": Start")
                .submit(Mineconomy.INSTANCE);
            this.waitTask.setWaitTime(this.lottery.getMinutesRemaining() + this.getFrequency());
            this.waitTask.setLottery(this.lottery);
        }
    }
    
    @Override
    public void endLottery()
    {
        if (this.isLotteryRunning())
        {
            Sponge.getScheduler().createTaskBuilder()
                .execute(new LotteryEndTask(this.lottery))
                .name("Lottery " + this.lottery.getID() + ": End")
                .submit(Mineconomy.INSTANCE);
            this.lottery = null;
            this.waitTask.setWaitTime(this.getFrequency());
            this.waitTask.setLottery(null);
        }
    }

    @Override
    public void setFrequency(int minutes) throws IOException
    {
        if (minutes > 0)
        {
            this.frequency = minutes;
            if (!this.isLotteryRunning() && this.waitTask.getWaitTime() > this.frequency)
                this.waitTask.setWaitTime(this.frequency);
            this.rootNode.getNode("frequency").setValue(this.frequency);
            saveConfig();
        }
    }

    @Override
    public void setDuration(int minutes) throws IOException
    {
        if (minutes > 0)
        {
            this.duration = minutes;
            this.rootNode.getNode("duration").setValue(this.duration);
            saveConfig();
        }
    }

    @Override
    public int getFrequency()
    {
        return frequency;
    }
    
    @Override
    public int getDuration()
    {
        return duration;
    }
    
    @Override
    public void createPrize(String name, int weight, BigDecimal ticketCost, BigDecimal money, List<ItemStack> items) throws IOException
    {
        ConfigurationNode prizeNode = this.rootNode.getNode("prizes", name);
        prizeNode.getNode("weight").setValue(weight);
        prizeNode.getNode("cost").setValue(ticketCost);
        prizeNode.getNode("money").setValue(money);
        prizeNode.getNode("items").setValue(null);
        for (int i = 0; i < items.size(); i++)
        {
            try
            {
                prizeNode.getNode("items", i).setValue(TypeToken.of(ItemStack.class), items.get(i));
            }
            catch (ObjectMappingException ex)
            {
                Mineconomy.getLogger().error("Could not save prize set.", ex);
            }
        }
        this.rootNode.getNode("total-weight").setValue(this.rootNode.getNode("total-weight").getInt(0) + weight);
        this.saveConfig();
    }
    
    @Override
    public void deletePrize(String name) throws IOException
    {
        this.rootNode.getNode("total-weight").setValue(this.rootNode.getNode("total-weight").getInt(0)
            - this.rootNode.getNode("prizes", name, "weight").getInt(0));
        this.rootNode.getNode("prizes").removeChild(name);
        this.saveConfig();
    }
    
    @Override
    public LotteryPrize getRandomPrize()
    {
        ConfigurationNode prizeNode = this.rootNode.getNode("prizes");
        int random = new Random().nextInt(this.rootNode.getNode("total-weight").getInt()) + 1;
        Mineconomy.getLogger().info(random + " " + prizeNode.getChildrenMap().size());
        for (ConfigurationNode node : prizeNode.getChildrenMap().values())
        {
            random -= node.getNode("weight").getInt();
            if (random <= 0)
                return this.getNamedPrize((String) node.getKey());
        }
        return null;
    }
    
    @Override
    public LotteryPrize getNamedPrize(String name)
    {
        LotteryPrize prize = null;
        ConfigurationNode prizeNode = this.rootNode.getNode("prizes", name);
        if (!prizeNode.isVirtual())
        {
            prize = new LotteryPrize(this.rootNode.getNode("prizes", name));
        }
        return prize;
    }
}
