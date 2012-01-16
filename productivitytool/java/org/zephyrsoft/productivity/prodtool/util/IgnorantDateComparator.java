package org.zephyrsoft.productivity.prodtool.util;

import java.util.*;

/**
 * Vergleicht lediglich Tag und Monat. Die Zeit und das Jahr wird völlig vernachlässigt. 
 * @author Mathis Dirksen-Thedens
 */
public class IgnorantDateComparator implements Comparator<Date> {
	
	public int compare(Date date1, Date date2) {
		if (date1==null || date2==null) {
			throw new IllegalArgumentException("argument is null");
		}
		int ret = 0;
		if (date1.getMonth() < date2.getMonth() || 
			(date1.getMonth() == date2.getMonth() && date1.getDate() < date2.getDate())) {
				// date1 ist vor date2
				ret = -1;
		} else if (date1.getMonth() > date2.getMonth() || 
			(date1.getMonth() == date2.getMonth() && date1.getDate() > date2.getDate())) {
				// date1 ist nach date2
				ret = 1;
		}
		return ret;
	}
	
	public boolean equals(Date date1, Date date2) {
		if (date1==null || date2==null) {
			throw new IllegalArgumentException("argument is null");
		}
		boolean ret = false;
		if (date1.getMonth() == date2.getMonth() && date1.getDate() == date2.getDate()) {
			ret = true;
		}
		return ret;
	}
}
