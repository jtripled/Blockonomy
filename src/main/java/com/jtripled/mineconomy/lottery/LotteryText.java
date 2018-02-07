package com.jtripled.mineconomy.lottery;

import com.jtripled.sponge.util.TextUtil;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

/**
 *
 * @author jtripled
 */
public class LotteryText
{
    private static final Text BUY_1 = Text.of(TextColors.GREEN, "Type \"");
    private static final Text BUY_2 = Text.builder("/lottery buy [amount]").color(TextColors.YELLOW)
            .onClick(TextActions.suggestCommand("/lottery buy"))
            .onHover(TextActions.showText(Text.of("Click here to suggest this command.")))
            .build();
    private static final Text BUY_3 = Text.of(TextColors.GREEN, "\" to purchase tickets. Or click here: ");
    private static final Text BUY_4 = Text.builder("[x1] ").color(TextColors.AQUA)
            .onClick(TextActions.runCommand("/lottery buy 1"))
            .onHover(TextActions.showText(Text.of("Click here to purchase 1 ticket.")))
            .build();
    private static final Text BUY_5 = Text.builder("[x5] ").color(TextColors.AQUA)
            .onClick(TextActions.runCommand("/lottery buy 5"))
            .onHover(TextActions.showText(Text.of("Click here to purchase 5 tickets.")))
            .build();
    private static final Text BUY_6 = Text.builder("[x10] ").color(TextColors.AQUA)
            .onClick(TextActions.runCommand("/lottery buy 10"))
            .onHover(TextActions.showText(Text.of("Click here to purchase 10 tickets.")))
            .build();
    private static final Text BUY_7 = Text.builder("[x25] ").color(TextColors.AQUA)
            .onClick(TextActions.runCommand("/lottery buy 25"))
            .onHover(TextActions.showText(Text.of("Click here to purchase 25 tickets.")))
            .build();
    private static final Text BUY_8 = Text.builder("[x50] ").color(TextColors.AQUA)
            .onClick(TextActions.runCommand("/lottery buy 50"))
            .onHover(TextActions.showText(Text.of("Click here to purchase 50 tickets.")))
            .build();
    private static final Text BUY_9 = Text.builder("[x100]").color(TextColors.AQUA)
            .onClick(TextActions.runCommand("/lottery buy 100"))
            .onHover(TextActions.showText(Text.of("Click here to purchase 100 tickets.")))
            .build();
    
    public static final Text BUY_TEXT = Text.join(BUY_1, BUY_2, BUY_3, BUY_4, BUY_5, BUY_6, BUY_7, BUY_8, BUY_9);
    
    private static final Text INFO_1 = Text.builder("Click ").color(TextColors.GREEN).build();
    
    private static final Text INFO_2 = Text.builder("here").color(TextColors.YELLOW)
                        .onClick(TextActions.runCommand("/lottery"))
                        .onHover(TextActions.showText(Text.of("Click here for more info on the lottery.")))
                        .build();
    
    private static final Text INFO_3 = Text.builder(" for more info.").color(TextColors.GREEN).build();
    
    public static final Text INFO_TEXT = Text.join(INFO_1, INFO_2, INFO_3);
    
    public static final Text BEGUN_TEXT = Text.of(TextColors.GREEN, "A lottery has just begun! ");
    
    public static final Text END_TEXT_NONE = Text.of(TextColors.RED, "There were not enough participants in the lottery.");
    
    public static final Text END_TEXT_1 = Text.of(TextColors.GREEN, "The lottery has ended! And the winner is ");
    
    public static final Text END_TEXT_2 = Text.of(TextColors.GREEN, "! Congratulations!");
    
    public static final Text REFUND_TEXT = Text.of(TextColors.GREEN, "You're money has been refunded.");
    
    public static Text buyTicketInsufficientFundsText()
    {
        return Text.of(TextColors.RED, "You cannot afford that many tickets.");
    }
    
    public static Text buyTicketText(int count, BigDecimal cost, String singular, String plural)
    {
        return Text.of(TextColors.GREEN, "You've purchased ", TextColors.YELLOW,
                count, TextUtil.pluralize(count, "ticket", "tickets"),
                TextColors.GREEN, "for ", TextColors.YELLOW,
                TextUtil.pluralize(cost.multiply(BigDecimal.valueOf(count)), singular, plural, new DecimalFormat("#0.00")),
                TextColors.GREEN, ".");
    }
    
    public static Text setChanceText(double chance)
    {
        return Text.of(TextColors.GREEN, "You've set the lottery chance to ", TextColors.YELLOW, String.format("%.0f",chance * 100), "%", TextColors.GREEN, ".");
    }
    
    public static Text setFrequencyText(int minutes)
    {
        return Text.of(TextColors.GREEN, "You've set the lottery frequency to ", TextColors.YELLOW, TextUtil.pluralize(minutes, "minute", "minutes"), TextColors.GREEN, ".");
    }
    
    public static Text setDurationText(int minutes)
    {
        return Text.of(TextColors.GREEN, "You've set the lottery duration to ", TextColors.YELLOW, TextUtil.pluralize(minutes, "minute", "minutes"), TextColors.GREEN, ".");
    }
    
    public static Text setCostText(BigDecimal minutes, String singular, String plural)
    {
        return Text.of(TextColors.GREEN, "You've set the lottery cost to ", TextColors.YELLOW, TextUtil.pluralize(minutes, singular, plural), TextColors.GREEN, ".");
    }
    
    public static Text invalidChanceText()
    {
        return Text.of(TextColors.RED, "Lottery chance must be between ",
                TextColors.YELLOW, "0.01", TextColors.RED, " and ", TextColors.YELLOW, "1.00",
                TextColors.RED, ".");
    }
    
    public static Text invalidFrequencyText()
    {
        return Text.of(TextColors.RED, "You cannot set lottery frequency below ",
                TextColors.YELLOW, "1 minute", TextColors.RED, ".");
    }
    
    public static Text invalidDurationText()
    {
        return Text.of(TextColors.RED, "You cannot set lottery duration below ",
                TextColors.YELLOW, "1 minute", TextColors.RED, ".");
    }
    
    public static Text invalidCostText(String plural)
    {
        return Text.of(TextColors.RED, "You cannot set lottery cost below ",
                TextColors.YELLOW, "0.00 ", plural, TextColors.RED, ".");
    }
    
    public static Text setChanceErrorText()
    {
        return Text.of(TextColors.RED, "There was an error setting the lottery chance.");
    }
    
    public static Text setFrequencyErrorText()
    {
        return Text.of(TextColors.RED, "There was an error setting the lottery frequency.");
    }
    
    public static Text setDurationErrorText()
    {
        return Text.of(TextColors.RED, "There was an error setting the lottery duration.");
    }
    
    public static Text setCostErrorText()
    {
        return Text.of(TextColors.RED, "There was an error setting the lottery cost.");
    }
    
    public static Text noRunningLotteryText()
    {
        return Text.of(TextColors.RED, "There is no lottery currently running.");
    }
}
