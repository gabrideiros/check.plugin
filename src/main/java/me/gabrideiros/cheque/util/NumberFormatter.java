package me.gabrideiros.cheque.util;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberFormatter {

    private static final Pattern PATTERN = Pattern.compile("^(\\d+\\.?\\d*)(\\D+)");

    private final List<String> suffixes;

    public NumberFormatter() {
        this.suffixes = Arrays.asList("", "K", "M", "B", "T", "Q", "L");
    }

    public NumberFormatter(List<String> suffixes) {
        this.suffixes = suffixes;
    }

    public String formatNumber(double value) {
        int index = 0;

        double tmp;
        while ((tmp = value / 1000) >= 1) {
            value = tmp;
            ++index;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(value) + this.suffixes.get(index);
    }

    public double parseString(String value, boolean integer) throws Exception {

        if (integer) {
            try {
                return Integer.parseInt(value);
            } catch (Exception ignored) {}

            Matcher matcher = PATTERN.matcher(value);
            if (!matcher.find()) {
                throw new Exception("Invalid format");
            }

            int amount = Integer.parseInt(matcher.group(1));
            String suffix = matcher.group(2);

            int index = this.suffixes.indexOf(suffix.toUpperCase());

            return amount * Math.pow(1000, index);
        }

        try {
            return Double.parseDouble(value);
        } catch (Exception ignored) {}

        Matcher matcher = PATTERN.matcher(value);
        if (!matcher.find()) {
            throw new Exception("Invalid format");
        }

        double amount = Double.parseDouble(matcher.group(1));
        String suffix = matcher.group(2);

        int index = this.suffixes.indexOf(suffix.toUpperCase());

        return amount * Math.pow(1000, index);
    }
}