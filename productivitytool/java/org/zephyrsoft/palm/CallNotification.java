/**
 * 
 */
package org.zephyrsoft.palm;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.List;

import javax.imageio.*;
import javax.swing.*;

import org.zephyrsoft.palm.pdbtool.*;
import org.zephyrsoft.palm.pdbtool.structure.*;

/**
 * 
 * @author Mathis Dirksen-Thedens
 */
public class CallNotification {
	
	private List<Person> persons = null;
	private HashMap<String, Set<Person>> number2persons = null;
	
	private Charset charset = Charset.forName("ISO-8859-1");

	public static void main(String[] args) {
		new CallNotification(args);
	}
	
	private static final String DELIMITER = "|";
	private static final String AND = " + ";
	private static final String EMPTY_STRING = "";
	private static final String SPACE = " ";
	private static final String COMMA = ",";
	
	public CallNotification(String[] args) {
		if (args==null || args.length==0) {
			System.err.println("Too few arguments. Please provide the path to the .pdb file to read!");
			System.exit(-1);
		} else {
			try {
//				setupTrayIcon();
				Object[] data = loadPdbFile(args[0]);
				persons = (List<Person>)data[0];
				number2persons = (HashMap<String, Set<Person>>)data[1];
				if (args!=null && args.length>=2) {
					// expected format: "0123456789"
					String number = null;
					StringBuilder nameBuilderFromPdb = new StringBuilder();
					number = removeSpaces(args[1]);
					Set<Person> personsFromPdb = number2persons.get(number);
					if (personsFromPdb!=null) {
						boolean isFirst = true;
						Person person = null;
						for (Iterator<Person> iter = personsFromPdb.iterator(); iter.hasNext(); ) {
							Person prevPerson = person;
							person = iter.next();
							if (isFirst) {
								isFirst = false;
								prevPerson = person;
							} else {
								nameBuilderFromPdb.append(AND);
							}
							nameBuilderFromPdb.append((person.getGivenname()==null ? EMPTY_STRING : person.getGivenname()));
							if (!iter.hasNext() || !eq(prevPerson.getLastname(), person.getLastname())) {
								nameBuilderFromPdb.append(SPACE);
								nameBuilderFromPdb.append(person.getLastname());
							}
						}
					} else {
						nameBuilderFromPdb.append(number);
					}
					displayMessage(nameBuilderFromPdb.toString());
					
					
				}
			} catch (IllegalArgumentException iae) {
				System.err.println("The given file could not be read!");
				System.exit(-2);
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
	
	private void displayMessage(String message) {
		final String finalMessage;
		if (message==null) {
			finalMessage = EMPTY_STRING;
		} else {
			finalMessage = message;
		}
		// TODO
		SwingUtilities.invokeLater(new Thread() {
			public void run() {
				JOptionPane.showMessageDialog(null, finalMessage, "Call Monitor - Incoming Call", JOptionPane.INFORMATION_MESSAGE, imageIcon);
			}
		});
	}
	
	private TrayIcon trayIcon = null;
	private Image image = null;
	private ImageIcon imageIcon = null;
	
	private void setupTrayIcon() {
		if (SystemTray.isSupported()) {

		    SystemTray tray = SystemTray.getSystemTray();
		    InputStream imageStream = getClass().getResourceAsStream("/images/trayicon.png");
		    try {
				image = ImageIO.read(imageStream);
			} catch (IOException e1) {
				// do nothing
			} catch (IllegalArgumentException iae) {
				// do nothing
			}
			if (image == null) {
				System.err.println("Image file problem!");
				System.exit(-4);
			}
			imageIcon = new ImageIcon(image);
			final PopupMenu popup = new PopupMenu();
		    MenuItem exitItem = new MenuItem("Exit");
		    ActionListener exitListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            if (JOptionPane.showConfirmDialog(null, "Close Call Monitor now?", "Call Monitor - Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, imageIcon)==JOptionPane.YES_OPTION) {
		            	System.exit(0);
		            }
		        }
		    };
		    exitItem.addActionListener(exitListener);
		    popup.add(exitItem);
		    
		    MouseListener mouseListener = new MouseAdapter() {
		        public void mouseClicked(MouseEvent e) {
		            if (e.getButton()==MouseEvent.BUTTON1) {
		            	// show information
		            	JOptionPane.showMessageDialog(null, "Call Monitor\nby Mathis Dirksen-Thedens\nHomepage: http://www.zephyrsoft.net/", "Call Monitor - Information", JOptionPane.INFORMATION_MESSAGE, imageIcon);
		            }
		        }
		    };

		    trayIcon = new TrayIcon(image, "Call Monitor", popup);
		    trayIcon.setImageAutoSize(true);
		    trayIcon.addMouseListener(mouseListener);

		    try {
		        tray.add(trayIcon);
		    } catch (AWTException e) {
		        System.err.println("TrayIcon could not be added.");
		    }

		} else {
		    //  System Tray is not supported
		}
	}
	
	public static Object[] loadPdbFile(String pdbFile) throws IllegalArgumentException {
		List<Person> internal_persons = PdbTool.loadContactPdbFile(pdbFile);
		
		// Testdaten:
//		persons = new ArrayList<Person>();
//		persons.add(new Person("Sophie", "Dirksen-Thedens", "16.4.", "0511 5334911", "", "", ""));
//		persons.add(new Person("Mathis", "Dirksen-Thedens", "19.04.81", "0511 5334911", "", "", ""));
//		persons.add(new Person("???", "Dirksen-Thedens", "22.04.2008", "0511 5334911", "", "", ""));
		
		HashMap<String, Set<Person>> internal_number2persons = new HashMap<String, Set<Person>>();
		for (Person person : internal_persons) {
			if (person.getPhone1()!=null && !person.getPhone1().equals(EMPTY_STRING)) {
				addPerson(person.getPhone1(), person, internal_number2persons);
			}
			if (person.getPhone2()!=null && !person.getPhone2().equals(EMPTY_STRING)) {
				addPerson(person.getPhone2(), person, internal_number2persons);
			}
			if (person.getPhone3()!=null && !person.getPhone3().equals(EMPTY_STRING)) {
				addPerson(person.getPhone3(), person, internal_number2persons);
			}
			if (person.getPhone4()!=null && !person.getPhone4().equals(EMPTY_STRING)) {
				addPerson(person.getPhone4(), person, internal_number2persons);
			}
			if (person.getPhone5()!=null && !person.getPhone5().equals(EMPTY_STRING)) {
				addPerson(person.getPhone5(), person, internal_number2persons);
			}
			if (person.getPhone6()!=null && !person.getPhone6().equals(EMPTY_STRING)) {
				addPerson(person.getPhone6(), person, internal_number2persons);
			}
			if (person.getPhone7()!=null && !person.getPhone7().equals(EMPTY_STRING)) {
				addPerson(person.getPhone7(), person, internal_number2persons);
			}
		}
		
		return new Object[] {internal_persons, internal_number2persons};
	}
	
	private static void addPerson(String number, Person person, HashMap<String, Set<Person>> number2persons) {
		number = removeSpaces(number);
		if (number2persons.get(number)==null) {
			Set<Person> newSet = new TreeSet<Person>(new PersonReihenfolgeComparator<Person>());
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
	
	/**
	 * Vergleicht Personen <b>zuerst nach Reihenfolge</b>, d.h. wenn nur zwei Personen die gleiche Zahl
	 * im Attribut Reihenfolge stehen haben, werden sie überhaupt erst nach Namen verglichen! 
	 */
	protected static class PersonReihenfolgeComparator<P extends Person> implements Comparator<P> {

		public int compare(P p1, P p2) {
			if (p1==null && p2!=null) {
				return -1;
			} else if (p1!=null && p2==null) {
				return 1;
			} else if (p1==null && p2==null) {
				return 0;
			} else {
				// beide Person-Objekte sind ungleich null
				if (p1.getReihenfolge() < p2.getReihenfolge()) {
					return -1;
				} else if (p1.getReihenfolge() > p2.getReihenfolge()) {
					return 1;
				} else {
					// jetzt nach Nachname vergleichen
					int byLastname = cmp(p1.getLastname(), p2.getLastname());
					if (byLastname!=0) {
						return byLastname;
					} else {
						// jetzt nach Vorname vergleichen
						int byGivenName = cmp(p1.getGivenname(), p2.getGivenname());
						if (byGivenName!=0) {
							return byGivenName;
						} else {
							// jetzt nach Geburtstag vergleichen
							int byBirthday = cmp(p1.getBirthday(), p2.getBirthday());
							if (byBirthday!=0) {
								return byBirthday;
							} else {
								// dann sind sie halt gleich!
								return 0;
							}
						}
					}
				}
			}
		}
		
		private int cmp(String s1, String s2) {
			if (s1==null && s2!=null) {
				return -1;
			} else if (s1!=null && s2==null) {
				return 1;
			} else if (s1==null && s2==null) {
				return 0;
			} else {
				// beide Strings sind ungleich null
				return s1.compareTo(s2);
			}
		}
		
		private int cmp(Date d1, Date d2) {
			if (d1==null && d2!=null) {
				return -1;
			} else if (d1!=null && d2==null) {
				return 1;
			} else if (d1==null && d2==null) {
				return 0;
			} else {
				// beide Daten sind ungleich null
				return d1.compareTo(d2);
			}
		}
		
	}

}
