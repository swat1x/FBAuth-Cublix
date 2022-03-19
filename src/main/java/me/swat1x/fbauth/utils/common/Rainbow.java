package me.swat1x.fbauth.utils.common;

import java.util.ArrayList;
import java.util.Hashtable;

public class Rainbow {

    private double minNum;
    private double maxNum;
    private String[] colours;
    private ArrayList<ColourGradient> colourGradients;

    public Rainbow() {
        minNum = 0;
        maxNum = 100;
        colours = new String[]{"red", "yellow", "lime", "blue"};
        setSpectrum(colours);

    }
    public String colourAt(double number) {
        if (colourGradients.size() == 1) {
            return colourGradients.get(0).colourAt(number);
        } else {
            double segment = (maxNum - minNum)/(colourGradients.size());
            int index = (int) Math.min(Math.floor((Math.max(number, minNum) - minNum)/segment), colourGradients.size() - 1);
            return colourGradients.get(index).colourAt(number);
        }
    }
    public void setSpectrum (String ... spectrum) {
        double increment = (maxNum - minNum)/(spectrum.length - 1);
        ColourGradient firstGradient = new ColourGradient();
        firstGradient.setGradient(spectrum[0], spectrum[1]);
        firstGradient.setNumberRange(minNum, minNum + increment);

        colourGradients = new ArrayList<ColourGradient>();
        colourGradients.add(firstGradient);

        for (int i = 1; i < spectrum.length - 1; i++) {
            ColourGradient colourGradient = new ColourGradient();
            colourGradient.setGradient(spectrum[i], spectrum[i + 1]);
            colourGradient.setNumberRange(minNum + increment * i, minNum + increment * (i + 1));
            colourGradients.add(colourGradient);
        }

        colours = spectrum;

    }

    public void setNumberRange(double minNumber, double maxNumber)
    {
        if (maxNumber > minNumber) {
            minNum = minNumber;
            maxNum = maxNumber;
            setSpectrum(colours);
        }
    }

    /**
     * Same as colourAt(double number)
     */
    public String colorAt(double number) {
        return colourAt(number);
    }

}

class ColourGradient {

    private int[] startColour = {0xff, 0x00, 0x00};
    private int[] endColour = {0x00, 0x00, 0xff};
    private double minNum = 0;
    private double maxNum = 100;

    private static Hashtable<String, int[]> htmlColors;
    static {
        htmlColors = new Hashtable<String, int[]>();
        htmlColors.put("black", new int[]{0x00, 0x00, 0x00});
        htmlColors.put("navy", new int[]{0x00, 0x00, 0x80});
        htmlColors.put("blue", new int[]{0x00, 0x00, 0xff});
        htmlColors.put("green", new int[]{0x00, 0x80, 0x00});
        htmlColors.put("teal", new int[]{0x00, 0x80, 0x80});
        htmlColors.put("lime", new int[]{0x00, 0xff, 0x00});
        htmlColors.put("aqua", new int[]{0x00, 0xff, 0xff});
        htmlColors.put("maroon", new int[]{0x80, 0x00, 0x00});
        htmlColors.put("purple", new int[]{0x80, 0x00, 0x80});
        htmlColors.put("olive", new int[]{0x80, 0x80, 0x00});
        htmlColors.put("grey", new int[]{0x80, 0x80, 0x80});
        htmlColors.put("gray", new int[]{0x80, 0x80, 0x80});
        htmlColors.put("silver", new int[]{0xc0, 0xc0, 0xc0});
        htmlColors.put("red", new int[]{0xff, 0x00, 0x00});
        htmlColors.put("fuchsia", new int[]{0xff, 0x00, 0xff});
        htmlColors.put("orange", new int[]{0xff, 0x80, 0x00});
        htmlColors.put("yellow", new int[]{0xff, 0xff, 0x00});
        htmlColors.put("white", new int[]{0xff, 0xff, 0xff});
    }

    public String colourAt(double number) {
        return 	formatHex(calcHex(number, startColour[0], endColour[0]))
                +	formatHex(calcHex(number, startColour[1], endColour[1]))
                +	formatHex(calcHex(number, startColour[2], endColour[2]));
    }

    private int calcHex(double number, int channelStart, int channelEnd) {
        double num = number;
        if (num < minNum) {
            num = minNum;
        }
        if (num > maxNum) {
            num = maxNum;
        }
        double numRange = maxNum - minNum;
        double cPerUnit = (channelEnd - channelStart)/numRange;
        return (int) Math.round(cPerUnit * (num - minNum) + channelStart);
    }

    private String formatHex (int val)
    {
        String hex = Integer.toHexString(val);
        if (hex.length() == 1) {
            return '0' + hex;
        } else {
            return hex;
        }
    }

    public void setNumberRange(double minNumber, double maxNumber) {
        if (maxNumber > minNumber) {
            minNum = minNumber;
            maxNum = maxNumber;
        }
    }

    public void setGradient (String colourStart, String colourEnd) {
        startColour = getHexColour(colourStart);
        endColour = getHexColour(colourEnd);
    }

    private int[] getHexColour(String s)  {
        if (s.matches("^#?[0-9a-fA-F]{6}$")){
            return rgbStringToArray(s.replace("#", ""));
        } else {
            int[] rgbArray = htmlColors.get(s.toLowerCase());
            return rgbArray;
        }
    }

    private int[] rgbStringToArray(String s){
        int red = Integer.parseInt(s.substring(0,2), 16);
        int green = Integer.parseInt(s.substring(2,4), 16);
        int blue = Integer.parseInt(s.substring(4,6), 16);
        return new int[]{red, green, blue};
    }

}
