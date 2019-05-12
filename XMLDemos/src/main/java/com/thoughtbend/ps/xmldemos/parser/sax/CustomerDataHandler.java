package com.thoughtbend.ps.xmldemos.parser.sax;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.thoughtbend.ps.xmldemos.data.Const;
import com.thoughtbend.ps.xmldemos.data.Customer;

public class CustomerDataHandler implements EntityDataHandler<Customer> {

	private enum CustomerNodeName {
		FIRST_NAME,
		LAST_NAME,
		EMAIL
	}
	
	private Customer customerData = new Customer();
	private CustomerNodeName currentNodeName = null;
	private AddressDataHandler addressDataHandler = null;
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if (Const.Namespace.CUSTOMER.equals(uri)) {
			
			
			switch (localName) {
			case "customer":	// We are handling the parent node in here too
				
				// we won't set a currentNodeName, but we'll handle the attributes
				String idValue = attributes.getValue("id");
				customerData.setId(Long.parseLong(idValue));
				break;
			case "firstName":
				currentNodeName = CustomerNodeName.FIRST_NAME;
				break;
			case "lastName":
				currentNodeName = CustomerNodeName.LAST_NAME;
				break;
			case "email":
				currentNodeName = CustomerNodeName.EMAIL;
				break;
			}
		}
		else if (Const.Namespace.ADDRESS.equals(uri)) {
			
			switch (localName) {
			case "addresses":
				break;
			case "address":
				addressDataHandler = new AddressDataHandler();
				break;
			}
			
			if (addressDataHandler != null) {
				
				if (customerData.getAddresses() == null) {
					customerData.setAddresses(new ArrayList<>());
				}
				addressDataHandler.startElement(uri, localName, qName, attributes);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if (addressDataHandler != null) {
			
			addressDataHandler.endElement(uri, localName, qName);
			
			if (Const.Namespace.ADDRESS.equals(uri) && "address".equals(localName)) {
				
				customerData.getAddresses().add(addressDataHandler.getData());
				addressDataHandler = null;
			}
		}
		
		currentNodeName = null;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		if (addressDataHandler != null) {
			addressDataHandler.characters(ch, start, length);
		}
		else if (customerData != null && currentNodeName != null) {
			
			String value = new String(ch).substring(start, start+length);
			
			switch (currentNodeName) {
			case FIRST_NAME:
				customerData.setFirstName(value);
				break;
			case LAST_NAME:
				customerData.setLastName(value);
				break;
			case EMAIL:
				customerData.setEmailAddress(value);
				break;
			}
		}
	}

	@Override
	public Customer getData() {
		return this.customerData;
	}

}
