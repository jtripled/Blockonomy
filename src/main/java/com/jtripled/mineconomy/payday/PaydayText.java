package com.jtripled.mineconomy.payday;

import com.jtripled.mineconomy.Mineconomy;
import com.jtripled.sponge.util.TextUtil;
import java.math.BigDecimal;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

/**
 *
 * @author jtripled
 */
public class PaydayText
{
    public static Text paydayText(BigDecimal amount)
    {
        return Text.of(TextColors.GREEN, "You have earned a paycheck of ", TextColors.YELLOW,
                TextUtil.money(amount, Mineconomy.getEconomy()), TextColors.GREEN, ".");
    }
    
    public static Text joinBonusText(BigDecimal amount)
    {
        return Text.of(TextColors.GREEN, "Welcome to our server! You've been awarded ", TextColors.YELLOW,
                TextUtil.money(amount, Mineconomy.getEconomy()), TextColors.GREEN, "!");
    }
    
    public static Text setFrequencyText(int minutes)
    {
        return Text.of(TextColors.GREEN, "You've set the payday frequency to ", TextColors.YELLOW,
                TextUtil.pluralize(minutes, "minute", "minutes"), TextColors.GREEN, ".");
    }
    
    public static Text setAmountText(BigDecimal amount)
    {
        return Text.of(TextColors.GREEN, "You've set the payday amount to ", TextColors.YELLOW,
                TextUtil.money(amount, Mineconomy.getEconomy()), TextColors.GREEN, ".");
    }
    
    public static Text setJoinBonusText(BigDecimal amount)
    {
        return Text.of(TextColors.GREEN, "You've set the payday join bonus to ", TextColors.YELLOW,
                TextUtil.money(amount, Mineconomy.getEconomy()), TextColors.GREEN, ".");
    }
    
    public static Text invalidFrequencyText()
    {
        return Text.of(TextColors.RED, "You cannot set payday frequency below ",
                TextColors.YELLOW, "1 minute", TextColors.RED, ".");
    }
    
    public static Text invalidAmountText()
    {
        return Text.of(TextColors.RED, "You cannot set payday amount below ",
                TextColors.YELLOW, TextUtil.money(BigDecimal.valueOf(0.00), Mineconomy.getEconomy()), TextColors.RED, ".");
    }
    
    public static Text invalidJoinBonusText()
    {
        return Text.of(TextColors.RED, "You cannot set payday join bonus below ",
                TextColors.YELLOW, TextUtil.money(BigDecimal.valueOf(0.00), Mineconomy.getEconomy()), TextColors.RED, ".");
    }
    
    public static Text setFrequencyErrorText()
    {
        return Text.of(TextColors.RED, "There was an error setting the payday frequency.");
    }
    
    public static Text setAmountErrorText()
    {
        return Text.of(TextColors.RED, "There was an error setting the payday amount.");
    }
    
    public static Text setJoinBonusErrorText()
    {
        return Text.of(TextColors.RED, "There was an error setting the payday join bonus.");
    }
    
    public static Text infoFrequencyText(int frequency)
    {
        return Text.of(Text.builder("Frequency: ").color(TextColors.AQUA)
                .onClick(TextActions.suggestCommand("/payday frequency 30"))
                .onHover(TextActions.showText(Text.of("Click here to modify the frequency."))).build(),
                TextColors.YELLOW, TextUtil.pluralize(frequency, "minute", "minutes"));
    }
    
    public static Text infoAmountText(BigDecimal amount)
    {
        return Text.of(Text.builder("Amount: ").color(TextColors.AQUA)
                .onClick(TextActions.suggestCommand("/payday amount 20.00"))
                .onHover(TextActions.showText(Text.of("Click here to modify the amount."))).build(),
                TextColors.YELLOW, TextUtil.money(amount, Mineconomy.getEconomy()));
    }
    
    public static Text infoJoinBonusText(BigDecimal joinBonus)
    {
        return Text.of(Text.builder("Join Bonus: ").color(TextColors.AQUA)
                .onClick(TextActions.suggestCommand("/payday joinbonus 200.00"))
                .onHover(TextActions.showText(Text.of("Click here to modify the join bonus."))).build(),
                TextColors.YELLOW, TextUtil.money(joinBonus, Mineconomy.getEconomy()));
    }
}
