package com.jtripled.mineconomy.payday;

import com.jtripled.sponge.util.TextUtil;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 *
 * @author jtripled
 */
public class PaydayText
{
    public static Text paydayText(BigDecimal amount, String singular, String plural)
    {
        return Text.of(TextColors.GREEN, "You have earned a paycheck of ", TextColors.YELLOW,
                TextUtil.pluralize(amount, singular, plural, new DecimalFormat("#0.00")), TextColors.GREEN, ".");
    }
    
    public static Text joinBonusText(BigDecimal amount, String singular, String plural)
    {
        return Text.of(TextColors.GREEN, "Welcome to our server! You've been awarded ", TextColors.YELLOW,
                TextUtil.pluralize(amount, singular, plural, new DecimalFormat("#0.00")), TextColors.GREEN, "!");
    }
    
    public static Text setFrequencyText(int minutes)
    {
        return Text.of(TextColors.GREEN, "You've set the payday frequency to ", TextColors.YELLOW,
                TextUtil.pluralize(minutes, "minute", "minutes"), TextColors.GREEN, ".");
    }
    
    public static Text setAmountText(BigDecimal amount, String singular, String plural)
    {
        return Text.of(TextColors.GREEN, "You've set the payday amount to ", TextColors.YELLOW,
                TextUtil.pluralize(amount, singular, plural, new DecimalFormat("#0.00")),
                TextColors.GREEN, ".");
    }
    
    public static Text setJoinBonusText(BigDecimal amount, String singular, String plural)
    {
        return Text.of(TextColors.GREEN, "You've set the payday join bonus to ", TextColors.YELLOW,
                TextUtil.pluralize(amount, singular, plural, new DecimalFormat("#0.00")),
                TextColors.GREEN, ".");
    }
    
    public static Text invalidFrequencyText()
    {
        return Text.of(TextColors.RED, "You cannot set payday frequency below ",
                TextColors.YELLOW, "1 minute", TextColors.RED, ".");
    }
    
    public static Text invalidAmountText(String plural)
    {
        return Text.of(TextColors.RED, "You cannot set payday amount below ",
                TextColors.YELLOW, "0.00 ", plural, TextColors.RED, ".");
    }
    
    public static Text invalidJoinBonusText(String plural)
    {
        return Text.of(TextColors.RED, "You cannot set payday join bonus below ",
                TextColors.YELLOW, "0.00 ", plural, TextColors.RED, ".");
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
}