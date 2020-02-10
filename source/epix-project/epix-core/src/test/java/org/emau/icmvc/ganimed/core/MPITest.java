package org.emau.icmvc.ganimed.core;

/*
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2017 The MOSAIC Project - Institut fuer Community Medicine der
 * 							Universitaetsmedizin Greifswald - mosaic-projekt@uni-greifswald.de
 * 							concept and implementation
 * 							c. schack, d.langner, l. geidel
 * 							web client
 * 							a. blumentritt
 * 							g. weiher
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
 * __
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ###license-information-end###
 */


import org.emau.icmvc.ganimed.epix.service.EPIXService;
import org.junit.Test;


public class MPITest {

	private EPIXService epixService = null;
	
	public MPITest() {
		
	}
	
//	protected void setUp() throws Exception {
//		epixService = new SpringEPIXFactory().createEPIXService(new Principal() {			
//			public String getName() {
//				return "minca";
//			}
//		});
//		super.setUp();
//	}
//	
	@Test
	public void testMPIRequest()  {
//		
//		try {
//			XMLBindingUtil<MPIRequest> binder = new XMLBindingUtil<MPIRequest>();					
//			MPIRequest request = binder.load(MPIRequest.class, "gmds-demo.xml");
//						
//			for (RequestEntry entry : request.getRequestEntries()) {
//				Person person = entry.getPerson();
//				List<Identifier> idents = person.getIdentifiers();
//				for (Identifier ident : idents) {				
//					System.out.println(ident);
//				}				
//				System.out.println("Associated Patient "+entry.getPerson()+"\n");
//			
//			}
//
//			
//			
//			MPIResponse res = epixService.requestMPI(request);			
//			
//			List<ResponseEntry> rentries = res.getResponseEntries();
//			assertEquals("MPIResonse entry count ", rentries.size() , request.getRequestEntries().size());
//			for (ResponseEntry entry : rentries) {
//				Person person = entry.getPerson();
//				MPIID mpiId = person.getMpiid();
//				List<Identifier> idents = person.getIdentifiers();
//				for (Identifier ident : idents) {
//					IdentifierDomain id = ident.getIdentifierDomain();
//					assertEquals("IdentifierDomain Name", id.getName(), request.getIdentifierDomain().getName());
//					assertEquals("IdentifierDomain OID", id.getOid(),  request.getIdentifierDomain().getOid());
//				}
//				System.out.println(entry.getErrorCode().getMessage());				
//				System.out.println("Requested MPI "+mpiId.getValue());
//				System.out.println("Requested MPI Type "+mpiId.getIdType());
//				System.out.println("Associated Patient "+entry.getPerson().getFirstName()+" "+entry.getPerson().getLastName());			
//			}
//			
//			/*JAXBContext context;
//			try {
//				context = JAXBContext.newInstance( MPIResponse.class.getPackage().getName() );
//	
//			    Marshaller m = context.createMarshaller();
//			    m.marshal(new JAXBElement(new QName("","rootTag"), MPIResponse.class, res),  new File("d:\\MPIResponse.xml"));
//	
//			} catch (JAXBException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}*/
//		} catch (MPIException e) {
//			e.printStackTrace();
//			assertTrue(false);
//		} catch (Exception e) {
//			e.printStackTrace();
//			assertTrue(false);
//		}
//		assertTrue(true);
//	}
//
//	protected void tearDown() throws Exception {				
//		EPIXContext ctx = ((EPIXImpl)epixService).getContext();
//		Session session = ctx.initPersistenceSession();		
//		PersonDAO personDao = ctx.getPersonDAO();
//		PersonHistoryDAO personHistoryDAO = ctx.getPersonHistoryDAO();
//		MPIIDDAO mpiiddao = ctx.getMpiIdDAO();
//		
//		delete(personDao, org.emau.icmvc.ganimed.epix.core.persistence.model.PersonLink.class.getName(), session);
//		
//		Transaction tx = session.beginTransaction();		
//		personHistoryDAO.deleteAll();		
//		tx.commit();
//		
//		tx = session.beginTransaction();		
//		personDao.deleteAll();		
//		tx.commit();
//		
//		tx = session.beginTransaction();		
//		mpiiddao.deleteAll();		
//		tx.commit();	
//		
//		/**
//		 * 	DELETE FROM contact;
//		 *	DELETE FROM contact_history;
//		 *	DELETE FROM identifier;
//		 *	DELETE FROM identifier_history;
//		 *	DELETE FROM personlink;
//		 *	DELETE FROM person_history;
//		 *	DELETE FROM person;
//		 *	DELETE FROM mpi_id;
//		 */			
//										
//		session.close();
//		super.tearDown();
//	}
//	
//	private void delete(GenericDAO genericDAO, String clazz, Session session) throws MPIException {
//		Transaction tx = session.beginTransaction();
//		genericDAO.deleteAll(clazz);
//		tx.commit();
//		
	} 
	
}
