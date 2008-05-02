/**
 * 
 */
package org.zephyrsoft.palm;

import java.io.*;
import java.net.*;
import java.util.*;

import org.zephyrsoft.palm.pdbtool.*;
import org.zephyrsoft.palm.pdbtool.structure.*;

/**
 * 
 * @author Mathis Dirksen-Thedens
 */
public class CallMonitor {
	
	private List<Person> persons = null;
	private HashMap<String, Set<Person>> number2persons = null;

	public static void main(String[] args) {
		new CallMonitor(args);
	}
	
	public CallMonitor(String[] args) {
		if (args==null || args.length==0) {
			System.err.println("Too few arguments. Please provide the path to the .pdb file to read!");
			System.exit(-1);
		} else {
			try {
				loadPdbFile(args[0]);
				ServerSocket serverSocket = new ServerSocket(7777);
				while (true) {
					Socket clientSocket = serverSocket.accept();
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String inputLine;
					if ((inputLine = in.readLine()) != null) {
						// TODO
						System.out.println(inputLine);
						
						
						
						
					}
					in.close();
					clientSocket.close();
				}
			} catch (IllegalArgumentException iae) {
				System.err.println("The given file could not be read!");
				System.exit(-2);
			} catch (IOException ioe) {
			    System.err.println("Problem with port 7777!");
				System.exit(-3);
			}
		}
	}
	
	private static final String EMPTY_STRING = "";
	private static final String SPACE = " ";
	
	private void loadPdbFile(String pdbFile) throws IllegalArgumentException {
		persons = PdbTool.loadContactPdbFile(pdbFile);
		
		// Testdaten:
//		persons = new ArrayList<Person>();
//		persons.add(new Person("Sophie", "Dirksen-Thedens", "16.4.", "0511 5334911", "", "", ""));
//		persons.add(new Person("Mathis", "Dirksen-Thedens", "19.04.81", "0511 5334911", "", "", ""));
//		persons.add(new Person("???", "Dirksen-Thedens", "22.04.2008", "0511 5334911", "", "", ""));
		
		number2persons = new HashMap<String, Set<Person>>();
		for (Person person : persons) {
			if (person.getPhone1()!=null && !person.getPhone1().equals(EMPTY_STRING)) {
				addPerson(person.getPhone1(), person);
			}
			if (person.getPhone2()!=null && !person.getPhone2().equals(EMPTY_STRING)) {
				addPerson(person.getPhone2(), person);
			}
			if (person.getPhone3()!=null && !person.getPhone3().equals(EMPTY_STRING)) {
				addPerson(person.getPhone3(), person);
			}
		}
	}
	
	private void addPerson(String number, Person person) {
		if (number2persons.get(number)==null) {
			Set<Person> newSet = new HashSet<Person>();
			newSet.add(person);
			number2persons.put(number, newSet);
		} else {
			Set<Person> oldSet = number2persons.get(number);
			oldSet.add(person);
			number2persons.put(number, oldSet);
		}
	}
	
	private static String removeSpaces(String in) {
		if (in==null) {
			return null;
		} else {
			return in.replaceAll(SPACE, EMPTY_STRING);
		}
	}

}
