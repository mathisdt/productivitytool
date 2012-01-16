package org.zephyrsoft.palm.pdbtool.util;

import java.util.*;

import org.zephyrsoft.palm.pdbtool.structure.*;

/**
 * Vergleicht Personen anhand verschiedener Kriterien.
 * @author Mathis Dirksen-Thedens
 */
public class PersonComparator implements Comparator<Person> {

	private IgnorantDateComparator dateComparator = new IgnorantDateComparator();
	private ComparationCriteria criteria = null;
	
	public PersonComparator(ComparationCriteria criteria) {
		this.criteria = criteria;
	}
	
	public int compare(Person o1, Person o2) {
		if (o1==null || o2==null) {
			throw new IllegalArgumentException("an argument is null");
		}
		if (criteria==ComparationCriteria.BIRTHDAY) {
			if (o1.getBirthday()==null) {
				return -1;
			}
			if (o2.getBirthday()==null) {
				return 1;
			}
			return dateComparator.compare(o1.getBirthday(), o2.getBirthday());
		} else if (criteria==ComparationCriteria.LASTNAME_GIVENNAME_BUSINESS) {
			String toCompare1 = (o1.getLastname()!=null ? o1.getLastname() : (o1.getGivenname()!=null ? o1.getGivenname() : o1.getBusiness()));
			String toCompare2 = (o2.getLastname()!=null ? o2.getLastname() : (o2.getGivenname()!=null ? o2.getGivenname() : o2.getBusiness()));
			int ret = compareStrings(toCompare1, toCompare2);
			if (ret!=0) {
				return ret;
			}
			ret = compareStrings(o1.getGivenname(), o2.getGivenname());
			if (ret!=0) {
				return ret;
			}
			ret = compareStrings(o1.getBusiness(), o2.getBusiness());
			if (ret!=0) {
				return ret;
			}
			ret = compareInts(o1.getReihenfolge(), o2.getReihenfolge());
			return ret;
		} else {
			throw new IllegalArgumentException("this criteria is not comparable (yet)");
		}
	}
	
	private static int compareStrings(String s1, String s2) {
		if (s1==null) {
			return -1;
		} else if (s2==null) {
			return 1;
		} else {
			return s1.compareTo(s2);
		}
	}
	
	private static int compareInts(int i1, int i2) {
		if (i1>i2) {
			return 1;
		} else if (i1<i2) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public enum ComparationCriteria {
		BIRTHDAY,
		LASTNAME_GIVENNAME_BUSINESS
	}
}
