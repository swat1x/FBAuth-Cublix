package me.swat1x.fbauth.utils.common;


import net.md_5.bungee.api.ChatColor;

public class TextCentering {

    public static String translateColors(final String message) {
        return message.replace("&", "§");
    }

    public static String getCenteredMessage(String message){
        message = translateColors(message);
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        for (final char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            }
            else if (previousCode) {
                previousCode = false;
                isBold = (c == 'l' || c == 'L');
            }
            else {
                final DefaultFont dFI = DefaultFont.getDefaultFontInfo(c);
                messagePxSize += (isBold ? dFI.getBoldLength() : dFI.getLength());
                ++messagePxSize;
            }
        }
        final int halvedMessageSize = messagePxSize / 2;
        final int toCompensate = 154 - halvedMessageSize;
        final int spaceLength = DefaultFont.SPACE.getLength() + 1;
        int compensated = 0;
        final StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb+message;
    }


}
