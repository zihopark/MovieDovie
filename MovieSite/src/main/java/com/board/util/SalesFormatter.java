package com.board.util;

public class SalesFormatter {

    public static String formatSales(int totalSales) {
        if (totalSales >= 1_000_000) {
            return String.format("%.1fM", totalSales / 1_000_000.0);
        } else if (totalSales >= 1_000) {
            return String.format("%.1fK", totalSales / 1000.0);
        } else {
            return String.valueOf(totalSales);
        }
    }

}
