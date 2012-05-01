package org.zephyrsoft.productivity.prodtool;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.risertech.xdav.address.vcard.Addressbook;
import com.risertech.xdav.address.vcard.IIdentificationType;
import com.risertech.xdav.address.vcard.VCard;
import com.risertech.xdav.address.vcard.addressing.Adr;
import com.risertech.xdav.address.vcard.addressing.DeliveryType;
import com.risertech.xdav.address.vcard.identification.BDay;
import com.risertech.xdav.address.vcard.identification.N;
import com.risertech.xdav.address.vcard.internal.AddressbookParser;
import com.risertech.xdav.address.vcard.telecommunication.Email;
import com.risertech.xdav.address.vcard.telecommunication.Tel;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.jackrabbit.spi2davex.DavGetMethod;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.DavMethodBase;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zephyrsoft.productivity.prodtool.structure.Person;
import org.zephyrsoft.productivity.prodtool.structure.Person.Address;
import org.zephyrsoft.productivity.prodtool.structure.Person.Address.Type;

/**
 * Get data about contacts using a CardDAV source.
 * 
 * @author Mathis Dirksen-Thedens
 */
public class CardDavTool {
	
	private static Logger LOG = LoggerFactory.getLogger(CardDavTool.class);
	
	private static final class MyProtocolSocketFactoryImpl implements ProtocolSocketFactory {
		SSLContext sslContext = null;
		
		public MyProtocolSocketFactoryImpl() {
			try {
				sslContext = SSLContext.getInstance("SSL");
				
				TrustManager tm = new X509TrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
						// do nothing
					}
					
					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
						// do nothing
					}
					
					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				};
				
				sslContext.init(null, new TrustManager[] {tm}, null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(host, port);
		}
		
		@Override
		public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException,
			UnknownHostException {
			return sslContext.getSocketFactory().createSocket(host, port, localHost, localPort);
		}
		
		@Override
		public Socket createSocket(String host, int port, InetAddress localHost, int localPort,
			HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
			// ignoring params
			return sslContext.getSocketFactory().createSocket(host, port, localHost, localPort);
		}
	}
	
	public static void main(String[] args) {
		if (args.length == 1) {
			List<Person> persons = loadCardDavAddressBook(args[0], null, null);
			System.out.println(persons);
		} else if (args.length == 3) {
			List<Person> persons = loadCardDavAddressBook(args[0], args[1], args[2]);
			System.out.println(persons);
		} else {
			System.out.println("wrong number of arguments");
		}
	}
	
	public static List<Person> loadCardDavAddressBook(String url, final String username, final String password) {
		List<Person> ret = new ArrayList<Person>();
		
		log("beginning to load metadata");
		
		// using Jackrabbit for PROPFIND method
		HttpClient client = null;
		MultiStatus multiStatus = null;
		try {
			client = new HttpClient();
			
			ProtocolSocketFactory protocolSocketFactory = new MyProtocolSocketFactoryImpl();
			Protocol.registerProtocol("https", new Protocol("https", protocolSocketFactory, 443));
			
			Credentials creds = new UsernamePasswordCredentials(username, password);
			client.getState().setCredentials(AuthScope.ANY, creds);
			
			DavMethodBase method =
				new PropFindMethod(url, PropFindMethod.PROPFIND_ALL_PROP, PropFindMethod.DEPTH_INFINITY);
			client.executeMethod(method);
			multiStatus = method.getResponseBodyAsMultiStatus();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		log("metadata loaded");
		
		if (multiStatus == null) {
			System.exit(-2);
			return null;
		} else {
			log("beginning to load vCards");
			
			StringBuilder sb = new StringBuilder();
			
			String baseUrl = url.replaceAll("^(.+://[^/]+)/.*$", "$1");
			// get each referenced VCard: interpret metadata first, then fetch content
			for (MultiStatusResponse resp : multiStatus.getResponses()) {
				DavPropertySet props = resp.getProperties(DavServletResponse.SC_OK);
				String name = null;
				String href = null;
				for (DavProperty<?> prop : props) {
					String value = prop.getValue() != null ? prop.getValue().toString() : "";
//					System.out.println(prop.getName().getName() + ": " + value);
					if (prop.getName().getName().equalsIgnoreCase("href")) {
						href = value;
					}
					if (prop.getName().getName().equalsIgnoreCase("displayname")) {
						name = value;
					}
				}
				// now get the VCard content
				log("loading " + name);
				try {
					DavMethodBase method2 = new DavGetMethod(baseUrl + href);
					client.executeMethod(method2);
					String vcard = method2.getResponseBodyAsString();
//					System.out.println(vcard);
					sb.append(vcard);
					sb.append("\n");
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-3);
				}
			}
			
			log("loaded vCards");
			
			Addressbook abook = AddressbookParser.parse(sb.toString());
			
			List<VCard> vCards = abook.getVCards();
			for (VCard vCard : vCards) {
				Person person = new Person();
				List<IIdentificationType> types = vCard.getTypes();
				for (IIdentificationType type : types) {
					if (type instanceof N) {
						// lastname / given name
						person.setLastname(flattenList(((N) type).getFamilyNames(), " "));
						person.setGivenname(flattenList(((N) type).getGivenNames(), " "));
					} else if (type instanceof BDay) {
						// birthday
						person.setBirthday(((BDay) type).getBirthday().getTime());
					} else if (type instanceof Adr) {
						// address
						Adr adrRecord = (Adr) type;
						for (int i = 0; i < adrRecord.getTypes().size(); i++) {
							DeliveryType delType = adrRecord.getTypes().get(i);
							Type adrType = Type.HOME;
							if (delType == DeliveryType.WORK) {
								adrType = Type.WORK;
							} else if (delType == DeliveryType.PREFERRED) {
								adrType = Type.OTHER;
							}
							person.addAddress(new Address(getPart(adrRecord.getStreetAddress(), i), getPart(
								adrRecord.getPostalCode(), i), getPart(adrRecord.getLocality(), i), adrType));
						}
					} else if (type instanceof Tel) {
						// telephone / mobile phone
						Tel telRecord = (Tel) type;
						person.addPhone(telRecord.getPhoneNumber());
					} else if (type instanceof Email) {
						// email
						Email emailRecord = (Email) type;
						person.addPhone(emailRecord.getAddress());
					}
				}
				ret.add(person);
			}
			
			return ret;
		}
	}
	
	private static void log(String toLog) {
		System.out.println(toLog);
	}
	
	private static final String flattenList(List<String> list, String separator) {
		StringBuilder ret = new StringBuilder();
		boolean isFirst = true;
		for (String str : list) {
			if (isFirst) {
				isFirst = false;
			} else {
				ret.append(separator);
			}
			ret.append(str);
		}
		return ret.toString();
	}
	
	private final static String getPart(List<String> list, int part) {
		if (list == null || list.size() <= part) {
			throw new IllegalArgumentException();
		}
		return list.get(part);
	}
}
