package org.zephyrsoft.palm;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.swing.*;

import org.zephyrsoft.palm.pdbtool.*;
import org.zephyrsoft.palm.pdbtool.structure.*;
import org.zephyrsoft.palm.pdbtool.util.*;
import org.zephyrsoft.palm.pdbtool.util.PersonComparator.*;

/**
 * 
 * @author Mathis Dirksen-Thedens
 */
public class PhoneBookExporter {
	
	private List<Person> persons = null;
	
	public PhoneBookExporter(String[] args) {
		if (args==null || args.length==0) {
			System.err.println("Too few arguments. Please provide the path to the .pdb file to read!");
			System.exit(-1);
		} else {
			try {
				loadPdbFile(args[0]);
				exportPhoneList();
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
//		persons = new ArrayList<Person>();
//		persons.add(new Person("Mathis", "Dirksen-Thedens", "19.04.81", new String[] {"0511 5334911", "0178 6886872"}, null, "1"));
//		persons.add(new Person("Sophie", "Dirksen-Thedens", "16.4.81", new String[] {"0511 5334911", "0178 1573737"}, null, "2"));
//		persons.add(new Person("Sarah", "Dirksen-Thedens", "07.06.08", new String[] {"0511 5334911"}, null, "3"));
		
		// jetzt noch sortieren nach 1. Nachname, 2. Vorname, 3. Firma
		Collections.sort(persons, new PersonComparator(ComparationCriteria.LASTNAME_GIVENNAME_BUSINESS));
	}
	
	private void exportPhoneList() {
		StringBuilder ret = new StringBuilder("<html><head><title>Telefonliste</title></head><body><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n\n");
		
		String leftrightpadding = "15px";
		
		boolean makeGrey = false;
		for (Person person : persons) {
			ret.append("<tr" + (makeGrey ? " style=\"background-color:#cccccc\"" : "") + "><td nowrap=\"nowrap\" style=\"padding-right:25px;padding-left:" + leftrightpadding + "\">")
				.append((isNotEmpty(person.getGivenname()) ? person.getGivenname() : ""))
				.append((isNotEmpty(person.getGivenname()) && isNotEmpty(person.getLastname()) ? " " : ""))
				.append((isNotEmpty(person.getLastname()) ? person.getLastname() : ""))
				.append(((isNotEmpty(person.getGivenname()) || isNotEmpty(person.getLastname())) && isNotEmpty(person.getBusiness()) ? " / " : ""))
				.append((isNotEmpty(person.getBusiness()) ? person.getBusiness() : ""))
				.append("</td>");
			int colsLeft = 7;
			for (String phone : person.getPhonesArray()) {
				ret.append("<td nowrap=\"nowrap\" style=\"padding-right:" + leftrightpadding + "\">")
					.append(phone)
					.append("</td>");
				colsLeft--;
			}
			ret.append("<td colspan=\"" + colsLeft + "\"></td>");
			ret.append("</tr>\n");
			makeGrey = !makeGrey;
		}
		
		ret.append("\n\n</table></body></html>");
		
		String userhome = System.getProperty("user.home", "");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");
		String datestamp = sdf.format(new Date());
		File output = new File(userhome + (userhome.endsWith(File.separator) ? "" : File.separator) + "phonebook" /* + "-" + datestamp */ + ".html");
		
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
		return (string==null || string.equals(""));
	}
	
	private static boolean isNotEmpty(String string) {
		return !isEmpty(string);
	}
	
	public static void main(String[] args) {
		new PhoneBookExporter(args);
	}
	
}
