package me.often.aureliummobs.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

public class Formatter {

    private final NumberFormat numberFormat;

    public Formatter(int maxPlaces) {
        // Creates DecimalFormat pattern based on maxPlaces
        StringBuilder pattern = new StringBuilder("#");
        if (maxPlaces > 0) {
            pattern.append(".");
        }
        pattern.append(repeat('#', maxPlaces));
        this.numberFormat = new DecimalFormat(pattern.toString());
    }

    private String repeat(char ch, int repeat) {
        if (repeat <= 0) {
            return "";
        }
        final char[] buf = new char[repeat];
        Arrays.fill(buf, ch);
        return new String(buf);
    }

    public String format(double input) {
        return numberFormat.format(input);
    }

}
