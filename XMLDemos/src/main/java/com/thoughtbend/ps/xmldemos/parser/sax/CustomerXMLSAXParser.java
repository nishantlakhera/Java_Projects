package com.thoughtbend.ps.xmldemos.parser.sax;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.data.ObjectPrinter;

public class CustomerXMLSAXParser {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./new-customers.xml")) {
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			
			SAXParser parser = factory.newSAXParser();
			
			CustomerSAXHandler handler = new CustomerSAXHandler();
			parser.parse(inputStream, handler);
			
			for (Customer currentCustomer : handler.getCustomerList()) {
				
				ObjectPrinter.printCustomer(currentCustomer);
			}
		}
		catch (IOException | ParserConfigurationException | SAXException ex) {
			ex.printStackTrace(System.err);
		}
	}

}
