package org.zephyrsoft.palm.pdbtool.structure;

import java.text.*;
import java.util.*;

public class Person {
	
	private static final String SEPARATOR = ", ";
	private static final String EMPTY_STRING = "";
	
	private String givenname = null;
	private String lastname = null;
	private Date birthday = null;
	private String phone1 = null;
	private String phone2 = null;
	private String phone3 = null;
	private String email = null;
	private int reihenfolge = 0;
	
	private Date curDate = new Date();
	private SimpleDateFormat onlyYear = new SimpleDateFormat("yyyy");
	
	
	/**
	 * Konstruktor mit dem Geburtstag als Date.
	 */
	public Person(String givenname, String lastname, Date birthday, String phone1, String phone2, String phone3, String email, String reihenfolge) {
		setGivenname(givenname);
		setLastname(lastname);
		setBirthday(birthday);
		setPhone1(phone1);
		setPhone2(phone2);
		setPhone3(phone3);
		setEmail(email);
		setReihenfolge(correctReihenfolge(reihenfolge));
	}
	
	/**
	 * Konstruktor mit dem Geburtstag als String (wird intern geparst, erwartetes Format: D.M.Y mit Y zwei- oder vierstellig optional).
	 */
	public Person(String givenname, String lastname, String birthday, String phone1, String phone2, String phone3, String email, String reihenfolge) {
		setGivenname(givenname);
		setLastname(lastname);
		GregorianCalendar cal = correctDate(birthday);
		if (cal!=null) {
			setBirthday(cal.getTime());
		}
		setPhone1(phone1);
		setPhone2(phone2);
		setPhone3(phone3);
		setEmail(email);
		setReihenfolge(correctReihenfolge(reihenfolge));
	}
	
	private int correctReihenfolge(String reihenfolgeString) {
		if (reihenfolgeString==null) {
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
	
	/**
	 * Parst das Datum aus dem String. Erwartetes Format: DD.MM.YYYY - wobei das Jahr optional ist
	 * und auch nur zweistellig sein kann (dann wird 19.. angenommen) und die Länge 
	 * der Tages- und Monatsfelder auch nur 1 sein kann. 
	 * Wenn das Datum nicht herausfindbar ist (falsches Format, leer o.ä.), wird der 01.01.2200 zurückgegeben.
	 * Wenn nur das Jahr fehlt, wird es auf 2200 gesetzt, der Rest des Datums ist aber entsprechend der Eingabe.
	 */
	private GregorianCalendar correctDate(String datum) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.YEAR, 2200);
		if ( datum==null || datum.equals("") || datum.indexOf(".")==datum.lastIndexOf(".") ) {
			// nicht zu vergleichen:
			cal = null;
		} else {
			// Tag, Monat und Jahr setzen:
			try {
				cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datum.substring(0,datum.indexOf("."))) );
				cal.set(Calendar.MONTH, Integer.parseInt(datum.substring(datum.indexOf(".")+1,datum.lastIndexOf(".")))-1);
				String yr = datum.substring(datum.lastIndexOf(".")+1);
				if (yr.length()==2) {
					String curYr = onlyYear.format(curDate).substring(2, 4);
					String curCt = onlyYear.format(curDate).substring(0, 2);
					if (Integer.parseInt(yr) <= Integer.parseInt(curYr)) {
						cal.set(Calendar.YEAR, Integer.parseInt(curCt + yr));
					} else {
						String lastCt = String.valueOf(Integer.parseInt(curCt) - 1);
						cal.set(Calendar.YEAR, Integer.parseInt(lastCt + yr));
					}
				} else if (yr.length()==4) {
					cal.set(Calendar.YEAR, Integer.parseInt(yr));
				}
			} catch(Exception e) {
				System.err.println("ignored following exception:");
				e.printStackTrace();
				cal = null;
			}
		}
		return cal;
	}
	
	public String getContactPossibilities() {
		StringBuilder ret = new StringBuilder();
		if (!isEmpty(getPhone1())) {
			ret.append(getPhone1());
		}
		if (!isEmpty(getPhone2())) {
			if (!isEmpty(ret)) {
				ret.append(SEPARATOR);
			}
			ret.append(getPhone2());
		}
		if (!isEmpty(getPhone3())) {
			if (!isEmpty(ret)) {
				ret.append(SEPARATOR);
			}
			ret.append(getPhone3());
		}
		// nur wenn keine Telefonnummern vorhanden sind
		if (isEmpty(ret) && !isEmpty(getEmail())) {
			if (!isEmpty(ret)) {
				ret.append(SEPARATOR);
			}
			ret.append(getEmail());
		}
		return ret.toString();
	}
	
	private static boolean isEmpty(String string) {
		return (string==null || string.equals(EMPTY_STRING));
	}
	
	private static boolean isEmpty(StringBuilder sb) {
		return (sb==null || sb.length()==0);
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

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getPhone3() {
		return phone3;
	}

	public void setPhone3(String phone3) {
		this.phone3 = phone3;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getReihenfolge() {
		return reihenfolge;
	}

	public void setReihenfolge(int reihenfolge) {
		this.reihenfolge = reihenfolge;
	}
	
}
