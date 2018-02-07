package com.jtripled.sponge.util;

import java.math.BigDecimal;
import java.text.Format;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 *
 * @author jtripled
 */
public class TextUtil
{
    public static Text join(String s1, String s2, boolean space)
    {
        if (space)
            return Text.of(s1, " ", s2);
        return Text.of(s1, s2);
    }
    
    public static Text pluralize(int count, String singular, String plural)
    {
        return pluralize(count, singular, plural, null);
    }
    
    public static Text pluralize(double count, String singular, String plural)
    {
        return pluralize(count, singular, plural, null);
    }
    
    public static Text pluralize(BigDecimal count, String singular, String plural)
    {
        return pluralize(count, singular, plural, null);
    }
    
    public static Text pluralize(int count, String singular, String plural, Format format)
    {
        return join(format == null ? String.valueOf(count) : format.format(count), count == 1 ? singular : plural, true);
    }
    
    public static Text pluralize(double count, String singular, String plural, Format format)
    {
        return join(format == null ? String.valueOf(count) : format.format(count), count == 1.00d ? singular : plural, true);
    }
    
    public static Text pluralize(BigDecimal count, String singular, String plural, Format format)
    {
        return join(format == null ? String.valueOf(count) : format.format(count), count.compareTo(BigDecimal.ONE) == 0 ? singular : plural, true);
    }
    
    public static Text percentage(double value)
    {
        return Text.of(String.format("%.0f", value * 100), "%");
    }
    
    public static Text serviceNotFound(String service)
    {
        return Text.of(TextColors.RED, "ERROR: Could not find service: ", service);
    }
    
    public static Text playerOnly()
    {
        return Text.of(TextColors.RED, "This command can only be send by players.");
    }
}
