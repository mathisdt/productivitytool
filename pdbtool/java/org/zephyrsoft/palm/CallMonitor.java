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
public class CallMonitor {
	
	private List<Person> persons = null;
	private HashMap<String, Set<Person>> number2persons = null;
	
	private Charset charset = Charset.forName("ISO-8859-1");

	public static void main(String[] args) {
		new CallMonitor(args);
	}
	
	private static final String DELIMITER = "|";
	private static final String AND = " + ";
	private static final String EMPTY_STRING = "";
	private static final String SPACE = " ";
	private static final String COMMA = ",";
	
	public CallMonitor(String[] args) {
		if (args==null || args.length==0) {
			System.err.println("Too few arguments. Please provide the path to the .pdb file to read!");
			System.exit(-1);
		} else {
			try {
//				setupTrayIcon();
				loadPdbFile(args[0]);
				ServerSocket serverSocket = new ServerSocket(7777);
				while (true) {
					Socket clientSocket = serverSocket.accept();
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), charset));
					String inputLine;
					if ((inputLine = in.readLine()) != null) {
						// expected format: "0123456789|Name from Reverse Lookup"
						// use following listener entry in Freetz configuration
						// (replace IPADDRESS with the address of the computer on which this program will run):
						//       in:request ^ ^ rawmsg -t "${SOURCE}|${SOURCE_NAME}" IPADDRESS -p 7777
						StringTokenizer tokenizer = new StringTokenizer(inputLine, DELIMITER);
						String number = null;
						String nameFromReverseLookup = null;
						StringBuilder nameBuilderFromPdb = new StringBuilder();
						if (tokenizer.hasMoreTokens()) {
							number = removeSpaces(tokenizer.nextToken());
						}
						if (tokenizer.hasMoreTokens()) {
							nameFromReverseLookup = tokenizer.nextToken();
						}
						Set<Person> personsFromPdb = number2persons.get(number);
						if (personsFromPdb!=null) {
							boolean isFirst = true;
							for (Iterator<Person> iter = personsFromPdb.iterator(); iter.hasNext(); ) {
								Person person = iter.next();
								if (!isFirst) {
									nameBuilderFromPdb.append(AND);
								} else {
									isFirst = false;
								}
								nameBuilderFromPdb.append(person.getGivenname())
									.append(SPACE)
									.append(person.getLastname());
							}
						} else {
							StringTokenizer subTokenizer = new StringTokenizer(nameFromReverseLookup, COMMA);
							if (subTokenizer.hasMoreElements()) {
								StringTokenizer subSubTokenizer = new StringTokenizer(subTokenizer.nextToken(), SPACE);
								if (subSubTokenizer.hasMoreElements()) {
									String lastname = subSubTokenizer.nextToken();
									while (subSubTokenizer.hasMoreTokens()) {
										nameBuilderFromPdb.append(SPACE).append(subSubTokenizer.nextToken());
									}
									nameBuilderFromPdb.append(SPACE).append(lastname);
								}
							}
						}
						displayMessage(nameBuilderFromPdb.toString());
						
						
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
	private boolean mouseIsOverTrayIcon = false;
	private boolean mouseIsOverPopupMenu = false;
	private Image image = null;
	private ImageIcon imageIcon = null;
	
	@SuppressWarnings("unused")
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
		number = removeSpaces(number);
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
