package org.zephyrsoft.pdbtool;

import java.util.*;

import javax.swing.*;

import org.jSyncManager.API.Protocol.Util.*;
import org.jSyncManager.API.Protocol.Util.StdApps.*;
import org.zephyrsoft.pdbtool.structure.*;
import org.zephyrsoft.pdbtool.util.*;

public class Birthday {
	
	/** Diese Anzahl von Tagen wird im Voraus und im Nachhinein angezeigt. */
	private static final int DAYS_TO_CONSIDER = 4;
	private static String[] dayNames = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"};
	
	private final Date today = new Date();
	
	private List<Person> persons = null;
	
	public Birthday(String[] args) {
		if (args==null || args.length==0) {
			System.err.println("Too few arguments. Please provide the path to the .pdb file to read!");
			System.exit(-1);
		} else {
			try {
				loadPdbFile(args[0]);
				displayDueBirthdays();
				System.exit(0);
			} catch (IllegalArgumentException iae) {
				System.err.println("The given file could not be read!");
				System.exit(-2);
			}
		}
	}
	
	private void loadPdbFile(String pdbFile) throws IllegalArgumentException {
		persons = new ArrayList<Person>();
		
		// Testdaten:
//		persons.add(new Person("Sophie", "Dirksen-Thedens", "16.4.", "0511 5334911", "", "", ""));
//		persons.add(new Person("Mathis", "Dirksen-Thedens", "19.04.81", "0511 5334911", "", "", ""));
//		persons.add(new Person("???", "Dirksen-Thedens", "22.04.2008", "0511 5334911", "", "", ""));
		
		DLPDatabase db = null;
		try {
		   db = DLPDatabase.importFromFile(pdbFile);
		} catch (Exception e) {
			throw new IllegalArgumentException("given file could not be read");
		}
		
		for (int i = 0; i < db.getElements(); i++) {
			Person person = null;
			try {
				ContactRecord element = new ContactRecord((DLPRecord)db.getElement(i));
				person = new Person(element.getGivenName(), element.getSurname(), element.getCustomField(0), element.getPhones(0), element.getPhones(1), element.getPhones(2), element.getPhones(3));
			} catch(ParseException pe) {
				// tue nichts
			}
			
			if (person!=null) {
				persons.add(person);
			}
		}
		
		Collections.sort(persons, new PersonComparator());
	}
	
	private void displayDueBirthdays() {
		if (persons==null) {
			return;
		}
		GregorianCalendar cal = new GregorianCalendar();
		Date now = cal.getTime();
		cal.add(Calendar.DATE, -1 * DAYS_TO_CONSIDER);
		Date begin = cal.getTime();
		cal.add(Calendar.DATE, 2 * DAYS_TO_CONSIDER);
		Date end = cal.getTime();
		IgnorantDateComparator dateComparator = new IgnorantDateComparator();
		StringBuilder toPrint = new StringBuilder("<html>");
		String beginMark1 = "<font color=\"";
		String beginMark2 = "\">";
		String endMark = "</font>";
		String breakMark = "<br>";
		String ageBegin = " [";
		String ageEnd = "]";
		String colon = ": ";
		String comma = ", ";
		String point = ".";
		String space = " ";
		String the = "den ";
		String contactBegin = " (";
		String contactEnd = ")";
		
		for (Person person : persons) {
			if (person.getBirthday()!=null && dateComparator.compare(begin, person.getBirthday())<=0 && dateComparator.compare(person.getBirthday(), end)<=0) {
				// Geburtstag der Person liegt im gewünschten Zeitfenster
				ColorMarkEnum colorMark = ColorMarkEnum.DATE_IS_TODAY;
				if (dateComparator.compare(person.getBirthday(), now)<0) {
					colorMark = ColorMarkEnum.DATE_IS_BEFORE;
				} else if (dateComparator.compare(now, person.getBirthday())<0) {
					colorMark = ColorMarkEnum.DATE_IS_AFTER;
				}
				
				toPrint.append(beginMark1)
					.append(colorMark.getColor())
					.append(beginMark2);
				if (person.getGivenname()!=null) {
					toPrint.append(person.getGivenname())
						.append(space);
				}
				toPrint.append(person.getLastname());
				if ((person.getBirthday().getYear()+1900)!=2200) {
					toPrint.append(ageBegin)
						.append(calculateAge(person.getBirthday()))
						.append(ageEnd);
				}
				toPrint.append(colon)
					.append(getDayNameThisYear(person.getBirthday()))
					.append(comma)
					.append(the)
					.append(person.getBirthday().getDate())
					.append(point)
					.append((person.getBirthday().getMonth()+1))
					.append(point);
				if ((person.getBirthday().getYear()+1900)!=2200) {
					toPrint.append((person.getBirthday().getYear()+1900));
				}
				toPrint.append(endMark);
				String contactPossibilities = person.getContactPossibilities();
				if (contactPossibilities!=null && contactPossibilities.length()>0) {
					toPrint.append(beginMark1)
						.append(ColorMarkEnum.CONTACT_POSSIBILITIES.getColor())
						.append(beginMark2)
						.append(contactBegin)
						.append(contactPossibilities)
						.append(contactEnd);
				}
				toPrint.append(breakMark);
			}
		}
		
		toPrint.append("</html>");

		// Meldung ausgeben:
		JOptionPane.showMessageDialog(null, toPrint.toString(), "Geburtstage", javax.swing.JOptionPane.PLAIN_MESSAGE);
	}
	
	public static void main(String[] args) {
		new Birthday(args);
	}
	
	private int calculateAge(Date date) {
		return today.getYear()-date.getYear();
	}
	
	private String getDayNameThisYear(Date date) {
		Date myDate = (Date)date.clone();
		myDate.setYear(today.getYear());
		return getDayName(myDate.getDay());
	}
	
	private static String getDayName(int dayofweek) {
		if (dayofweek == Calendar.MONDAY) {
			return dayNames[0];
		} else if (dayofweek == Calendar.TUESDAY) {
			return dayNames[1];
		} else if (dayofweek == Calendar.WEDNESDAY) {
			return dayNames[2];
		} else if (dayofweek == Calendar.THURSDAY) {
			return dayNames[3];
		} else if (dayofweek == Calendar.FRIDAY) {
			return dayNames[4];
		} else if (dayofweek == Calendar.SATURDAY) {
			return dayNames[5];
		} else {
			return dayNames[6];
		}
	}
	
	/** Enthält die Farben, um die Geburtstage zu markieren */
	private enum ColorMarkEnum {
		DATE_IS_BEFORE("#9C2828"),
		DATE_IS_TODAY("#0E8214"),
		DATE_IS_AFTER("#000000"),
		CONTACT_POSSIBILITIES("#7f7f7f");
		
		private String color = null;
		
		private ColorMarkEnum(String colorString) {
			setColor(colorString);
		}

		public String getColor() {
			return color;
		}

		private void setColor(String color) {
			this.color = color;
		}
	}
}
