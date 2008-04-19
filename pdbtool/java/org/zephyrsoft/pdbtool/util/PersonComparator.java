package org.zephyrsoft.pdbtool.util;

import java.util.*;

import org.zephyrsoft.pdbtool.structure.*;

/**
 * Vergleicht Personen anhand des Geburtstages.
 * @author Mathis Dirksen-Thedens
 */
public class PersonComparator implements Comparator<Person> {

	private IgnorantDateComparator dateComparator = new IgnorantDateComparator();
	
	public int compare(Person o1, Person o2) {
		if (o1==null || o2==null) {
			throw new IllegalArgumentException("argument is null");
		}
		if (o1.getBirthday()==null) {
			return -1;
		}
		if (o2.getBirthday()==null) {
			return 1;
		}
		return dateComparator.compare(o1.getBirthday(), o2.getBirthday());
	}
}
