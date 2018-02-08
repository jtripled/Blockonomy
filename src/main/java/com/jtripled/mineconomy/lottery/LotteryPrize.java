package com.jtripled.mineconomy.lottery;

import com.google.common.reflect.TypeToken;
import com.jtripled.mineconomy.Mineconomy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 *
 * @author jtripled
 */
public class LotteryPrize
{
    private final BigDecimal cost;
    private final BigDecimal money;
    private final List<ItemStack> items;
    
    public LotteryPrize(ConfigurationNode node)
    {
        this.cost = BigDecimal.valueOf(node.getNode("cost").getDouble(20.00d));
        this.money = BigDecimal.valueOf(node.getNode("money").getDouble(100.00d));
        this.items = new ArrayList<>();
        try
        {
            for (ItemStack item : node.getNode("items").getList(TypeToken.of(ItemStack.class)))
            {
                this.items.add(item);
            }
        }
        catch (ObjectMappingException ex)
        {
            Mineconomy.getLogger().error("Unable to parse prize.", ex);
        }
    }
    
    public LotteryPrize(BigDecimal cost, BigDecimal money, List<ItemStack> items)
    {
        this.cost = cost;
        this.money = money;
        this.items = items;
    }
    
    public BigDecimal getCost()
    {
        return this.cost;
    }
    
    public BigDecimal getMoney()
    {
        return this.money;
    }
    
    public List<ItemStack> getItems()
    {
        return this.items;
    }
}
