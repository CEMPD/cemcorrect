package edu.unc.cem.util;

public class DateUtil {
	public static boolean checkYear( int year) {
		if ( year < 0 || year > 99) {
			return false;
		}
		return true;
	}
	
	public static boolean checkMonth( int month) {
		if ( month < 1 || month > 12) {
			return false;
		}
		return true;
	}
	public static boolean checkDay( int month, int day) {
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			if ( day <1 || day > 31) {
				return false;
			}
			return true;
		case 4:
		case 6:
		case 9:
		case 11:
			if ( day <1 || day > 30) {
				return false;
			}
			return true;
		case 2:
			if ( day <1 || day > 29) {
				return false;
			}
			return true;
		default:
			return false;
		}
	}
}
