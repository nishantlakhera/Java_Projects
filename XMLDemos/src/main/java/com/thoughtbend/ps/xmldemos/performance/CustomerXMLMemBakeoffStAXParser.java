package com.thoughtbend.ps.xmldemos.performance;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Const;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.data.ObjectPrinter;

public class CustomerXMLMemBakeoffStAXParser {
	
	public static void main(String[] args) {
		System.out.println("Started @ " + new Date());
		printMemory();
		try (final InputStream inputStream = ClassLoader.getSystemResourceAsStream("./mem-eff-data/demodata-111m.xml")) {
			
			XMLInputFactory inputFactory = XMLInputFactory.newFactory();
			XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);
			printMemory();
			
			List<Customer> customerList = new ArrayList<>();
			
			while (reader.hasNext()) {
				//printMemory();
				if (reader.isStartElement() && Const.Namespace.CUSTOMER.equals(reader.getNamespaceURI()) && 
						"customer".equals(reader.getLocalName())) {
					
					customerList.add(processCustomer(reader));
					//processCustomer(reader);
				}
				else {
					reader.next();
				}
			}
			
			printMemory("BEFORE_PRINTING");
			for (Customer currentCustomer : customerList) {
				ObjectPrinter.printCustomer(currentCustomer);
			}
			//System.gc();
			printMemory();
			System.gc();
			printMemory();
		}
		catch (IOException | XMLStreamException ex) {
			ex.printStackTrace(System.err);
		}
		System.out.println("Ended @ " + new Date());
	}
	
	private static Customer processCustomer(final XMLStreamReader reader) throws XMLStreamException {
		
		final Customer customer = new Customer();
		
		while (reader.hasNext()) {
			
			reader.next();
			
			if (reader.isStartElement() && Const.Namespace.CUSTOMER.equals(reader.getNamespaceURI())) {
				String localName = reader.getLocalName();
				reader.next();
				switch (localName) {
				case "id":
					customer.setId(Long.parseLong(reader.getText()));
					break;
				case "firstName":
					customer.setFirstName(reader.getText());
					break;
				case "lastName":
					customer.setLastName(reader.getText());
					break;
				case "email":
					customer.setEmailAddress(reader.getText());
					break;
				}
			}
			else if (reader.isStartElement() && Const.Namespace.ADDRESS.equals(reader.getNamespaceURI())) {
				
				if ("addresses".equals(reader.getLocalName())) {
					customer.setAddresses(new ArrayList<>());
				}
				else if ("address".equals(reader.getLocalName())) {
					customer.getAddresses().add(processAddress(reader));
				}
			}
			// When we hit the end element, we want to break and return - the next customer 
			// start will re-enter this method
			else if (reader.isEndElement() && Const.Namespace.CUSTOMER.equals(reader.getNamespaceURI()) &&
					 "customer".equals(reader.getLocalName())) {
				break;
			}
		}
		
		return customer;
	}
	
	private static Address processAddress(final XMLStreamReader reader) throws XMLStreamException {
		
		final Address address = new Address();
		
		while (reader.hasNext()) {
			
			reader.next();
			
			if (reader.isStartElement() && Const.Namespace.ADDRESS.equals(reader.getNamespaceURI())) {
				
				String localName = reader.getLocalName();
				reader.next();
				
				switch (localName) {
				case "type":
					address.setAddressType(reader.getText());
					break;
				case "street":
					address.setStreet1(reader.getText());
					break;
				case "city":
					address.setCity(reader.getText());
					break;
				case "state":
					address.setState(reader.getText());
					break;
				case "zip":
					address.setZip(reader.getText());
					break;
				}
			}
			else if (reader.isEndElement() && Const.Namespace.ADDRESS.equals(reader.getNamespaceURI()) &&
				"address".equals(reader.getLocalName())) {
				break;
			}
		}
		
		return address;
	}
	
	private static void printMemory() {
		printMemory("");
	}
	
	private static void printMemory(String label) {
		
		if (label != null && !label.equals("")) {
			label = String.format("(%1$s)", label);
		}
		
		Runtime runtime = Runtime.getRuntime();
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		String trace = String.format("MEM DUMP [Free=%1$s BYTES, Total=%3$s BYTES, Max=%2$s BYTES] %4$s", 
				numberFormat.format(runtime.freeMemory()), 
				numberFormat.format(runtime.maxMemory()), 
				numberFormat.format(runtime.totalMemory()), 
				label);
		System.out.println(trace);
	}
}
