package me.often.aureliummobs.Utils;

import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Formatter {

    private final NumberFormat numberFormat;

    public Formatter(int maxPlaces) {
        // Creates DecimalFormat pattern based on maxPlaces
        StringBuilder pattern = new StringBuilder("#");
        if (maxPlaces > 0) {
            pattern.append(".");
        }
        pattern.append(StringUtils.repeat("#", maxPlaces));
        this.numberFormat = new DecimalFormat(pattern.toString());
    }

    public String format(double input) {
        return numberFormat.format(input);
    }

}
