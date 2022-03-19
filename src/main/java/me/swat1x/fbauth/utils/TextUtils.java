package me.swat1x.fbauth.utils;

import me.swat1x.fbauth.utils.common.Rainbow;
import me.swat1x.fbauth.utils.common.TextCentering;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {

    public static String createGradFromString(String name, String[] colours) {
        int count = name.length();
        if (Math.min(count, colours.length) < 2) {
            return name;
        }

        ArrayList<String> cols = createGradient(count, colours);

        String colourCodes = "";
        for (int i = 0; i < cols.size(); i++) {
            colourCodes += ChatColor.of(cols.get(i)) + "" + name.charAt(i);
        }
        return colourCodes;
    }

    public static String getCenter(String s){
        return TextCentering.getCenteredMessage(s);
    }

    public static ArrayList<String> createGradient(int count, String[] colours) {
        Rainbow rainbow = new Rainbow();

        rainbow.setNumberRange(1, count);
        rainbow.setSpectrum(colours);

        ArrayList<String> hexCodes = new ArrayList<String>();
        for (int i = 1; i <= count; i++) {
            hexCodes.add("#" + rainbow.colourAt(i));
        }
        return hexCodes;
    }

    public static String getTimeLabel(long start, long end) {
        StringBuilder text = new StringBuilder();
        long value = end - start;
        long var;
        long dur = 86400000L;

        var = value / dur;
        if (var >= 1) {
            if (!text.toString().equalsIgnoreCase("")) {
                text.append(" ");
            }
            text.append(var).append("д");
        }

        value = value - (dur * var);
        dur = 3600000L;
        var = value / dur;
        if (var >= 1) {
            if (!text.toString().equalsIgnoreCase("")) {
                text.append(" ");
            }
            text.append(var).append("ч");
        }

        value = value - (dur * var);
        dur = 60000L;
        var = value / dur;
        if (var >= 1) {
            if (!text.toString().equalsIgnoreCase("")) {
                text.append(" ");
            }
            text.append(var).append("м");
        }

        value = value - (dur * var);
        dur = 1000L;
        var = value / dur;
        if (var >= 1) {
            if (!text.toString().equalsIgnoreCase("")) {
                text.append(" ");
            }
            text.append(var).append("с");
        }

        long mls = end - start;
        if (mls < 1000L) {
            return mls + "млсек";
        }

        return text.toString();
    }

    public static String translateColors(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> translateColors(List<String> s) {
        List<String> list = new ArrayList<>();
        s.forEach(line -> list.add(translateColors(line)));
        return list;
    }

    public static List<String> replace(List<String> oldList, String target, String value) {
        List<String> list = new ArrayList<>();
        for (String s : oldList) {
            list.add(s.replace(target, value));
        }
        return list;
    }

    public static String getEndingByValue(int amount) {
        String s = amount + "";
        if (s.endsWith("1")) {
            return "";
        }
        if (s.endsWith("2") || s.endsWith("3") || s.endsWith("4")) {
            return "а";
        } else {
            return "ов";
        }
    }

}
