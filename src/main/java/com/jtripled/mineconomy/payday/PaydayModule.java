package com.jtripled.mineconomy.payday;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.mineconomy.payday.commands.PaydayCommand;
import com.jtripled.sponge.util.TextUtil;
import io.github.nucleuspowered.nucleus.api.events.NucleusFirstJoinEvent;
import io.github.nucleuspowered.nucleus.api.service.NucleusAFKService;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

/**
 *
 * @author jtripled
 */
public class PaydayModule
{
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode rootNode;
    
    private BigDecimal joinBonus;
    private BigDecimal amount;
    private int frequency;
    private final Map<Player, Integer> cooldowns;
    
    private NucleusAFKService afkService;
    
    public PaydayModule() throws IOException
    {
        /* Initialize payday service. */
        this.loadConfig();
        this.cooldowns = new HashMap<>();
        
        /* Register payday commands. */
        Sponge.getCommandManager().register(Mineconomy.INSTANCE, PaydayCommand.SPEC, "payday");
        
        /* Add check task. */
        Sponge.getScheduler().createTaskBuilder()
                .execute(new PaydayTask())
                .interval(1, TimeUnit.MINUTES)
                .name("PaydayTask")
                .submit(Mineconomy.INSTANCE);
    }
    
    @Listener
    public void onReload(GameReloadEvent event) throws IOException
    {
        this.loadConfig();
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
    
    /* Give join bonus if enabled. */
    @Listener
    public void onPlayerFirstJoin(NucleusFirstJoinEvent event)
    {
        /* Join bonus is disabled. */
        if (this.joinBonus.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        
        EconomyService economy = Mineconomy.getEconomy();
        
        Player player = event.getTargetEntity();
        
        Optional<UniqueAccount> opAccount = economy.getOrCreateAccount(player.getUniqueId());
        
        /* Could not find player's account. */
        if (!opAccount.isPresent())
        {
            Sponge.getServer().getConsole().sendMessage(TextUtil.playerAccountNotFound(player));
            return;
        }
        
        UniqueAccount account = opAccount.get();
        
        /* Award join bonus. */
        account.deposit(economy.getDefaultCurrency(), this.joinBonus, Cause.of(EventContext.empty(), Mineconomy.getInstance()));
        player.sendMessage(PaydayText.joinBonusText(this.joinBonus));
    }
    
    private void loadConfig() throws IOException
    {
        Path configPath = Mineconomy.getConfigDirectory().resolve("payday.conf");
        if (this.loader == null)
            this.loader = HoconConfigurationLoader.builder().setPath(configPath).build();
        if (Files.notExists(configPath))
        {
            Sponge.getAssetManager().getAsset(Mineconomy.INSTANCE, "payday.conf").get().copyToDirectory(Mineconomy.getConfigDirectory());
        }
        this.rootNode = this.loader.load();
        Asset asset = Sponge.getAssetManager().getAsset(Mineconomy.INSTANCE, "payday.conf").get();
        this.rootNode.mergeValuesFrom(loader.load());
        this.loader.save(this.rootNode);
        
        /* Load values. */
        this.joinBonus = BigDecimal.valueOf(this.rootNode.getNode("join-bonus").getDouble());
        this.amount = BigDecimal.valueOf(this.rootNode.getNode("amount").getDouble());
        this.frequency = this.rootNode.getNode("frequency").getInt();
        
        /* Validate values. */
        if (this.joinBonus.compareTo(BigDecimal.ZERO) < 0)
            this.setJoinBonus(BigDecimal.ZERO);
        if (this.amount.compareTo(BigDecimal.ZERO) < 0)
            this.setAmount(BigDecimal.ZERO);
        if (this.frequency < 1)
            this.setFrequency(1);
    }
    
    private void saveConfig() throws IOException
    {
        this.loader.save(this.rootNode);
    }
    
    public void setFrequency(int minutes) throws IOException
    {
        if  (minutes >= 1)
        {
            try
            {
                this.rootNode.getNode("frequency").setValue(minutes);
                this.saveConfig();
                this.frequency = minutes;
            }
            catch (IOException ex)
            {
                Sponge.getServer().getConsole().sendMessage(PaydayText.setFrequencyErrorText());
                throw ex;
            }
        }
    }
    
    public void setAmount(BigDecimal amount) throws IOException
    {
        if (amount.compareTo(BigDecimal.ZERO) >= 0)
        {
            try
            {
                this.rootNode.getNode("amount").setValue(amount);
                this.saveConfig();
                this.amount = amount;
            }
            catch (IOException ex)
            {
                Sponge.getServer().getConsole().sendMessage(PaydayText.setAmountErrorText());
                throw ex;
            }
        }
    }
    
    public void setJoinBonus(BigDecimal joinBonus) throws IOException
    {
        if (joinBonus.compareTo(BigDecimal.ZERO) >= 0)
        {
            try
            {
                this.rootNode.getNode("join-bonus").setValue(joinBonus);
                this.saveConfig();
                this.joinBonus = joinBonus;
            }
            catch (IOException ex)
            {
                Sponge.getServer().getConsole().sendMessage(PaydayText.setJoinBonusErrorText());
                throw ex;
            }
        }
    }
    
    public int getFrequency()
    {
        return this.frequency;
    }
    
    public BigDecimal getAmount()
    {
        return this.amount;
    }
    
    public BigDecimal getJoinBonus()
    {
        return this.joinBonus;
    }
    
    public boolean decrementCooldown(Player player)
    {
        int current = this.getCooldown(player);
        if (current > this.getFrequency())
            current = this.getFrequency();
        this.cooldowns.put(player, current - 1);
        return current - 1 <= 0;
    }
    
    public int getCooldown(Player player)
    {
        return this.cooldowns.containsKey(player) ? this.cooldowns.get(player) : this.getFrequency();
    }
    
    public void resetCooldown(Player player)
    {
        this.cooldowns.put(player, this.getFrequency());
    }
    
    public boolean isAFK(Player player)
    {
        return this.afkService == null || this.afkService.isAFK(player);
    }
}
