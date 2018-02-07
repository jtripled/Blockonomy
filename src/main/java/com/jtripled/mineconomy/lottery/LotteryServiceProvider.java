package com.jtripled.mineconomy.lottery;

import com.jtripled.mineconomy.Mineconomy;
import io.github.nucleuspowered.nucleus.api.service.NucleusAFKService;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 *
 * @author jtripled
 */
public class LotteryServiceProvider implements LotteryService
{
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode rootNode;
    
    private int frequency;
    private int duration;
    private double chance;
    private BigDecimal cost;
    
    private NucleusAFKService afkService;
    
    private boolean running;
    private int minutesRemaining;
    private LotteryPrizeSet prize;
    private Task countdownTask;
    private int cooldown;
    
    private int ticketCount;
    private Map<Player, Integer> tickets;
    
    public LotteryServiceProvider() throws IOException
    {
        loadConfig();
    }
    
    @Listener
    public void onReload(GameReloadEvent event) throws IOException
    {
        loadConfig();
    }
    
    /* Update service providers. */
    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event)
    {
        if (event.getService() == NucleusAFKService.class)
        {
            afkService = (NucleusAFKService) event.getNewProvider();
        }
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
        this.frequency = rootNode.getNode("frequency").getInt();
        this.duration = rootNode.getNode("duration").getInt();
        this.chance = rootNode.getNode("chance").getDouble();
        this.cost = BigDecimal.valueOf(rootNode.getNode("cost").getDouble());
        if (this.frequency < 1)
            setFrequency(1);
        if (this.duration < 1)
            setFrequency(1);
        if (this.chance <= 0.00d)
            setChance(0.01d);
        if (this.chance > 1.00d)
            setChance(1.00d);
        if (this.cost.compareTo(BigDecimal.ONE) < 0)
            setCost(BigDecimal.ONE);
        this.cooldown = this.frequency;
    }
    
    private void saveConfig() throws IOException
    {
        loader.save(rootNode);
    }
    
    @Override
    public boolean decrementCooldown()
    {
        this.cooldown -= 1;
        return this.cooldown <= 0;
    }
    
    @Override
    public LotteryPrizeSet getPrize()
    {
        return this.prize;
    }
    
    @Override
    public boolean isLotteryRunning()
    {
        return running;
    }

    @Override
    public int minutesRemaining()
    {
        return minutesRemaining;
    }
    
    @Override
    public boolean shouldLotteryStart()
    {
        if (new Random().nextFloat() > this.chance)
        {
            this.cooldown = getFrequency();
            return false;
        }
        
        if (isLotteryRunning())
        {
            return false;
        }
        
        int playerCount = 0;
        for (Player player : Sponge.getServer().getOnlinePlayers())
            if (player.isOnline() && (afkService == null || !afkService.isAFK(player)))
                playerCount += 1;
        
        if (playerCount < 2)
        {
            return false;
        }
        
        return true;
    }

    @Override
    public void startLottery(LotteryPrizeSet prize)
    {
        this.running = true;
        this.minutesRemaining = getDuration();
        this.cooldown = getFrequency();
        this.ticketCount = 0;
        this.tickets = new HashMap<>();
        
        if (prize == null)
        {
            prize = new LotteryPrizeSet(BigDecimal.valueOf(100), null);
        }
        
        this.prize = prize;
        
        Text msg = Text.of(LotteryText.BEGUN_TEXT, LotteryText.INFO_TEXT);
        Sponge.getServer().getOnlinePlayers().forEach((Player player) -> {
            player.sendMessage(msg);
        });
        countdown(this.minutesRemaining - 1);
    }
    
    @Override
    public void endLottery()
    {
        Player test = null;
        
        if (ticketCount > 0 && tickets.keySet().size() > 1)
        {
            do
            {
                int random = new Random().nextInt(ticketCount) + 1;
                for (Player player : tickets.keySet())
                {
                    int t = tickets.get(player);
                    random -= t;
                    if (random <= 0)
                    {
                        test = player;
                        break;
                    }
                }
                if (test == null) break;
                if (!test.isOnline())
                {
                    ticketCount -= tickets.get(test);
                    tickets.remove(test);
                }
            } while (!test.isOnline());
        }
        
        final Player winner = test;
        final int cnt = this.ticketCount;
        
        if (winner != null)
        {
            Text msg = Text.of(LotteryText.END_TEXT_1, TextColors.YELLOW, winner.getName());
            Sponge.getServer().getOnlinePlayers().forEach((Player player) -> {
                player.sendMessage(Text.of(LotteryText.END_TEXT_1, TextColors.YELLOW, winner.getName(),
                        LotteryText.END_TEXT_2));
            });
            prize.award(winner, getCost().multiply(BigDecimal.valueOf(cnt)));
        }
        else
        {
            Optional<ProviderRegistration<EconomyService>> opEconomy = Sponge.getServiceManager().getRegistration(EconomyService.class);
            EconomyService economy = opEconomy.get().getProvider();
            
            Sponge.getServer().getOnlinePlayers().forEach((Player player) -> {
                player.sendMessage(LotteryText.END_TEXT_NONE);
                if (tickets.containsKey(player))
                {
                    player.sendMessage(LotteryText.REFUND_TEXT);
                    economy.getOrCreateAccount(player.getUniqueId()).get().deposit(economy.getDefaultCurrency(), getCost().multiply(BigDecimal.valueOf(tickets.get(player))), Cause.of(EventContext.empty(), Mineconomy.INSTANCE));
                }
            });
        }
        
        this.running = false;
        this.minutesRemaining = -1;
        this.ticketCount = 0;
        this.tickets = new HashMap<>();
    }

    @Override
    public void setFrequency(int minutes) throws IOException
    {
        if (minutes > 0)
        {
            this.frequency = minutes;
            if (this.cooldown > this.frequency)
                this.cooldown = this.frequency;
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
    public void setChance(double chance) throws IOException
    {
        if (chance > 0.00d && chance <= 1.00d)
        {
            this.chance = chance;
            this.rootNode.getNode("chance").setValue(this.chance);
            saveConfig();
        }
    }
    
    @Override
    public void setCost(BigDecimal cost) throws IOException
    {
        if (cost.compareTo(BigDecimal.ZERO) > 0)
        {
            this.cost = cost;
            this.rootNode.getNode("cost").setValue(this.cost);
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
    public double getChance()
    {
        return chance;
    }
    
    @Override
    public BigDecimal getCost()
    {
        return cost;
    }
    
    @Override
    public int getPlayerTicketCount(Player player)
    {
        return tickets.containsKey(player) ? tickets.get(player) : 0;
    }
    
    @Override
    public int getTotalTicketCount()
    {
        return ticketCount;
    }
    
    private void countdown(int minutesRemaining)
    {
        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        this.countdownTask = taskBuilder.execute((Task t1) -> {
            Text countText = Text.of(TextColors.GREEN, "There ", minutesRemaining == 1 ? "is " : "are ",
                TextColors.YELLOW, minutesRemaining, minutesRemaining == 1 ? " minute " : " minutes ",
                TextColors.GREEN, "remaining in the lottery.");
            
            this.minutesRemaining = minutesRemaining;
            if (minutesRemaining <= 0)
            {
                endLottery();
            }
            else
            {
                Text out = Text.join(countText, Text.of(" "), LotteryText.INFO_TEXT);
                Sponge.getServer().getOnlinePlayers().forEach((Player player) -> {
                    player.sendMessage(out);
                });
                countdown(minutesRemaining - 1);
            }
        }).delay(1, TimeUnit.MINUTES).name("Lottery - Countdown").submit(Mineconomy.INSTANCE);
    }

    @Override
    public boolean buyTickets(Player player, int quantity)
    {
        if (!isLotteryRunning())
        {
            return false;
        }
        
        Optional<ProviderRegistration<EconomyService>> opEconomy = Sponge.getServiceManager().getRegistration(EconomyService.class);
        
        /* Could not find economy service. */
        if (!opEconomy.isPresent())
        {
            return false;
        }
        
        EconomyService economy = opEconomy.get().getProvider();
        
        Optional<UniqueAccount> opAccount = economy.getOrCreateAccount(player.getUniqueId());

        /* Could not find player's account. */
        if (!opAccount.isPresent())
        {
            return false;
        }

        UniqueAccount account = opAccount.get();
        
        BigDecimal totalCost = getCost().multiply(BigDecimal.valueOf(quantity));
        if (account.getBalance(economy.getDefaultCurrency()).compareTo(totalCost) < 0)
        {
            return false;
        }
        
        account.withdraw(economy.getDefaultCurrency(), totalCost, Cause.of(EventContext.empty(), Mineconomy.INSTANCE));
        
        tickets.put(player, getPlayerTicketCount(player) + quantity);
        ticketCount += quantity;
        
        return true;
    }
}
