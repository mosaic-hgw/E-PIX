package org.emau.icmvc.ganimed.epix;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.emau.icmvc.ganimed.common.model.Identifier;
import org.emau.icmvc.ganimed.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.common.model.MPIID;
import org.emau.icmvc.ganimed.common.model.Person;
import org.emau.icmvc.ganimed.epix.client.EPIXClient;
import org.emau.icmvc.ganimed.epix.client.EPIXClientFactory;
import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.MPIRequest;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.common.model.RequestEntry;
import org.emau.icmvc.ganimed.epix.common.model.ResponseEntry;
import org.emau.icmvc.ganimed.xml.bind.XMLBindingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EPIXInvocationExample {
	
	private EPIXClient client = null;
	
	private Logger logger = LoggerFactory.getLogger(EPIXInvocationExample.class);
	
	public EPIXInvocationExample() {
		
		BasicConfigurator.configure();
		
		try {
			client = EPIXClientFactory.getInstance().createEPIXClient("epix-service-config.xml");
		} catch (MPIException e) {
			logger.error("Unable to instantiate EPIXClient.", e);
			System.exit(-1);
		}		
		
		testMPIRequest();
				
		/*Patient p = new Patient();
		
		p.setFirstName("Hanso");
		p.setLastName("WurstTester");						
		p.setGender(Gender.M);
			
		try {
			XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar();
			date.setDay(3);
			date.setMonth(8);
			date.setYear(1934);
			p.setBirthDate(date);
		} catch (DatatypeConfigurationException e1) {		
			e1.printStackTrace();
		}				
	
		Contact contact = new Contact();
		contact.setCity("Greifswald");
		contact.setStreet("Franz-Mehring-Str. 24");
		contact.setZipCode("17489");
		contact.setPhone("0174234567");
		contact.setState("Mecklenburg-Vorpommern");
		p.getContact().add(contact);
		System.out.println(p);
		
		Identifier ident1 = new Identifier();
		ident1.setValue("dd2266644tt");
		ident1.setSendingApplication("MInCa");
		
		Contact c = new Contact();	
		c.setStreet("Streets of Philadelphia 23");
		c.setState("MacPom");
		
		HealthcareProvider hp = new HealthcareProvider();
		
		hp.setFirstName("Dr.");
		hp.setLastName("Pille");
		
		Identifier ident2 = new Identifier();
		ident2.setValue("Doctor_33333333311");
		ident2.setSendingApplication("MInCa");
			
		HealthcareProvider hp2 = new HealthcareProvider();
		
		hp2.setFirstName("Dr.");
		hp2.setLastName("Hansi");
		hp2.setDepartment("Radiologie");
		
		Identifier ident3 = new Identifier();	
		ident3.setValue("Doctor_888888");
		ident3.setSendingApplication("MInCa");
		
		MPIRequest request = new MPIRequest();
								
		RequestEntry r1 = new RequestEntry();
		r1.setPerson(p);
		p.getIdentifiers().add(ident1);
		
		RequestEntry r2 = new RequestEntry();
		r2.setPerson(hp);
		hp.getIdentifiers().add(ident2);
		
		RequestEntry r3 = new RequestEntry();
		r3.setPerson(hp2);
		hp2.getIdentifiers().add(ident3);
		
		request.setIdentifierDomain(client.getIdentifierDomain());
		request.getRequestEntries().add(r1);
		
		try {
			MPIResponse res = client.requestMPI(request);
			
			List<ResponseEntry> rentries = res.getResponseEntries();
			
			for (ResponseEntry entry : rentries) {
				Person person = entry.getPerson();
				MPIID mpiId = person.getMpiid();
				List<Identifier> idents = person.getIdentifiers();
				for (Identifier ident : idents) {
					System.out.println("idents "+ident.getValue());
				}
				System.out.println(entry.getErrorCode().getMessage());				
				System.out.println("Requested MPI "+mpiId.getValue());
				System.out.println("Requested MPI Type "+mpiId.getIdType());
				System.out.println("Associated Patient "+entry.getPerson().getFirstName()+" "+entry.getPerson().getLastName());			
			}
			
			/*JAXBContext context;
			try {
				context = JAXBContext.newInstance( MPIResponse.class.getPackage().getName() );
	
			    Marshaller m = context.createMarshaller();
			    m.marshal(new JAXBElement(new QName("","rootTag"), MPIResponse.class, res),  new File("d:\\MPIResponse.xml"));
	
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MPIException e) {
			e.printStackTrace();
		}*/
	}
	
public void testMPIRequest()  {
		
		try {
			XMLBindingUtil binder = new XMLBindingUtil();					
			MPIRequest request = binder.load(MPIRequest.class, "gmds-demo.xml");
						
			for (RequestEntry entry : request.getRequestEntries()) {
				Person person = entry.getPerson();
				List<Identifier> idents = person.getIdentifiers();
				for (Identifier ident : idents) {				
					System.out.println(ident);
				}				
				System.out.println("Associated Patient "+entry.getPerson()+"\n");
			
			}

			
			MPIResponse res = client.requestMPI(request);			
			
			List<ResponseEntry> rentries = res.getResponseEntries();
		
			for (ResponseEntry entry : rentries) {
				Person person = entry.getPerson();
				MPIID mpiId = person.getMpiid();
				List<Identifier> idents = person.getIdentifiers();
				for (Identifier ident : idents) {
					IdentifierDomain id = ident.getIdentifierDomain();
				}
				System.out.println(entry.getErrorCode().getMessage());				
				System.out.println("Requested MPI "+mpiId.getValue());
				System.out.println("Requested MPI Type "+mpiId.getIdType());
				System.out.println("Associated Patient "+entry.getPerson().getFirstName()+" "+entry.getPerson().getLastName());			
			}
			
			/*JAXBContext context;
			try {
				context = JAXBContext.newInstance( MPIResponse.class.getPackage().getName() );
	
			    Marshaller m = context.createMarshaller();
			    m.marshal(new JAXBElement(new QName("","rootTag"), MPIResponse.class, res),  new File("d:\\MPIResponse.xml"));
	
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		} catch (MPIException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {		
		EPIXInvocationExample main = new EPIXInvocationExample();
	}	
}
