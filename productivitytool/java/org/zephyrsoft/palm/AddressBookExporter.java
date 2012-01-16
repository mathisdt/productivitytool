package org.zephyrsoft.palm;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import org.zephyrsoft.palm.pdbtool.*;
import org.zephyrsoft.palm.pdbtool.structure.*;
import org.zephyrsoft.palm.pdbtool.structure.Person.Address;
import org.zephyrsoft.palm.pdbtool.util.*;
import org.zephyrsoft.palm.pdbtool.util.PersonComparator.*;

/**
 * @author Mathis Dirksen-Thedens
 */
public class AddressBookExporter {
	
	private List<Person> persons = null;
	
	public AddressBookExporter(String[] args) {
		if (args == null || args.length == 0) {
			System.err.println("Too few arguments. Please provide the path to the .pdb file to read!");
			System.exit(-1);
		} else {
			try {
				loadPdbFile(args[0]);
				exportAddressList();
				System.exit(0);
			} catch (IllegalArgumentException iae) {
				System.err.println("The given file could not be read!");
				System.exit(-2);
			}
		}
	}
	
	private void loadPdbFile(String pdbFile) throws IllegalArgumentException {
		persons = PdbTool.loadContactPdbFile(pdbFile);
		
		// Testdaten:
		// persons = new ArrayList<Person>();
		// persons.add(new Person("Mathis", "Dirksen-Thedens", "19.04.81", new String[] {"0511 5334911",
		// "0178 6886872"}, null, "1"));
		// persons.add(new Person("Sophie", "Dirksen-Thedens", "16.4.81", new String[] {"0511 5334911", "0178 1573737"},
		// null, "2"));
		// persons.add(new Person("Sarah", "Dirksen-Thedens", "07.06.08", new String[] {"0511 5334911"}, null, "3"));
		
		// jetzt noch sortieren nach 1. Nachname, 2. Vorname, 3. Firma
		Collections.sort(persons, new PersonComparator(ComparationCriteria.LASTNAME_GIVENNAME_BUSINESS));
	}
	
	private void exportAddressList() {
		StringBuilder ret = new StringBuilder("Name\tStraße\tPLZ\tOrt");
		
		for (Person person : persons) {
			for (Address address : person.getAdresses()) {
				ret.append("\n")
					.append((isNotEmpty(person.getGivenname()) ? person.getGivenname() : ""))
					.append((isNotEmpty(person.getGivenname()) && isNotEmpty(person.getLastname()) ? " " : ""))
					.append((isNotEmpty(person.getLastname()) ? person.getLastname() : ""))
					.append(
						((isNotEmpty(person.getGivenname()) || isNotEmpty(person.getLastname()))
							&& isNotEmpty(person.getBusiness()) ? " / " : ""))
					.append((isNotEmpty(person.getBusiness()) ? person.getBusiness() : ""));
				ret.append("\t").append(cleanUp(address.getStreet()))
				.append("\t").append(cleanUp(address.getZipCode()))
				.append("\t").append(cleanUp(address.getCity()));
			}
		}
		
		String userhome = System.getProperty("user.home", "");
		File output =
			new File(userhome + (userhome.endsWith(File.separator) ? "" : File.separator) + "addressbook.txt");
		
		FileWriter out;
		try {
			out = new FileWriter(output, false);
			out.append(ret.toString());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean isEmpty(String string) {
		return (string == null || string.equals(""));
	}
	
	private static boolean isNotEmpty(String string) {
		return !isEmpty(string);
	}
	
	private static String cleanUp(String in) {
		if (in==null) {
			return "";
		}
		in = in.replaceAll(";", ",");
		in = in.replaceAll("\t", " ");
		in = in.replaceAll("\r", "");
		in = in.replaceAll("\n", " / ");
		return in;
	}
	
	public static void main(String[] args) {
		new AddressBookExporter(args);
	}
	
}
