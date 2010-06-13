/**
 * 
 */
package org.zephyrsoft.palm.pdbtool;

import java.util.*;

import org.jSyncManager.API.Protocol.Util.*;
import org.jSyncManager.API.Protocol.Util.StdApps.*;
import org.zephyrsoft.palm.pdbtool.structure.*;
import org.zephyrsoft.palm.pdbtool.util.*;
import org.zephyrsoft.palm.pdbtool.util.PersonComparator.*;

/**
 * Statische Hilfsklasse zur Behandlung von Palm-PDB-Dateien.
 * 
 * @author Mathis Dirksen-Thedens
 */
public class PdbTool {
	
	public static List<Person> loadContactPdbFile(String pdbFile) {
		List<Person> ret = new ArrayList<Person>();
		
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
				List<String> phones = new ArrayList<String>();
				List<String> emails = new ArrayList<String>();
				int cnt = 0;
				for (String one : element.getPhones()) {
					if (one != null && !one.isEmpty()) {
						if (((long)element.getPhoneField(cnt)) == ContactRecord.HOME_LABEL
							|| ((long)element.getPhoneField(cnt)) == ContactRecord.WORK_LABEL
							|| ((long)element.getPhoneField(cnt)) == ContactRecord.MAIN_LABEL
							|| ((long)element.getPhoneField(cnt)) == ContactRecord.MOBILE_LABEL) {
							// Telefon
							phones.add(one);
						} else if (((long)element.getPhoneField(cnt)) == ContactRecord.EMAIL_LABEL) {
							// Email
							emails.add(one);
						}
					}
					cnt++;
				}
				person = new Person(element.getGivenName(), element.getSurname(), element.getCompany(), element.getCustomField(0), makeStringArray(phones), makeStringArray(emails), element.getCustomField(1));
			} catch(ParseException pe) {
				// tue nichts
			}
			
			if (person!=null) {
				ret.add(person);
			}
		}
		
		Collections.sort(ret, new PersonComparator(ComparationCriteria.BIRTHDAY));
		
		return ret;
	}
	
	private static String[] makeStringArray(List<String> list) {
		String[] ret = new String[list.size()];
		int cnt = 0;
		for (String str : list) {
			ret[cnt] = str;
			cnt++;
		}
		return ret;
	}
	
}
