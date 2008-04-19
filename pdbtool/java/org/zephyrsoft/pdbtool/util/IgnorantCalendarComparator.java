package org.zephyrsoft.pdbtool.util;

import java.util.*;

/**
 * Vergleicht lediglich Tag und Monat. Die Zeit und das Jahr wird völlig vernachlässigt. 
 * @author Mathis Dirksen-Thedens
 */
public class IgnorantCalendarComparator implements Comparator<GregorianCalendar> {
	
	public int compare(GregorianCalendar cal1, GregorianCalendar cal2) {
		if (cal1==null || cal2==null) {
			throw new IllegalArgumentException("argument is null");
		}
		int ret = 0;
		if (cal1.get(Calendar.MONTH) < cal2.get(Calendar.MONTH) || 
			(cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_MONTH) < cal2.get(Calendar.DAY_OF_MONTH))) {
				ret = -1;
		} else if (cal1.get(Calendar.MONTH) > cal2.get(Calendar.MONTH) || 
			(cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_MONTH) > cal2.get(Calendar.DAY_OF_MONTH))) {
				ret = 1;
		}
		return ret;
	}
	
	public boolean equals(GregorianCalendar cal1, GregorianCalendar cal2) {
		if (cal1==null || cal2==null) {
			throw new IllegalArgumentException("argument is null");
		}
		boolean ret = false;
		if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)) {
			ret = true;
		}
		return ret;
	}
}
