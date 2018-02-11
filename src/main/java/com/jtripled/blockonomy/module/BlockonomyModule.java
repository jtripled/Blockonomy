package com.jtripled.blockonomy.module;

import com.jtripled.mineconomy.Mineconomy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;

/**
 *
 * @author jtripled
 */
public abstract class BlockonomyModule
{
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode rootNode;
    
    public BlockonomyModule()
    {
        Sponge.getEventManager().registerListeners(Mineconomy.getInstance(), this);
    }
    
    public String getConfigName()
    {
        return null;
    }
    
    public final ConfigurationNode getConfig()
    {
        return this.rootNode;
    }
    
    public final void loadConfig() throws IOException
    {
        if (getConfigName() == null)
            return;
        Path configPath = Mineconomy.getConfigDirectory().resolve(this.getConfigName());
        if (this.loader == null)
            loader = HoconConfigurationLoader.builder().setPath(configPath).build();
        if (Files.notExists(configPath))
        {
            Sponge.getAssetManager().getAsset(Mineconomy.getInstance(),
                    this.getConfigName()).get().copyToDirectory(Mineconomy.getConfigDirectory());
        }
        rootNode = loader.load();
        Asset asset = Sponge.getAssetManager().getAsset(Mineconomy.getInstance(),
                this.getConfigName()).get();
        rootNode.mergeValuesFrom(loader.load());
        loader.save(rootNode);
    }
    
    public final void saveConfig() throws IOException
    {
        if (getConfigName() == null)
            return;
        loader.save(rootNode);
    }
}
