package com.sib.ibanklosucl.utilies;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {

    public static String formatCurrency(BigDecimal amount) {
        return formatCurrency(amount, true, true);
    }

    public static String formatCurrency(BigDecimal amount, boolean showSymbol, boolean showDecimals) {
        if (amount == null) {
            return "N/A";
        }

        NumberFormat indianCurrencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        // Configure decimal places
        if (!showDecimals) {
            indianCurrencyFormat.setMinimumFractionDigits(0);
            indianCurrencyFormat.setMaximumFractionDigits(0);
        } else {
            indianCurrencyFormat.setMinimumFractionDigits(2);
            indianCurrencyFormat.setMaximumFractionDigits(2);
        }

        String formatted = indianCurrencyFormat.format(amount);

        // Remove symbol if not needed
        if (!showSymbol) {
            formatted = formatted.substring(1).trim();
        }

        return formatted;
    }

    public static String formatCurrencyCompact(BigDecimal amount) {
        if (amount == null) {
            return "N/A";
        }

        double value = amount.doubleValue();

        if (value >= 10000000) { // 1 Cr
            return String.format("₹%.2f Cr", value / 10000000);
        } else if (value >= 100000) { // 1 Lakh
            return String.format("₹%.2f L", value / 100000);
        } else if (value >= 1000) { // 1 K
            return String.format("₹%.2f K", value / 1000);
        } else {
            return formatCurrency(amount);
        }
    }
}
