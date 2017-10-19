package org.emau.icmvc.ganimed.epix.core.persistence.dao.impl;

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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Contact;
import org.emau.icmvc.ganimed.epix.core.persistence.model.HealthcareProvider;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Identifier;
import org.emau.icmvc.ganimed.epix.core.persistence.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Patient;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Person;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonGroup;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonLink;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;

/**
 * 
 * @author Christian Schack
 * @since 2010
 * 
 */

public class PersonDAOImpl extends PersonDAO {

	public PersonDAOImpl() {
	}

	//TODO Konstruktor ändern (personType löschen)
	@Override
	public Person addPerson(Person person, Class<? extends Person> personType) throws MPIException {

		if (person == null) {
			throw new IllegalArgumentException("Person must not be null.");
		}

		try {
			em.persist(person);
			logger.debug("New person added: " + person);
			return person;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Person getPersonById(Long id) throws MPIException {
		try {
			Query q = em.createNamedQuery("Person.getPersonByID");
			q.setParameter("id", id);
			Person person = (Person) q.getSingleResult();
			return person;
		} catch (NoResultException noe) {
			return null;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Person getPersonByPersonPreprocessed(PersonPreprocessed personPreprocessed) throws MPIException {
		try {
			
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
			Root<Person> root = criteriaQuery.from(Person.class);
			Predicate predicate = criteriaBuilder.equal(root.get("id"), personPreprocessed.getPersonId());
			criteriaQuery.select(root).where(predicate);
			Person person = em.createQuery(criteriaQuery).getSingleResult();
			return person;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Person> getPersonByIdentifierAndDomain(String identifier, IdentifierDomain domain) throws MPIException {
		try {
			@SuppressWarnings("unchecked")
			List<Person> p = (List<Person>) em.createNamedQuery("Person.findByLocalIdetifierAndDomain")
					.setParameter("ivalue", identifier).setParameter("oid", domain.getOid()).getResultList();
			return p;
		} catch (Exception e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Person getPersonByIdentifierAndDomain(Identifier identifier, IdentifierDomain domain) throws MPIException {
		try {
			// Person p = (Person) em.createQuery("SELECT p " +
			// "FROM Person p, Identifier i " +
			// "WHERE i.value = :ivalue AND i.domain.oid = :oid " +
			// "AND p.id = i.person.id")
			// .setParameter("ivalue",
			// identifier.getValue()).setParameter("oid",
			// domain.getOid()).getSingleResult();
			Person p = (Person) em.createNamedQuery("Person.findByLocalIdetifierAndDomain")
					.setParameter("ivalue", identifier.getValue()).setParameter("oid", domain.getOid())
					.getSingleResult();
			return p;
		} catch (NoResultException noe) {
			return null;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Person> getPersonsByIdentifierAndDomain(Identifier identifier, IdentifierDomain domain)
			throws MPIException {
		try {
			// Person p = (Person) em.createQuery("SELECT p " +
			// "FROM Person p, Identifier i " +
			// "WHERE i.value = :ivalue AND i.domain.oid = :oid " +
			// "AND p.id = i.person.id")
			// .setParameter("ivalue",
			// identifier.getValue()).setParameter("oid",
			// domain.getOid()).getSingleResult();
			@SuppressWarnings("unchecked")
			List<Person> p = em.createNamedQuery("Person.findByLocalIdetifierAndDomain")
					.setParameter("ivalue", identifier.getValue()).setParameter("oid", domain.getOid()).getResultList();
			return p;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Person> getPersonsByIdentifierAndDomain(String identifier, IdentifierDomain domain) throws MPIException {
		try {
			// Person p = (Person) em.createQuery("SELECT p " +
			// "FROM Person p, Identifier i " +
			// "WHERE i.value = :ivalue AND i.domain.oid = :oid " +
			// "AND p.id = i.person.id")
			// .setParameter("ivalue",
			// identifier.getValue()).setParameter("oid",
			// domain.getOid()).getSingleResult();
			@SuppressWarnings("unchecked")
			List<Person> p = em.createNamedQuery("Person.findByLocalIdetifierAndDomain")
					.setParameter("ivalue", identifier).setParameter("oid", domain.getOid()).getResultList();
			return p;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Person> getAllPersons() throws MPIException {
		try {
			logger.debug("Get all persons from database");
			@SuppressWarnings("unchecked")
			List<Person> persons = em.createNamedQuery("Person.getAllPersons").getResultList();
			return persons;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Person> getAllPersons(int pageSize, long pageNr) throws MPIException {
		try {
			logger.debug("Fetching person from dataset " + (int) (pageSize * pageNr) + " with page size " + pageSize
					+ ".");
			@SuppressWarnings("unchecked")
			List<Person> persons = em.createQuery("SELECT p " + "FROM Person p ").setMaxResults(pageSize)
					.setFirstResult((int) (pageSize * pageNr)).getResultList();
			return persons;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Person> getAllPersonMinusLinkedPersons(int pageSize, long pageNr) throws MPIException {

		try {
			@SuppressWarnings("unchecked")
			List<Person> persons = em.createNamedQuery("Person.getAllPersonMinusLinkedPersons").setMaxResults(pageSize)
					.setFirstResult((int) (pageSize * pageNr)).getResultList();
			return persons;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void addLinkedPerson(Person src, Person dest, double matchValue, String matchStrategy) throws MPIException {

		PersonLink pl = new PersonLink();
		pl.setSrcPerson(src);
		pl.setDestPerson(dest);
		pl.setThreshold(matchValue);
		pl.setAlgorithm(matchStrategy);

		try {
			em.persist(pl);
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Person> getAllLinkedPersonsByPersonId(Long personId) throws MPIException {
		try {
			@SuppressWarnings("unchecked")
			List<Person> persons = em
					.createQuery(
							"SELECT p " + "FROM Person p, PersonLink pl  " + "WHERE pl.srcPerson.id = :srcpid AND "
									+ "pl.destPerson.id = p.id").setParameter("srcpid", personId).getResultList();

			return persons;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Person> getPersonByFirstNameAndLastNameAndBirthDate(Person p) throws MPIException {
		try {
			Query q = em.createNamedQuery("Person.findByFirstNameAndLastNameAndDateOfBirth");
			q.setParameter("lastName", p.getLastName());
			q.setParameter("firstName", p.getFirstName());
			q.setParameter("birthDate", p.getBirthDate());
			@SuppressWarnings("unchecked")
			List<Person> persons = (List<Person>) q.getResultList();
			return persons;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Person findPersonByIdentifierAndDomain(Identifier identifier, IdentifierDomain domain) throws MPIException {
		try {
			Query q = em.createNamedQuery("Person.findByLocalIdetifierAndDomain");
			q.setParameter("ivalue", identifier.getValue());
			q.setParameter("oid", domain.getOid());
			try {
				Person searchedPerson = (Person) q.getSingleResult();
				return searchedPerson;
			} catch (NoResultException e) {
				throw new MPIException(ErrorCode.NO_PERSON_FOUND);
			} catch (NonUniqueResultException e) {
				throw new MPIException(ErrorCode.PERSISTENCE_ERROR);
			}
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Person> findPersonByPersonGroup(PersonGroup personGroup) throws MPIException {
		try {
			Query q = em.createNamedQuery("Person.findByPersonGroup");
			q.setParameter("pg", personGroup);
			try {
				@SuppressWarnings("unchecked")
				List<Person> persons = q.getResultList();
				return persons;
			} catch (NoResultException e) {
				throw new MPIException(ErrorCode.NO_PERSON_FOUND);
			}
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Person> getPersonsByIds(List<Long> ids) throws MPIException {
		if (ids == null || ids.isEmpty()) {
			throw new MPIException(ErrorCode.NO_ATTRIBUTES_DEFINED);
		}
		Query q = em.createNamedQuery("Person.getPersonsByIds");
		q.setParameter("ids", ids);
		try {
			@SuppressWarnings("unchecked")
			List<Person> persons = q.getResultList();
			return persons;
		} catch (Exception e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public int countLinkedPersonsByPersonId(Long personId) throws MPIException {
		try {
			int count = (Integer) em
					.createQuery(
							"SELECT count(p.id) " + "FROM Person p, PersonLink pl  "
									+ "WHERE pl.srcPerson.id = :srcpid AND " + "pl.destPerson.id = p.id")
					.setParameter("srcpid", personId).getSingleResult();

			return count;
		} catch (NoResultException noe) {
			return 0;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void deleteAll() throws MPIException {
		@SuppressWarnings("unchecked")
		List<Person> persons = getAllEntries(Person.class.getName());

		for (Person person : persons) {
			for (Contact contact : person.getContacts()) {
				em.remove(contact);
			}

			for (Identifier identifier : person.getLocalIdentifiers()) {
				// identifier.setPerson(null);
				em.remove(identifier);
			}
			em.remove(person);
		}
	}

	public void addPersonAndContact(Person person) throws MPIException {

		if (person == null) {
			throw new IllegalArgumentException("Person must not be null.");
		}

		try {
			person.setTimestamp(new Timestamp(System.currentTimeMillis()));
			em.persist(person);
			logger.debug("New person added: " + person);
			// return person;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void updatePerson(Person findPerson, Person p) throws MPIException {
		findPerson.setEntityManager(em);
		if (p.getFirstName() != null && !"".equals(p.getFirstName())) {
			findPerson.setFirstName(p.getFirstName());
		}
		if (p.getMiddleName() != null && !"".equals(p.getMiddleName())) {
			findPerson.setMiddleName(p.getMiddleName());
		}
		if (p.getLastName() != null && !"".equals(p.getLastName())) {
			findPerson.setLastName(p.getLastName());
		}
		if (p.getPrefix() != null && !"".equals(p.getPrefix())) {
			findPerson.setPrefix(p.getPrefix());
		}
		if (p.getSuffix() != null && !"".equals(p.getSuffix())) {
			findPerson.setSuffix(p.getSuffix());
		}
		if (p.getGender() != null && !"".equals(p.getGender()) && !"U".equals(p.getGender())) {
			findPerson.setGender(p.getGender());
		}
		if (p.getBirthDate() != null) {
			findPerson.setBirthDate(p.getBirthDate());
		}
		if (p.getBirthPlace() != null && !"".equals(p.getBirthPlace())) {
			findPerson.setBirthPlace(p.getBirthPlace());
		}
		if (p.getRace() != null && !"".equals(p.getRace())) {
			findPerson.setRace(p.getRace());
		}
		if (p.getReligion() != null && !"".equals(p.getReligion())) {
			findPerson.setReligion(p.getReligion());
		}
		if (p.getMothersMaidenName() != null && !"".equals(p.getMothersMaidenName())) {
			findPerson.setMothersMaidenName(p.getMothersMaidenName());
		}
		if (p.getDegree() != null && !"".equals(p.getDegree())) {
			findPerson.setDegree(p.getDegree());
		}
		if (p.getMotherTongue() != null && !"".equals(p.getMotherTongue())) {
			findPerson.setMotherTongue(p.getMotherTongue());
		}
		if (p.getNationality() != null && !"".equals(p.getNationality())) {
			findPerson.setNationality(p.getNationality());
		}
		if (p.getCivilStatus() != null && !"".equals(p.getCivilStatus())) {
			findPerson.setCivilStatus(p.getCivilStatus());
		}
		if (p.getValue1() != null && !"".equals(p.getValue1())) {
			findPerson.setValue1(p.getValue1());
		}
		if (p.getValue2() != null && !"".equals(p.getValue2())) {
			findPerson.setValue2(p.getValue2());
		}
		if (p.getValue3() != null && !"".equals(p.getValue3())) {
			findPerson.setValue3(p.getValue3());
		}
		if (p.getValue4() != null && !"".equals(p.getValue4())) {
			findPerson.setValue4(p.getValue4());
		}
		if (p.getValue5() != null && !"".equals(p.getValue5())) {
			findPerson.setValue5(p.getValue5());
		}
		if (p.getValue6() != null && !"".equals(p.getValue6())) {
			findPerson.setValue6(p.getValue6());
		}
		if (p.getValue7() != null && !"".equals(p.getValue7())) {
			findPerson.setValue7(p.getValue7());
		}
		if (p.getValue8() != null && !"".equals(p.getValue8())) {
			findPerson.setValue8(p.getValue8());
		}
		if (p.getValue9() != null && !"".equals(p.getValue9())) {
			findPerson.setValue9(p.getValue9());
		}
		if (p.getValue10() != null && !"".equals(p.getValue10())) {
			findPerson.setValue10(p.getValue10());
		}
		if (p.getOriginDate() != null) {
			findPerson.setOriginDate(p.getOriginDate());
		}
		// if(p.getMpiid() != null) {
		// findPerson.setMpiid(p.getMpiid());
		// }
	}

	@Override
	public void updatePatient(Patient findPatient, Patient updatePatient) throws MPIException {

	}

	@Override
	public void updateHealthcareProvider(HealthcareProvider findHealthcareProvider,
			HealthcareProvider updateHealthcareProvider) throws MPIException {
		if (updateHealthcareProvider.getDepartment() != null && !"".equals(updateHealthcareProvider.getDepartment())) {
			findHealthcareProvider.setDepartment(updateHealthcareProvider.getDepartment());
		}
		if (updateHealthcareProvider.getInstitute() != null && !"".equals(updateHealthcareProvider.getInstitute())) {
			findHealthcareProvider.setInstitute(updateHealthcareProvider.getInstitute());
		}
	}

	@Override
	public List<Person> findPersonsByPDQ(String firstName, String lastName, String year, String month, String day,
			String mpiId, String identifier) throws MPIException {
		try {
			QueryBuilderPersonsByPDQ qb = new QueryBuilderPersonsByPDQ();
			qb.appendPerson("firstName", firstName);
			qb.appendPerson("lastName", lastName);
			qb.appendDate(year, month, day);
			qb.appendMPI("mpiid", mpiId);
			qb.appendLocalId("localIdentifier", identifier);

			@SuppressWarnings("unchecked")
			List<Person> persons = qb.getQuery(em).getResultList();

			return persons;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Person> findPersonsByPDQ(String firstName, String lastName, String year, String month, String day,
			String mpiId, String identifier, String identifierDomain) throws MPIException {
		try {
			QueryBuilderPersonsByPDQ qb = new QueryBuilderPersonsByPDQ();
			if (firstName != null) {
				qb.appendPerson("firstName", firstName.toLowerCase());
			}
			if (lastName != null) {
				qb.appendPerson("lastName", lastName.toLowerCase());
			}
			qb.appendDate(year, month, day);
			qb.appendMPI("mpiid", mpiId);
			qb.appendLocalId("localIdentifier", identifier);
			qb.appendIdentDomain("identDomain", identifierDomain);
			Query query = qb.getQuery(em);
			if (query != null) {
				@SuppressWarnings("unchecked")
				List<Person> persons = query.getResultList();
				return persons;
			} else {
				return new ArrayList<Person>();
			}
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	private static class QueryBuilderPersonsByPDQ {

		Map<String, String> mapPerson = new HashMap<String, String>();
		Map<String, String> mapMPI = new HashMap<String, String>();
		Map<String, String> mapLocalId = new HashMap<String, String>();
		Map<String, String> mapIdentDomain = new HashMap<String, String>();

		public QueryBuilderPersonsByPDQ() {

		}

		public void appendPerson(String key, String value) {
			if (key == null || mapPerson.containsValue(key)) {
				throw new IllegalArgumentException();
			}
			if (value != null && value.trim().length() > 0) {
				mapPerson.put(key, value);
			}
		}

		public void appendMPI(String key, String value) {
			if (key == null || mapMPI.containsValue(key)) {
				throw new IllegalArgumentException();
			}
			if (value != null && value.trim().length() > 0) {
				mapMPI.put(key, value);
			}
		}

		public void appendLocalId(String key, String value) {
			if (key == null || mapLocalId.containsValue(key)) {
				throw new IllegalArgumentException();
			}
			if (value != null && value.trim().length() > 0) {
				mapLocalId.put(key, value);
			}
		}

		public void appendIdentDomain(String key, String value) {
			if (key == null || mapIdentDomain.containsValue(key)) {
				throw new IllegalArgumentException();
			}
			if (value != null && value.trim().length() > 0) {
				mapIdentDomain.put(key, value);
			}
		}

		public void appendDate(String year, String month, String day) throws MPIException {
			try {
				if (day != null && day.trim().length() > 0 && month != null && month.trim().length() > 0
						&& year != null && year.trim().length() > 0) {
					String date = year + "-" + month + "-" + day;
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					java.util.Date birthDate = format.parse(date);
					appendPerson("birthDate", format.format(birthDate));
				} else if ((day != null && day.trim().length() > 0) || (month != null && month.trim().length() > 0)
						|| (year != null && year.trim().length() > 0)) {
					throw new MPIException(ErrorCode.INCOMPLETE_DATE);
				}
			} catch (Exception e) {
				throw new MPIException(ErrorCode.INVALID_DATE_FORMAT_PATTERN, e.getLocalizedMessage(), e);
			}
		}

		public Query getQuery(EntityManager em) {
			Query q = null;
			if (mapMPI.isEmpty() && mapLocalId.isEmpty() && mapPerson.isEmpty() && mapIdentDomain.isEmpty()) {
				return q;
			}

			String query = "SELECT p FROM Person p";

			if (!mapMPI.isEmpty()) {
				query = String.format("%s left join p.personGroup pg", query);
			}

			if (!mapLocalId.isEmpty() || !mapIdentDomain.isEmpty()) {
				query = String.format("%s left join p.localIdentifiers i", query);
			}

			query = String.format("%s WHERE", query);

			for (String key : mapPerson.keySet()) {
				query += " " + createWhere("p", key);
			}

			for (String key : mapMPI.keySet()) {
				query += " " + createWhereForMpiId("pg", key);
			}

			for (String key : mapLocalId.keySet()) {
				query += " " + createWhereForLocalId("i", key);
			}

			for (String key : mapIdentDomain.keySet()) {
				query += " " + createWhereForIdentDomain("i", key);
			}

			q = em.createQuery(query.substring(0, query.length() - 4));

			for (String key : mapPerson.keySet()) {
				String value = mapPerson.get(key);
				q.setParameter(key, value);
			}

			for (String key : mapMPI.keySet()) {
				String value = mapMPI.get(key);
				q.setParameter(key, value);
			}

			for (String key : mapLocalId.keySet()) {
				String value = mapLocalId.get(key);
				q.setParameter(key, value);
			}

			for (String key : mapIdentDomain.keySet()) {
				String value = mapIdentDomain.get(key);
				q.setParameter(key, value);
			}

			return q;
		}

		private String createWhere(String prefix, String key) {
			String where = "LOWER(" + prefix + "." + key + ") like CONCAT('%', :" + key + ", '%') AND";
			return where;
		}

		private String createWhereForLocalId(String prefix, String key) {
			String where = prefix + ".value =:"+key+" AND";
			return where;
		}

		private String createWhereForIdentDomain(String prefix, String key) {
			String where = prefix + ".domain.oid like :" + key + " AND";
			return where;
		}

		private String createWhereForMpiId(String prefix, String key) {
			String where = prefix + ".firstMpi.value = :" + key + " AND ";
			return where;
		}
	}

}
