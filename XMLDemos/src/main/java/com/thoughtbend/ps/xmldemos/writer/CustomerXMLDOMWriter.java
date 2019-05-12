package com.thoughtbend.ps.xmldemos.writer;

import static com.thoughtbend.ps.xmldemos.data.Const.Namespace.ADDRESS;
import static com.thoughtbend.ps.xmldemos.data.Const.Namespace.CUSTOMER;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.data.CustomerDataFactory;

public class CustomerXMLDOMWriter {

	public static void main(String[] args) {
		
		CustomerDataFactory dataFactory = new CustomerDataFactory();
		List<Customer> customerList = dataFactory.buildCustomers();
		
		try (FileOutputStream fos = new FileOutputStream("./dom-xml-output.xml")) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
			Element rootElement = document.createElementNS(CUSTOMER, "customers");
			rootElement.setPrefix("tbc");
			
			document.appendChild(rootElement);
			
			for (Customer currentCustomer : customerList) {
				
				DocumentFragment customerFragment = buildCustomerFragment(document, currentCustomer);
				rootElement.appendChild(customerFragment);
			}
			
			DOMSource source = new DOMSource(document);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			//transformer.transform(source, new StreamResult(System.out));
			transformer.transform(source, new StreamResult(fos));
			
		} catch (ParserConfigurationException | TransformerException | IOException e) {
			
			e.printStackTrace(System.err);
		}
	}
	
	private static DocumentFragment buildCustomerFragment(Document document, Customer customer) {
		
		DocumentFragment customerFragment = document.createDocumentFragment();
		
		Element customerElement = document.createElementNS(CUSTOMER, "customer");
		customerElement.setAttribute("id", customer.getId().toString());
		customerElement.setPrefix("tbc");
		
		customerElement.appendChild(createTextElement(document, CUSTOMER, "tbc", "firstName", 
														customer.getFirstName()));
		customerElement.appendChild(createTextElement(document, CUSTOMER, "tbc", "lastName", 
														customer.getLastName()));
		customerElement.appendChild(createTextElement(document, CUSTOMER, "tbc", "email", 
														customer.getEmailAddress()));
		
		if (customer.getAddresses() != null && customer.getAddresses().size() > 0) {
			
			Element addressesElement = document.createElementNS(ADDRESS, "addresses");
			addressesElement.setPrefix("tba");
			customerElement.appendChild(addressesElement);
			
			for (Address address : customer.getAddresses()) {
				
				DocumentFragment addressFragment = buildAddressFragment(document, address);
				addressesElement.appendChild(addressFragment);
			}
		}
		
		customerFragment.appendChild(customerElement);
		
		return customerFragment;
	}
	
	private static DocumentFragment buildAddressFragment(Document document, Address address) {
		
		DocumentFragment addressFragment = document.createDocumentFragment();
		Element addressElement = document.createElementNS(ADDRESS, "address");
		addressElement.setPrefix("tba");
		
		addressElement.appendChild(createTextElement(document, ADDRESS, "tba", "type", 
														address.getAddressType()));
		addressElement.appendChild(createTextElement(document, ADDRESS, "tba", "street", 
														address.getStreet1()));
		addressElement.appendChild(createTextElement(document, ADDRESS, "tba", "city", 
														address.getCity()));
		addressElement.appendChild(createTextElement(document, ADDRESS, "tba", "state", 
														address.getState()));
		addressElement.appendChild(createTextElement(document, ADDRESS, "tba", "zip", 
														address.getZip()));
		
		addressFragment.appendChild(addressElement);
		
		return addressFragment;
	}
	
	private static Element createTextElement(Document document, String namespace, String prefix, 
											 String localName, String value) {
		
		Element element = document.createElementNS(namespace, localName);
		
		if (prefix != null) {
			element.setPrefix(prefix);
		}
		
		element.setTextContent(value);
		
		return element;
	}

}
