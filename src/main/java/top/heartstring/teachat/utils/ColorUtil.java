//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorUtil {
    public ColorUtil() {
    }

    public static String parseColor(String s) {
        StringBuilder builder = new StringBuilder();
        List<String> colors = new ArrayList();
        int start = -1;

        for(int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '<') {
                String subString = s.substring(i);
                if (subString.startsWith("<#")) {
                    int indexOf = subString.indexOf(">");
                    String c1 = subString.substring(1, indexOf);
                    colors = Arrays.asList(c1.split("-"));
                    start = i + indexOf + 1;
                } else if (subString.startsWith("<end>")) {
                    builder.append(parseColor(colors, s.substring(start, i)));
                    i += 4;
                    start = -1;
                }
            } else if (start == -1) {
                builder.append(s.charAt(i));
            }
        }

        return builder.toString();
    }

    public static String parseColor(List<String> colors, String text) {
        StringBuilder b = new StringBuilder();
        int length = text.length() / (colors.size() - 1);

        for(int j = 0; j < colors.size() - 1; ++j) {
            Color colorA = Color.decode("0x" + ((String)colors.get(j)).substring(1));
            Color colorB = Color.decode("0x" + ((String)colors.get(j + 1)).substring(1));
            int start = j * length;
            int end = (j + 1) * length;
            if (j == colors.size() - 2) {
                length = length + text.length() - end;
                end = text.length();
            }

            int i = 0;
            char[] var10 = text.substring(start, end).toCharArray();
            char[] var11 = var10;
            int var12 = var10.length;

            for(int var13 = 0; var13 < var12; ++var13) {
                char c = var11[var13];
                String d = getLocationColor(colorA, colorB, length, i);
                b.append(d);
                b.append(c);
                ++i;
            }
        }

        return b.toString();
    }

    public static String getLocationColor(Color c1, Color c2, int distance, int location) {
        int r = c1.getRed() + Math.round((float)(c2.getRed() - c1.getRed()) / (float)distance * (float)location);
        int g = c1.getGreen() + Math.round((float)(c2.getGreen() - c1.getGreen()) / (float)distance * (float)location);
        int b = c1.getBlue() + Math.round((float)(c2.getBlue() - c1.getBlue()) / (float)distance * (float)location);
        return String.format("§#%02x%02x%02x", r, g, b);
    }

}
