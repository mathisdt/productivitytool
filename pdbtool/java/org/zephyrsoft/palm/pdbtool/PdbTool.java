/**
 * 
 */
package org.zephyrsoft.palm.pdbtool;

import java.util.*;

import org.jSyncManager.API.Protocol.Util.*;
import org.jSyncManager.API.Protocol.Util.StdApps.*;
import org.zephyrsoft.palm.pdbtool.structure.*;
import org.zephyrsoft.palm.pdbtool.util.*;

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
				person = new Person(element.getGivenName(), element.getSurname(), element.getCustomField(0), element.getPhones(0), element.getPhones(1), element.getPhones(2), element.getPhones(3), element.getCustomField(1));
			} catch(ParseException pe) {
				// tue nichts
			}
			
			if (person!=null) {
				ret.add(person);
			}
		}
		
		Collections.sort(ret, new PersonComparator());
		
		return ret;
	}
	
}
