package com.thoughtbend.ps.xmldemos.parser.validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Const;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.data.ObjectPrinter;

public class CustomerXMLStAXParser {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./demo06/customers.xml");
			 InputStream validationStream = ClassLoader.getSystemResourceAsStream("./demo06/customers.xml")) {
			
			InputStream customerSchemaStream = ClassLoader.getSystemResourceAsStream("./demo06/customer.xsd");
			InputStream addressSchemaStream = ClassLoader.getSystemResourceAsStream("./demo06/address.xsd");
			StreamSource customerSchemaSource = new StreamSource(customerSchemaStream);
			StreamSource addressSchemaSource = new StreamSource(addressSchemaStream);
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			Schema schema = schemaFactory.newSchema(new StreamSource[] {
				addressSchemaSource,
				customerSchemaSource
			});
			
			XMLInputFactory inputFactory = XMLInputFactory.newFactory();
			
			StAXSource validationSource =
					new StAXSource(inputFactory.createXMLStreamReader(validationStream));
			Validator validator = schema.newValidator();
			ValidationErrorHandler errorHandler = new ValidationErrorHandler();
			validator.setErrorHandler(errorHandler);
			validator.validate(validationSource);
			
			if (errorHandler.isErrorOccurred()) {
				System.exit(-1);
			}
			
			XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);
			
			List<Customer> customerList = new ArrayList<>();
			
			while (reader.hasNext()) {
				
				if (reader.isStartElement() &&
					Const.Namespace.CUSTOMER.equals(reader.getNamespaceURI()) &&
					"customer".equals(reader.getLocalName())) {
					
					customerList.add(processCustomer(reader));
				}
				else {
					reader.next();
				}
			}
			
			for (Customer currentCustomer : customerList) {
				
				ObjectPrinter.printCustomer(currentCustomer);
			}
		}
		catch (IOException | XMLStreamException | SAXException ex) {
			ex.printStackTrace(System.err);
		}
	}

	private static Customer processCustomer(XMLStreamReader reader) throws XMLStreamException {
		
		Customer customer = new Customer();
		String idValue = reader.getAttributeValue(null, "id");
		customer.setId(Long.parseLong(idValue));
		
		while (reader.hasNext()) {
			
			reader.next();
			
			if (reader.isStartElement() && Const.Namespace.CUSTOMER.equals(reader.getNamespaceURI())) {
				
				String localName = reader.getLocalName();
				reader.next();
				if (reader.isCharacters()) {
					String elementValue = reader.getText();
					
					switch (localName) {
					case "firstName":
						customer.setFirstName(elementValue);
						break;
					case "lastName":
						customer.setLastName(elementValue);
						break;
					case "email":
						customer.setEmailAddress(elementValue);
						break;
					}
				}
			}
			else if (reader.isStartElement() && 
					 Const.Namespace.ADDRESS.equals(reader.getNamespaceURI())) {
				
				if ("addresses".equals(reader.getLocalName())) {
					
					customer.setAddresses(new ArrayList<>());
				}
				else if ("address".equals(reader.getLocalName())) {
					
					customer.getAddresses().add(processAddress(reader));
				}
			}
			else if (reader.isEndElement() && 
					 Const.Namespace.CUSTOMER.equals(reader.getNamespaceURI()) &&
					 "customer".equals(reader.getLocalName())) {
				
				break;
			}
		}
		
		return customer;
	}
	
	private static Address processAddress(XMLStreamReader reader) throws XMLStreamException {
		
		Address address = new Address();
		
		while (reader.hasNext()) {
			
			reader.next();
			
			if (reader.isStartElement() && Const.Namespace.ADDRESS.equals(reader.getNamespaceURI())) {
				
				String localName = reader.getLocalName();
				reader.next();
				
				if (reader.isCharacters()) {
					
					String elementValue = reader.getText();
					switch (localName) {
					case "type":
						address.setAddressType(elementValue);
						break;
					case "street":
						address.setStreet1(elementValue);
						break;
					case "city":
						address.setCity(elementValue);
						break;
					case "state":
						address.setState(elementValue);
						break;
					case "zip":
						address.setZip(elementValue);
						break;
					}
				}
			}
			else if (reader.isEndElement() && 
					 Const.Namespace.ADDRESS.equals(reader.getNamespaceURI()) &&
					 "address".equals(reader.getLocalName())) {
				
				break;
			}
		}
		
		return address;
	}
}
