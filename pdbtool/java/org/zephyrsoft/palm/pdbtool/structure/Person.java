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
	private String phone4 = null;
	private String phone5 = null;
	private String phone6 = null;
	private String phone7 = null;
	private String email1 = null;
	private String email2 = null;
	private String email3 = null;
	private String email4 = null;
	private String email5 = null;
	private String email6 = null;
	private String email7 = null;
	private int reihenfolge = 0;
	
	private static Date curDate = new Date();
	private static SimpleDateFormat onlyYear = new SimpleDateFormat("yyyy");
	
	
	/**
	 * Konstruktor mit dem Geburtstag als Date.
	 */
	public Person(String givenname, String lastname, Date birthday, String[] phone, String[] email, String reihenfolge) {
		setGivenname(givenname);
		setLastname(lastname);
		setBirthday(birthday);
		int usedPhone = 0;
		for (String one : phone) {
			if (!isEmpty(one)) {
				switch (usedPhone) {
					case 0:
						setPhone1(one);
						usedPhone++;
						break;
					case 1:
						setPhone2(one);
						usedPhone++;
						break;
					case 2:
						setPhone3(one);
						usedPhone++;
						break;
					case 3:
						setPhone4(one);
						usedPhone++;
						break;
					case 4:
						setPhone5(one);
						usedPhone++;
						break;
					case 5:
						setPhone6(one);
						usedPhone++;
						break;
					case 6:
						setPhone7(one);
						usedPhone++;
						break;
					default:
						break;
				}
			}
		}
		int usedEmail = 0;
		for (String one : email) {
			if (!isEmpty(one)) {
				switch (usedEmail) {
					case 0:
						setEmail1(one);
						usedEmail++;
						break;
					case 1:
						setEmail2(one);
						usedEmail++;
						break;
					case 2:
						setEmail3(one);
						usedEmail++;
						break;
					case 3:
						setEmail4(one);
						usedEmail++;
						break;
					case 4:
						setEmail5(one);
						usedEmail++;
						break;
					case 5:
						setEmail6(one);
						usedEmail++;
						break;
					case 6:
						setEmail7(one);
						usedEmail++;
						break;
					default:
						break;
				}
			}
		}
		setReihenfolge(correctReihenfolge(reihenfolge));
	}
	
	/**
	 * Konstruktor mit dem Geburtstag als String (wird intern geparst, erwartetes Format: D.M.Y mit Y zwei- oder vierstellig optional).
	 */
	public Person(String givenname, String lastname, String birthday, String[] phone, String[] email, String reihenfolge) {
		this(givenname, lastname, makeDateOrNull(correctDate(birthday)), phone, email, reihenfolge);
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
	
	private static Date makeDateOrNull(GregorianCalendar cal) {
		if (cal==null) {
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
		if (!isEmpty(getPhone4())) {
			if (!isEmpty(ret)) {
				ret.append(SEPARATOR);
			}
			ret.append(getPhone4());
		}
		if (!isEmpty(getPhone5())) {
			if (!isEmpty(ret)) {
				ret.append(SEPARATOR);
			}
			ret.append(getPhone5());
		}
		if (!isEmpty(getPhone6())) {
			if (!isEmpty(ret)) {
				ret.append(SEPARATOR);
			}
			ret.append(getPhone6());
		}
		if (!isEmpty(getPhone7())) {
			if (!isEmpty(ret)) {
				ret.append(SEPARATOR);
			}
			ret.append(getPhone7());
		}
		// jetzt Email-Adressen
		if (!isEmpty(getEmail1())) {
			if (!isEmpty(ret)) {
				ret.append(SEPARATOR);
			}
			ret.append(getEmail1());
		}
		if (!isEmpty(getEmail2())) {
			if (!isEmpty(ret)) {
				ret.append(SEPARATOR);
			}
			ret.append(getEmail2());
		}
		if (!isEmpty(getEmail3())) {
			if (!isEmpty(ret)) {
				ret.append(SEPARATOR);
			}
			ret.append(getEmail3());
		}
		if (!isEmpty(getEmail4())) {
			if (!isEmpty(ret)) {
				ret.append(SEPARATOR);
			}
			ret.append(getEmail4());
		}
		if (!isEmpty(getEmail5())) {
			if (!isEmpty(ret)) {
				ret.append(SEPARATOR);
			}
			ret.append(getEmail5());
		}
		if (!isEmpty(getEmail6())) {
			if (!isEmpty(ret)) {
				ret.append(SEPARATOR);
			}
			ret.append(getEmail6());
		}
		if (!isEmpty(getEmail7())) {
			if (!isEmpty(ret)) {
				ret.append(SEPARATOR);
			}
			ret.append(getEmail7());
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

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public int getReihenfolge() {
		return reihenfolge;
	}

	public void setReihenfolge(int reihenfolge) {
		this.reihenfolge = reihenfolge;
	}

	public String getPhone4() {
		return phone4;
	}

	public void setPhone4(String phone4) {
		this.phone4 = phone4;
	}

	public String getPhone5() {
		return phone5;
	}

	public void setPhone5(String phone5) {
		this.phone5 = phone5;
	}

	public String getPhone6() {
		return phone6;
	}

	public void setPhone6(String phone6) {
		this.phone6 = phone6;
	}

	public String getPhone7() {
		return phone7;
	}

	public void setPhone7(String phone7) {
		this.phone7 = phone7;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getEmail3() {
		return email3;
	}

	public void setEmail3(String email3) {
		this.email3 = email3;
	}

	public String getEmail4() {
		return email4;
	}

	public void setEmail4(String email4) {
		this.email4 = email4;
	}

	public String getEmail5() {
		return email5;
	}

	public void setEmail5(String email5) {
		this.email5 = email5;
	}

	public String getEmail6() {
		return email6;
	}

	public void setEmail6(String email6) {
		this.email6 = email6;
	}

	public String getEmail7() {
		return email7;
	}

	public void setEmail7(String email7) {
		this.email7 = email7;
	}
	
}
