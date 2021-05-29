package edu.unc.cem.util;

public class DateUtil {
    public static boolean checkYear(int year) {
        return year >= 0 && year <= 99;
    }

    public static boolean checkMonth(int month) {
        return month >= 1 && month <= 12;
    }

    public static boolean checkDay(int month, int day) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return day >= 1 && day <= 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return day >= 1 && day <= 30;
            case 2:
                return day >= 1 && day <= 29;
            default:
                return false;
        }
    }
}
