package me.swat1x.fbauth.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;

public class HexConstants {

    public static ChatColor TEXT = ChatColor.of(Color.decode("#C0C0C0"));
    public static ChatColor BLUE = ChatColor.of(Color.decode("#7FFFD4"));
    public static ChatColor RED = ChatColor.of(Color.decode("#DC143C"));
    public static ChatColor ORANGE = ChatColor.of(Color.decode("#FF4500"));
    public static ChatColor GRAY = ChatColor.of(Color.decode("#708090"));
    public static ChatColor YELLOW = ChatColor.of(Color.decode("#FFE162"));
    public static ChatColor GREEN = ChatColor.of(Color.decode("#00FA9A"));

    public static String getPreset(String text, GradientPreset preset){
        return TextUtils.createGradFromString(text, preset.gradientList);
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum GradientPreset {
        ORANGE(new String[]{"#FB6500", "#FBB600"}),
        RED(new String[]{"#FF0000", "#CD5C5C"}),
        BLUE(new String[]{"#5ac2fa", "#0a78fc"}),
        LIME(new String[]{"#00ca00", "#00e500"});

        private final String[] gradientList;
    }


}
