package com.jtripled.mineconomy.lottery;

import com.jtripled.sponge.util.TextUtil;
import java.math.BigDecimal;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

/**
 *
 * @author jtripled
 */
public class LotteryText
{
    
    private static final Text INFO_1 = Text.builder("Click ").color(TextColors.GREEN).build();
    
    private static final Text INFO_2 = Text.builder("here").color(TextColors.AQUA).style(TextStyles.UNDERLINE)
                        .onClick(TextActions.runCommand("/lottery"))
                        .onHover(TextActions.showText(Text.of("Click here for more info on the lottery.")))
                        .build();
    
    private static final Text INFO_3 = Text.builder(" for more info.").color(TextColors.GREEN).build();
    
    public static Text buyText(BigDecimal cost, EconomyService economy)
    {
        return Text.of(TextColors.GREEN, "Click here to buy tickets: ", TextColors.AQUA,
                Text.builder("[x1] ").color(TextColors.AQUA)
                    .onClick(TextActions.runCommand("/lottery buy 1"))
                    .onHover(TextActions.showText(Text.of("Click here to purchase 1 ticket for ", TextColors.YELLOW, TextUtil.money(BigDecimal.valueOf(1).multiply(cost), economy), TextColors.WHITE, ".")))
                    .build(),
                Text.builder("[x5] ").color(TextColors.AQUA)
                    .onClick(TextActions.runCommand("/lottery buy 5"))
                    .onHover(TextActions.showText(Text.of("Click here to purchase 5 ticket for ", TextColors.YELLOW, TextUtil.money(BigDecimal.valueOf(5).multiply(cost), economy), TextColors.WHITE, ".")))
                    .build(),
                Text.builder("[x10] ").color(TextColors.AQUA)
                    .onClick(TextActions.runCommand("/lottery buy 10"))
                    .onHover(TextActions.showText(Text.of("Click here to purchase 10 ticket for ", TextColors.YELLOW, TextUtil.money(BigDecimal.valueOf(10).multiply(cost), economy), TextColors.WHITE, ".")))
                    .build(),
                Text.builder("[x25] ").color(TextColors.AQUA)
                    .onClick(TextActions.runCommand("/lottery buy 25"))
                    .onHover(TextActions.showText(Text.of("Click here to purchase 25 ticket for ", TextColors.YELLOW, TextUtil.money(BigDecimal.valueOf(25).multiply(cost), economy), TextColors.WHITE, ".")))
                    .build(),
                Text.builder("[x50] ").color(TextColors.AQUA)
                    .onClick(TextActions.runCommand("/lottery buy 50"))
                    .onHover(TextActions.showText(Text.of("Click here to purchase 50 ticket for ", TextColors.YELLOW, TextUtil.money(BigDecimal.valueOf(50).multiply(cost), economy), TextColors.WHITE, ".")))
                    .build(),
                Text.builder("[x100] ").color(TextColors.AQUA)
                    .onClick(TextActions.runCommand("/lottery buy 100"))
                    .onHover(TextActions.showText(Text.of("Click here to purchase 100 ticket for ", TextColors.YELLOW, TextUtil.money(BigDecimal.valueOf(100).multiply(cost), economy), TextColors.WHITE, ".")))
                    .build());
    }
    
    public static Text prizeAlreadyExists(String name)
    {
        return Text.of(TextColors.RED, "A prize set named ",
                TextColors.YELLOW, name, TextColors.RED, " already exists.");
    }
    
    public static Text prizeNotExists(String name)
    {
        return Text.of(TextColors.RED, "There is no prize set named ",
                TextColors.YELLOW, name, TextColors.RED, ".");
    }
    
    public static Text prizeCreated(String name)
    {
        return Text.of(TextColors.GREEN, "You have created a prize set named ",
                TextColors.YELLOW, name, TextColors.GREEN, ".");
    }
    
    public static Text prizeDeleted(String name)
    {
        return Text.of(TextColors.GREEN, "You have deleted a prize set named ",
                TextColors.YELLOW, name, TextColors.GREEN, ".");
    }
    
    public static Text winnerText(Player player)
    {
        return Text.of(TextColors.GREEN, "The lottery has ended, and the winner is ",
                TextColors.YELLOW, player.getName(), TextColors.GREEN, "! Congratulations!");
    }
    
    public static Text noWinnerText()
    {
        return Text.of(TextColors.RED, "The lottery has ended, but there were not enough participants.");
    }
    
    public static Text refundText(BigDecimal amount, EconomyService economy)
    {
        return Text.of(TextColors.GREEN, "You have been refunded ", TextColors.YELLOW,
                TextUtil.money(amount, economy), TextColors.GREEN, ".");
    }
    
    public static Text moreInfoText()
    {
        return Text.join(INFO_1, INFO_2, INFO_3);
    }
    
    public static Text buyTicketInsufficientFundsText()
    {
        return Text.of(TextColors.RED, "You cannot afford that many tickets.");
    }
    
    public static Text buyTicketText(int count, BigDecimal cost, EconomyService economy)
    {
        return Text.of(TextColors.GREEN, "You've purchased ", TextColors.YELLOW,
                TextUtil.pluralize(count, "ticket", "tickets"), TextColors.GREEN, " for ", TextColors.YELLOW,
                TextUtil.money(cost.multiply(BigDecimal.valueOf(count)), economy),
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
    
    public static Text setCostText(BigDecimal minutes, EconomyService economy)
    {
        return Text.of(TextColors.GREEN, "You've set the lottery cost to ", TextColors.YELLOW, TextUtil.money(minutes, economy), TextColors.GREEN, ".");
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
    
    public static Text invalidCostText(EconomyService economy)
    {
        return Text.of(TextColors.RED, "You cannot set lottery cost below ",
                TextColors.YELLOW, TextUtil.money(BigDecimal.valueOf(0.00d), economy), TextColors.RED, ".");
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
    
    public static Text lotteryAlreadyRunningText()
    {
        return Text.of(TextColors.RED, "There is already a lottery running.");
    }
    
    public static Text noRunningLotteryText()
    {
        return Text.of(TextColors.RED, "There is no lottery currently running.");
    }
    
    public static Text infoFrequencyText(int frequency)
    {
        return Text.of(Text.builder("Frequency: ").color(TextColors.AQUA)
                .onClick(TextActions.suggestCommand("/lottery frequency 30"))
                .onHover(TextActions.showText(Text.of("Click here to modify the frequency."))).build(),
                TextColors.YELLOW, TextUtil.pluralize(frequency, "minute", "minutes"));
    }
    
    public static Text infoDurationText(int duration)
    {
        return Text.of(Text.builder("Duration: ").color(TextColors.AQUA)
                .onClick(TextActions.suggestCommand("/lottery duration 5"))
                .onHover(TextActions.showText(Text.of("Click here to modify the duration."))).build(),
                TextColors.YELLOW, TextUtil.pluralize(duration, "minute", "minutes"));
    }
    
    public static Text infoChanceText(double chance)
    {
        return Text.of(Text.builder("Chance: ").color(TextColors.AQUA)
                .onClick(TextActions.suggestCommand("/lottery chance 1.00"))
                .onHover(TextActions.showText(Text.of("Click here to modify the chance."))).build(),
                TextColors.YELLOW, TextUtil.percentage(chance));
    }
    
    public static Text infoCostText(BigDecimal cost, EconomyService economy)
    {
        return Text.of(Text.builder("Cost: ").color(TextColors.AQUA)
                .onClick(TextActions.suggestCommand("/lottery cost 20.00"))
                .onHover(TextActions.showText(Text.of("Click here to modify the cost."))).build(),
                TextColors.YELLOW, TextUtil.money(cost, economy));
    }
    
    public static Text lotteryTimeRemainingText(int minutes)
    {
        return Text.of(TextColors.GREEN, "Time remaining: ", TextColors.YELLOW,
                TextUtil.pluralize(minutes, "minute", "minutes"));
    }
    
    public static Text lotteryTicketCostText(BigDecimal cost, EconomyService economy)
    {
        return Text.of(TextColors.GREEN, "Ticket cost: ", TextColors.YELLOW,
                TextUtil.money(cost, economy));
    }
    
    public static Text lotteryPrizeText(Lottery lottery, EconomyService service)
    {
        return Text.of(TextColors.GREEN, "Prize: ", TextColors.YELLOW,
                prizeText(lottery, service));
    }
    
    public static Text prizeText(Lottery lottery, EconomyService service)
    {
        Text out = null;
        if (lottery.getMoney().compareTo(BigDecimal.ZERO) > 0)
        {
            out = Text.of(TextUtil.money(lottery.getMoney(), service));
        }
        if (!lottery.getItems().isEmpty())
        {
            for (ItemStack item : lottery.getItems())
            {
                if (out != null)
                    out = Text.join(out, Text.of(", "), Text.of(item.getQuantity() > 1 ? item.getQuantity() + "x " : "", item.getType().getName()));
                else
                    out = Text.of(item.getQuantity() > 1 ? item.getQuantity() + "x " : "", item.getType().getName());
            }
        }
        if (out == null)
            out = Text.of();
        return out;
    }
    
    public static Text lotteryCurrentTicketText(int tickets)
    {
        return Text.of(TextColors.GREEN, "Current tickets: ", TextColors.YELLOW,
                TextUtil.pluralize(tickets, "ticket", "tickets"));
    }
    
    public static Text lotteryTotalTicketText(int tickets)
    {
        return Text.of(TextColors.GREEN, "Total tickets: ", TextColors.YELLOW,
                TextUtil.pluralize(tickets, "ticket", "tickets"));
    }
    
    public static Text countdownText(int minutesRemaining)
    {
        return Text.of(TextColors.GREEN, "There ", minutesRemaining == 1 ? "is " : "are ",
            TextColors.YELLOW, minutesRemaining, minutesRemaining == 1 ? " minute " : " minutes ",
            TextColors.GREEN, "remaining in the lottery. ", moreInfoText());
    }
    
    public static Text beginText()
    {
        return Text.of(TextColors.GREEN, "A lottery has just begun! ", moreInfoText());
    }
    
    public static Text endLotteryCmdText()
    {
        return Text.of(TextColors.GREEN, "You have ended the current lottery.");
    }
    
    public static Text startLotteryCmdText()
    {
        return Text.of(TextColors.GREEN, "You have started a new lottery.");
    }
}
