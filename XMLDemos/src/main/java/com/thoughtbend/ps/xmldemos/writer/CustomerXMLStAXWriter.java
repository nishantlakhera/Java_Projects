package com.thoughtbend.ps.xmldemos.writer;

import static com.thoughtbend.ps.xmldemos.data.Const.Namespace.ADDRESS;
import static com.thoughtbend.ps.xmldemos.data.Const.Namespace.CUSTOMER;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Const;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.data.CustomerDataFactory;

public class CustomerXMLStAXWriter {

	public static void main(String[] args) {
		
		CustomerDataFactory dataFactory = new CustomerDataFactory();
		List<Customer> customerList = dataFactory.buildCustomers();
		
		try (FileOutputStream fos = new FileOutputStream("./stax-xml-output.xml")) {
			XMLOutputFactory factory = XMLOutputFactory.newFactory();
			factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
			
			//XMLStreamWriter writer = factory.createXMLStreamWriter(System.out);
			XMLStreamWriter writer = factory.createXMLStreamWriter(fos);
			
			writer.writeStartDocument();
			writer.setPrefix("tbc", CUSTOMER);
			writer.writeStartElement(CUSTOMER, "customers");
			
			for (Customer currentCustomer : customerList) {
				
				buildCustomer(writer, currentCustomer);
			}
			
			writer.writeEndElement();
			writer.writeEndDocument();
			writer.flush();
			
		} catch (XMLStreamException | IOException e) {
			
			e.printStackTrace(System.err);
		} 
	}
	
	private static void buildCustomer(XMLStreamWriter writer, Customer customer) throws XMLStreamException {
		
		writer.writeStartElement(CUSTOMER, "customer");
		writer.writeAttribute("id", customer.getId().toString());
		
		buildTextElement(writer, CUSTOMER, "firstName", customer.getFirstName());
		buildTextElement(writer, CUSTOMER, "lastName", customer.getLastName());
		buildTextElement(writer, CUSTOMER, "email", customer.getEmailAddress());
		
		List<Address> addressList = customer.getAddresses();
		if (addressList != null && addressList.size() > 0) {
			
			writer.setPrefix("tba", ADDRESS);
			writer.writeStartElement(ADDRESS, "addresses");
			
			for (Address currentAddress : addressList) {
				
				buildAddress(writer, currentAddress);
			}
			
			writer.writeEndElement();
		}
		
		writer.writeEndElement();
	}
	
	private static void buildAddress(XMLStreamWriter writer, Address address) throws XMLStreamException {
		
		writer.writeStartElement(ADDRESS, "address");
		
		buildTextElement(writer, ADDRESS, "type", address.getAddressType());
		buildTextElement(writer, ADDRESS, "street", address.getStreet1());
		buildTextElement(writer, ADDRESS, "city", address.getCity());
		buildTextElement(writer, ADDRESS, "state", address.getState());
		buildTextElement(writer, ADDRESS, "zip", address.getZip());
		
		writer.writeEndElement();
	}
	
	private static void buildTextElement(XMLStreamWriter writer, String namespace, String localName, 
										 String value) throws XMLStreamException {
		
		writer.writeStartElement(namespace, localName);
		writer.writeCharacters(value);
		writer.writeEndElement();
	}

}
