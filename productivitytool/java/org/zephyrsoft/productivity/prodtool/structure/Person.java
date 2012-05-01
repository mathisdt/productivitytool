package org.zephyrsoft.productivity.prodtool.structure;

import java.text.*;
import java.util.*;

public class Person {
	
	private static final String SEPARATOR = ", ";
	private static final String EMPTY_STRING = "";
	
	private String givenname = null;
	private String lastname = null;
	private String business = null;
	private Date birthday = null;
	private List<String> phone = new ArrayList<String>();
	private List<String> email = new ArrayList<String>();
	private int reihenfolge = 0;
	private Set<Person.Address> addresses = new HashSet<Person.Address>();
	
	private static Date curDate = new Date();
	private static SimpleDateFormat onlyYear = new SimpleDateFormat("yyyy");
	
	public Person() {
		// alle Felder bleiben leer
	}
	
	/**
	 * Konstruktor mit dem Geburtstag als Date.
	 */
	public Person(String givenname, String lastname, String business, Date birthday, String[] phone, String[] email,
		String reihenfolge) {
		setGivenname(givenname);
		setLastname(lastname);
		setBusiness(business);
		setBirthday(birthday);
		if (phone != null) {
			for (String one : phone) {
				if (!isEmpty(one)) {
					addPhone(one);
				}
			}
		}
		if (email != null) {
			for (String one : email) {
				if (!isEmpty(one)) {
					addEmail(one);
				}
			}
		}
		setReihenfolge(correctReihenfolge(reihenfolge));
	}
	
	/**
	 * Konstruktor mit dem Geburtstag als String (wird intern geparst, erwartetes Format: D.M.Y mit Y zwei- oder
	 * vierstellig optional).
	 */
	public Person(String givenname, String lastname, String business, String birthday, String[] phone, String[] email,
		String reihenfolge) {
		this(givenname, lastname, business, makeDateOrNull(correctDate(birthday)), phone, email, reihenfolge);
	}
	
	@Override
	public String toString() {
		return (isNotEmpty(getGivenname()) ? getGivenname() : "")
			+ (isNotEmpty(getGivenname()) && isNotEmpty(getLastname()) ? " " : "")
			+ (isNotEmpty(getLastname()) ? getLastname() : "")
			+ ((isNotEmpty(getGivenname()) || isNotEmpty(getLastname())) && isNotEmpty(getBusiness()) ? " / " : "")
			+ (isNotEmpty(getBusiness()) ? getBusiness() : "");
	}
	
	private int correctReihenfolge(String reihenfolgeString) {
		if (reihenfolgeString == null) {
			return 0;
		}
		int rf = 0;
		try {
			rf = Integer.parseInt(reihenfolgeString);
		} catch (NumberFormatException nfe) {
			// tue nichts, der Wert von rf wurde schon oben auf 0 gesetzt
		}
		return rf;
	}
	
	private static Date makeDateOrNull(GregorianCalendar cal) {
		if (cal == null) {
			return null;
		} else {
			return cal.getTime();
		}
	}
	
	/**
	 * Parst das Datum aus dem String. Erwartetes Format: DD.MM.YYYY - wobei das Jahr optional ist
	 * und auch nur zweistellig sein kann (dann wird 19.. angenommen) und die Länge
	 * der Tages- und Monatsfelder auch nur 1 sein kann.
	 * Wenn das Datum nicht herausfindbar ist (falsches Format, leer o.ä.), wird der 01.01.2200 zurückgegeben.
	 * Wenn nur das Jahr fehlt, wird es auf 2200 gesetzt, der Rest des Datums ist aber entsprechend der Eingabe.
	 */
	private static GregorianCalendar correctDate(String datum) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.YEAR, 2200);
		if (datum == null || datum.equals("") || datum.indexOf(".") == datum.lastIndexOf(".")) {
			// nicht zu vergleichen:
			cal = null;
		} else {
			// Tag, Monat und Jahr setzen:
			try {
				cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datum.substring(0, datum.indexOf("."))));
				cal.set(Calendar.MONTH,
					Integer.parseInt(datum.substring(datum.indexOf(".") + 1, datum.lastIndexOf("."))) - 1);
				String yr = datum.substring(datum.lastIndexOf(".") + 1);
				if (yr.length() == 2) {
					String curYr = onlyYear.format(curDate).substring(2, 4);
					String curCt = onlyYear.format(curDate).substring(0, 2);
					if (Integer.parseInt(yr) <= Integer.parseInt(curYr)) {
						cal.set(Calendar.YEAR, Integer.parseInt(curCt + yr));
					} else {
						String lastCt = String.valueOf(Integer.parseInt(curCt) - 1);
						cal.set(Calendar.YEAR, Integer.parseInt(lastCt + yr));
					}
				} else if (yr.length() == 4) {
					cal.set(Calendar.YEAR, Integer.parseInt(yr));
				}
			} catch (Exception e) {
				System.err.println("ignored following exception:");
				e.printStackTrace();
				cal = null;
			}
		}
		return cal;
	}
	
	public String getContactPossibilities() {
		String phones = getPhones();
		String emails = getEmails();
		String ret = phones;
		if (!isEmpty(phones) && !isEmpty(emails)) {
			ret += SEPARATOR;
		}
		ret += emails;
		return ret;
	}
	
	public String getPhones() {
		StringBuilder ret = new StringBuilder();
		boolean isFirst = true;
		for (String one : phone) {
			if (!isEmpty(one)) {
				if (isFirst) {
					isFirst = false;
				} else {
					ret.append(SEPARATOR);
				}
				ret.append(one);
			}
		}
		return ret.toString();
	}
	
	public String getEmails() {
		StringBuilder ret = new StringBuilder();
		boolean isFirst = true;
		for (String one : email) {
			if (!isEmpty(one)) {
				if (isFirst) {
					isFirst = false;
				} else {
					ret.append(SEPARATOR);
				}
				ret.append(one);
			}
		}
		return ret.toString();
	}
	
	public String[] getPhonesArray() {
		return phone.toArray(new String[phone.size()]);
	}
	
	public int getPhoneCount() {
		return phone.size();
	}
	
	public int getEmailCount() {
		return email.size();
	}
	
	private static boolean isEmpty(String string) {
		return (string == null || string.equals(""));
	}
	
	private static boolean isNotEmpty(String string) {
		return !isEmpty(string);
	}
	
	private static boolean isEmpty(StringBuilder sb) {
		return (sb == null || sb.length() == 0);
	}
	
	public String getGivenname() {
		return givenname;
	}
	
	public void setGivenname(String givenname) {
		this.givenname = givenname;
	}
	
	public String getLastname() {
		return lastname;
	}
	
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public Date getBirthday() {
		return birthday;
	}
	
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	public void addPhone(String one) {
		phone.add(one);
	}
	
	public String getPhone(int index) {
		return phone.get(index);
	}
	
	public void addEmail(String one) {
		email.add(one);
	}
	
	public String getEmail(int index) {
		return email.get(index);
	}
	
	public int getReihenfolge() {
		return reihenfolge;
	}
	
	public void setReihenfolge(int reihenfolge) {
		this.reihenfolge = reihenfolge;
	}
	
	public String getBusiness() {
		return business;
	}
	
	public void setBusiness(String business) {
		this.business = business;
	}
	
	public static class Address {
		private String street;
		private String zipCode;
		private String city;
		private Type type;
		
		public Address(String street, String zipCode, String city, Type type) {
			this.street = street;
			this.zipCode = zipCode;
			this.city = city;
			this.type = type;
		}
		
		public String getStreet() {
			return street;
		}
		
		public String getZipCode() {
			return zipCode;
		}
		
		public String getCity() {
			return city;
		}
		
		public Type getType() {
			return type;
		}
		
		public enum Type {
			WORK, HOME, OTHER;
		}
	}
	
	public boolean isAddressesEmpty() {
		return addresses.isEmpty();
	}
	
	public boolean addAddress(Address e) {
		return addresses.add(e);
	}
	
	public Iterator<Address> addressesIterator() {
		return addresses.iterator();
	}
	
	public Set<Address> getAdresses() {
		return addresses;
	}
	
}
