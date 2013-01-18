package org.zephyrsoft.productivity;

import java.util.*;
import org.zephyrsoft.productivity.prodtool.structure.*;

public class PhoneNumberAnalyer {
	
	private static final String DELIMITER = "|";
	private static final String AND = " + ";
	private static final String EMPTY_STRING = "";
	private static final String SPACE = " ";
	private static final String EQUAL = "=";
	private static final String BRACKET1 = "(";
	private static final String BRACKET2 = ")";
	
	public static void main(String[] args) {
		if (args==null || args.length==0) {
			System.err.println("Too few arguments. Please provide the path to the .pdb file to read!");
			System.exit(-1);
		} else {
			Object[] data = CallMonitor.loadPdbFile(args[0]);
			List<Person> persons = (List<Person>)data[0];
			HashMap<String, Set<Person>> number2persons = (HashMap<String, Set<Person>>)data[1];
			
			for (String number : number2persons.keySet()) {
				Set<Person> personsFromPdb = number2persons.get(number);
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(number).append(EQUAL);
				boolean isFirst = true;
				Person person = null;
				for (Iterator<Person> iter = personsFromPdb.iterator(); iter.hasNext(); ) {
					Person prevPerson = person;
					person = iter.next();
					if (isFirst) {
						isFirst = false;
						prevPerson = person;
					} else {
						stringBuilder.append(AND);
					}
					stringBuilder.append(person.getGivenname());
					if (!iter.hasNext() || !eq(prevPerson.getLastname(), person.getLastname())) {
						stringBuilder.append(SPACE);
						stringBuilder.append(person.getLastname());
					}
					stringBuilder.append(BRACKET1).append(person.getReihenfolge()).append(BRACKET2);
				}
				System.out.println(stringBuilder.toString());
			}
		}
	}
	
	private static boolean eq(String s1, String s2) {
		if ((s1==null && s2!=null) || (s1!=null && s2==null)) {
			return false;
		} else if (s1==null && s2==null) {
			return true;
		} else {
			// beide Strings sind ungleich null
			return s1.equals(s2);
		}
	}
	
}
