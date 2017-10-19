package org.emau.icmvc.ganimed.epix.common.model;

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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author Christian Schack
 * @since 2011
 * 
 *        <p>
 *        Java class for ErrorCode complex type.
 * 
 *        <p>
 *        The following schema fragment specifies the expected content contained within this class.
 * 
 *        <pre>
 * &lt;complexType name="ErrorCode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ErrorCode", namespace = "http://www.ganimed.icmvc.emau.org/epix/common/model", propOrder = { "code", "message" })
public class ErrorCode implements Serializable {

	private static final long serialVersionUID = 2364728141790963690L;

	protected int code = UNDEFINED;

	@XmlElement(required = true)
	protected String message;

	public ErrorCode() {
	}

	public ErrorCode(int code) {
		this.code = code;
	}

	/**
	 * Gets the value of the code property.
	 * 
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Sets the value of the code property.
	 * 
	 */
	public void setCode(int value) {
		this.code = value;
	}

	/**
	 * Gets the value of the message property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMessage() {
		switch (code) {
		case UNDEFINED:
			return UNDEFINED_MSG;
		case OK:
			return OK_MSG;
		case MULTIPLE_MPIID_ERROR:
			return MULTIPLE_MPIID_ERROR_MSG;
		case MPIID_ALREADYEXISTS:
			return MPIID_ALREADYEXISTS_MSG;
		case IDTYPE_ALREADY_EXISTS:
			return IDTYPE_ALREADY_EXISTS_MSG;
		case PATIENT_GENDER_MISSING:
			return PATIENT_GENDER_MISSING_MSG;
		case USERNAME_ALREADY_EXISTS:
			return USERNAME_ALREADY_EXISTS_MSG;
		case USERNAME_EMPTY:
			return USERNAME_EMPTY_MSG;
		case PASSWORD_EMPTY:
			return PASSWORD_EMPTY_MSG;
		case PROJECT_ALREADY_EXISTS:
			return PROJECT_ALREADY_EXISTS_MSG;
		case IDENTIFIERDOMAIN_ALREADY_EXISTS:
			return IDENTIFIERDOMAIN_ALREADY_EXISTS_MSG;
		case IDENTIFIERDOMAIN_NOT_EXISTS:
			return IDENTIFIERDOMAIN_NOT_EXISTS_MSG;
		case NOIDENTIFIERDOMAIN:
			return NOIDENTIFIERDOMAIN_MSG;
		case IDENTIFIER_ALREADY_EXISTS:
			return IDENTIFIER_ALREADY_EXISTS_MSG;
		case ROLE_ALREADY_EXISTS:
			return ROLE_ALREADY_EXISTS_MSG;
		case PERSON_FOR_LOCALIDENTIFIER_ALREADY_EXISTS:
			return PERSON_FOR_LOCALIDENTIFIER_ALREADY_EXISTS_MSG;
		case PERSISTENCE_ERROR:
			return PERSTISTENCE_ERR_MSG;
		case IDENTICAL_MATCH_FOR_PERSON:
			return IDENTICAL_MATCH_FOR_PERSON_MSG;
		case CRITICAL_MATCH_FOR_PERSON:
			return CRITICAL_MATCH_FOR_PERSON_MSG;
		case NO_VALID_MATCHING_CRITERIA_FOUND:
			return NO_VALID_MATCHING_CRITERIA_FOUND_MSG;
		case INVALID_OPERATION_PARAMETER:
			return INVALID_OPERATION_PARAMETER_MSG;
		case INVALID_DATE_FORMAT_PATTERN:
			return INVALID_DATE_FORMAT_PATTERN_MSG;
		case USERNAME_NOT_EXIST:
			return USERNAME_NOT_EXIST_MSG;
		case INTERNAL_ERROR:
			return INTERNAL_ERROR_MSG;
		case EPIXCLIENT_INSTANTIATION_ERROR:
			return EPIXCLIENT_INSTANTIATION_ERROR_MSG;
		case DEDUPLICATION_ERROR:
			return DEDUPLICATION_ERROR_MSG;
		case IDENTIFIERDOMAIN_NULL_ERROR:
			return IDENTIFIERDOMAIN_NULL_ERROR_MSG;
		case MPIREQUEST_NULL_ERROR:
			return MPIREQUEST_NULL_ERROR_MSG;
		case REQUESTENTRY_LIST_EMPTY_ERROR:
			return REQUESTENTRY_LIST_EMPTY_MSG;
		case NO_MATCHABLE_ERROR:
			return NO_MATCHABLE_MSG;
		case NO_ATTRIBUTES_DEFINED:
			return NO_ATTRIBUTES_DEFINED_MSG;
		case ILLEGAL_PERSON_TYPE:
			return ILLEGAL_PERSON_TYPE_MSG;
		case DIFFERENT_PERSONS_FOR_SAME_LOCAL_IDENTIFIER:
			return DIFFERENT_PERSONS_FOR_SAME_LOCAL_IDENTIFIER_MSG;
		case PROJECT_ID_NOT_KNOWN:
			return PROJECT_ID_NOT_KNOWN_MSG;
		case ADD_PERSON_ERROR:
			return ADD_PERSON_ERROR_MSG;
		case NO_MPI_DOMAIN_AVAILABLE:
			return NO_MPI_DOMAIN_AVAILABLE_MSG;
		case MULTI_MPI_DOMAIN_AVAILABLE:
			return MULTI_MPI_DOMAIN_AVAILABLE_MSG;
		case OLD_UPDATE_ERROR:
			return OLD_UPDATE_ERROR_MSG;
		case NO_PERSON_FOUND:
			return NO_PERSON_FOUND_MSG;
		case NO_MPIID_FOR_UPDATE:
			return NO_MPIID_FOR_UPDATE_MSG;
		case DIFFERENT_PERSON_TYPES_FOUND:
			return DIFFERENT_PERSON_TYPES_FOUND_MSG;
		case IDENTIFIER_VALUE_NULL_ERROR:
			return IDENTIFIER_VALUE_NULL_ERROR_MSG;
		case PERFECT_MATCH_FOR_PERSON:
			return PERFECT_MATCH_FOR_PERSON_MSG;
		case UPDATE_ERROR:
			return UPDATE_ERROR_MSG;
		case PERFECT_MATCH_ERROR:
			return PERFECT_MATCH_ERROR_MSG;
		case DIFFERENT_PERSON_GROUP_FOR_SAME_LOCAL_IDENTIFIER:
			return DIFFERENT_PERSON_GROUP_FOR_SAME_LOCAL_IDENTIFIER_MSG;
		case MATCH_SAME_LOCAL_IDENTIFIER_ERROR:
			return MATCH_SAME_LOCAL_IDENTIFIER_ERROR_MSG;
		case MATCH_ERROR:
			return MATCH_ERROR_MSG;
		case CHECK_REFERENCE_PERSON_ERROR:
			return CHECK_REFERENCE_PERSON_ERROR_MSG;
		case DIFFERENT_PERSON_GROUP_FOR_UPDATE:
			return DIFFERENT_PERSON_GROUP_FOR_UPDATE_MSG;
		case MERGE_PERSON_ERROR:
			return MERGE_PERSON_ERROR_MSG;
		case UNKNOWN_PERSON_EXEPTION:
			return UNKNOWN_PERSON_EXEPTION_MSG;
		case UNKNOWN_ALIAS_EXEPTION:
			return UNKNOWN_ALIAS_EXEPTION_MSG;
		case SAME_PERSON_FOR_MERGE:
			return SAME_PERSON_FOR_MERGE_MSG;
		case UNPARSEABLE_DBID_ERROR:
			return UNPARSEABLE_DBID_ERROR_MSG;
		case MULTIPLE_MATCH_FOR_SAME_LOCAL_IDENTIFIER_IN_SAME_GROUP:
			return MULTIPLE_MATCH_FOR_SAME_LOCAL_IDENTIFIER_IN_SAME_GROUP_MSG;
		case SAFE_IDENTIFIERDOMAIN_NOT_EXISTS:
			return SAFE_IDENTIFIERDOMAIN_NOT_EXISTS_MSG;
		case SET_SAFE_IDENTIFIERDOMAIN_ERROR:
			return SET_SAFE_IDENTIFIERDOMAIN_ERROR_MSG;
		case NO_MATCH_FOR_EXISTING_IDENTIFIER:
			return NO_MATCH_FOR_EXISTING_IDENTIFIER_MSG;
		case INCOMPLETE_DATE:
			return INCOMPLETE_DATE_MSG;
		case NO_CONFIGURATION_FOR_EPIX:
			return NO_CONFIGURATION_FOR_EPIX_MSG;
		case IDENTIFIER_OR_DOMAIN_NULL:
			return IDENTIFIER_OR_DOMAIN_NULL_MSG;
		case SOURCE_NOT_EXISTS:
			return SOURCE_NOT_EXISTS_MSG;
		case MPIID_NOT_EXISTS:
			return MPIID_NOT_EXISTS_MSG;
		case MPI_INITIALISING:
			return MPI_INITIALISING_MSG;

		default:
			return String.valueOf(UNDEFINED_MSG + " for ErrorCode " + code);
		}
	}

	/**
	 * Sets the value of the message property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Error codes
	 */

	public static final int UNDEFINED = -1;

	public static final int OK = 0;

	public static final int MULTIPLE_MPIID_ERROR = 1;

	public static final int MPIID_ALREADYEXISTS = 2;

	public static final int IDTYPE_ALREADY_EXISTS = 3;

	public static final int PATIENT_GENDER_MISSING = 4;

	public static final int USERNAME_ALREADY_EXISTS = 5;

	public static final int USERNAME_EMPTY = 6;

	public static final int PASSWORD_EMPTY = 7;

	public static final int PROJECT_ALREADY_EXISTS = 8;

	public static final int IDENTIFIERDOMAIN_ALREADY_EXISTS = 9;

	public static final int IDENTIFIERDOMAIN_NOT_EXISTS = 10;

	public static final int NOIDENTIFIERDOMAIN = 11;

	public static final int IDENTIFIER_ALREADY_EXISTS = 12;

	public static final int ROLE_ALREADY_EXISTS = 13;

	public static final int PERSON_FOR_LOCALIDENTIFIER_ALREADY_EXISTS = 14;

	public static final int PERSISTENCE_ERROR = 15;

	public static final int IDENTICAL_MATCH_FOR_PERSON = 16;

	public static final int CRITICAL_MATCH_FOR_PERSON = 17;

	public static final int NO_VALID_MATCHING_CRITERIA_FOUND = 18;

	public static final int INVALID_DATE_FORMAT_PATTERN = 19;

	public static final int INVALID_OPERATION_PARAMETER = 20;

	public static final int USERNAME_NOT_EXIST = 21;

	public static final int INTERNAL_ERROR = 22;

	public static final int EPIXCLIENT_INSTANTIATION_ERROR = 23;

	public static final int DEDUPLICATION_ERROR = 24;

	public static final int IDENTIFIERDOMAIN_NULL_ERROR = 25;

	public static final int MPIREQUEST_NULL_ERROR = 26;

	public static final int REQUESTENTRY_LIST_EMPTY_ERROR = 27;

	public static final int NO_MATCHABLE_ERROR = 28;

	public static final int ILLEGAL_PERSON_TYPE = 29;

	public static final int DIFFERENT_PERSON_TYPES_FOUND = 30;

	public static final int NO_ATTRIBUTES_DEFINED = 31;

	public static final int DIFFERENT_PERSONS_FOR_SAME_LOCAL_IDENTIFIER = 32;

	public static final int PROJECT_ID_NOT_KNOWN = 33;

	public static final int ADD_PERSON_ERROR = 34;

	public static final int NO_MPI_DOMAIN_AVAILABLE = 35;

	public static final int MULTI_MPI_DOMAIN_AVAILABLE = 36;
	
	public static final int OLD_UPDATE_ERROR = 37;
	
	public static final int NO_PERSON_FOUND = 38;
	
	public static final int NO_MPIID_FOR_UPDATE = 39;
	
	public static final int IDENTIFIER_VALUE_NULL_ERROR =40;
	
	public static final int PERFECT_MATCH_FOR_PERSON = 41;
	
	public static final int UPDATE_ERROR = 42;
	
	public static final int PERFECT_MATCH_ERROR = 43;
	
	public static final int DIFFERENT_PERSON_GROUP_FOR_SAME_LOCAL_IDENTIFIER = 44;
	
	public static final int MATCH_SAME_LOCAL_IDENTIFIER_ERROR = 45;
	
	public static final int MATCH_ERROR = 46;
	
	public static final int CHECK_REFERENCE_PERSON_ERROR = 47;
	
	public static final int DIFFERENT_PERSON_GROUP_FOR_UPDATE = 48;
	
	public static final int MERGE_PERSON_ERROR = 49;
	
	public static final int UNKNOWN_PERSON_EXEPTION = 50;
	
	public static final int UNKNOWN_ALIAS_EXEPTION = 51;
	
	public static final int SAME_PERSON_FOR_MERGE = 52;
	
	public static final int UNPARSEABLE_DBID_ERROR = 53;
	
	public static final int MULTIPLE_MATCH_FOR_SAME_LOCAL_IDENTIFIER_IN_SAME_GROUP = 54;
	
	public static final int SAFE_IDENTIFIERDOMAIN_NOT_EXISTS = 55;
	
	public static final int SET_SAFE_IDENTIFIERDOMAIN_ERROR = 56;
	
	public static final int NO_MATCH_FOR_EXISTING_IDENTIFIER = 57;
	
	public static final int INCOMPLETE_DATE = 58;
	
	public static final int NO_CONFIGURATION_FOR_EPIX = 59;
	
	public static final int IDENTIFIER_OR_DOMAIN_NULL = 60;
	
	public static final int SOURCE_NOT_EXISTS = 61;
	
	public static final int MPIID_NOT_EXISTS = 62;

	public static final int MPI_INITIALISING = 63;

	// Error Messages

	public static final String UNDEFINED_MSG = "Error undefined";

	public static final String OK_MSG = "OK";

	public static final String MULTIPLE_MPIID_ERROR_MSG = "There are multiple MPI-IDs";

	public static final String MPIID_ALREADYEXISTS_MSG = "The given MPI-ID already exists!";

	public static final String IDTYPE_ALREADY_EXISTS_MSG = "The given IDType already exists.";

	public static final String PATIENT_GENDER_MISSING_MSG = "The gender of the Patient must not be null or empty.";

	public static final String USERNAME_ALREADY_EXISTS_MSG = "Username already exists.";

	public static final String USERNAME_EMPTY_MSG = "Username must not be null or empty.";

	public static final String PASSWORD_EMPTY_MSG = "Password must not be null or empty.";

	public static final String PROJECT_ALREADY_EXISTS_MSG = "Project already exists.";

	public static final String IDENTIFIERDOMAIN_ALREADY_EXISTS_MSG = "IdentifierDomain already exists.";

	public static final String IDENTIFIERDOMAIN_NOT_EXISTS_MSG = "IdentifierDomain does not exists.";

	public static final String NOIDENTIFIERDOMAIN_MSG = "Given Identifier must be associated to a IdentifierDomain!";

	public static final String IDENTIFIER_ALREADY_EXISTS_MSG = "Identifier already exists for the given domain.";

	public static final String ROLE_ALREADY_EXISTS_MSG = "Role already exists.";

	public static final String PERSON_FOR_LOCALIDENTIFIER_ALREADY_EXISTS_MSG = "A Person already exists for the given local identifier and identifier domain";

	public static final String PERSISTENCE_ERROR_MSG = "Error during database access.";

	public static final String IDENTICAL_MATCH_FOR_PERSON_MSG = "Matching algorithm found identical match for given person!";

	public static final String PERSTISTENCE_ERR_MSG = "Internal data access object error ";

	public static final String CRITICAL_MATCH_FOR_PERSON_MSG = "Matching algorithm found critical match for given person!";

	public static final String NO_VALID_MATCHING_CRITERIA_FOUND_MSG = "The matching algorithm expecetd at least one valid matching criteria!";

	public static final String INVALID_DATE_FORMAT_PATTERN_MSG = "Invalid date format pattern!";

	public static final String INVALID_OPERATION_PARAMETER_MSG = "Invalid operation parameter.";

	public static final String USERNAME_NOT_EXIST_MSG = "Username does not exist!";

	public static final String INTERNAL_ERROR_MSG = "Internal Error";

	public static final String EPIXCLIENT_INSTANTIATION_ERROR_MSG = "Error creating EPIX client";

	public static final String DEDUPLICATION_ERROR_MSG = "Internal error during deduplication processing occured!";

	public static final String IDENTIFIERDOMAIN_NULL_ERROR_MSG = "IdentifierDomain must not be null.";

	public static final String MPIREQUEST_NULL_ERROR_MSG = "MPIRequest must not be null";

	public static final String REQUESTENTRY_LIST_EMPTY_MSG = "No request entry found within MPIRequest";

	public static final String NO_MATCHABLE_MSG = "matching algorithm does not work because of demographics datas not complete";
	
	public static final String ILLEGAL_PERSON_TYPE_MSG = "Illegal person type found.";
	
	public static final String DIFFERENT_PERSON_TYPES_FOUND_MSG = "The requested person dosen't fit the person found in database.";

	public static final String NO_ATTRIBUTES_DEFINED_MSG = "Not all attributes must not be null";

	public static final String DIFFERENT_PERSONS_FOR_SAME_LOCAL_IDENTIFIER_MSG = "A Person already exists for the given local identifier and identifier "
			+ "domain, but is a different one to the requested person.";

	public static final String PROJECT_ID_NOT_KNOWN_MSG = "ProjectId not known. Projects can not be addet at runtime.";

	public static final String ADD_PERSON_ERROR_MSG = "An error occured while saving person entry.";

	public static final String NO_MPI_DOMAIN_AVAILABLE_MSG = "no mpi-domain available";

	public static final String MULTI_MPI_DOMAIN_AVAILABLE_MSG = "multiple mpi-domain available";
	
	public static final String OLD_UPDATE_ERROR_MSG = "update is older than actual database entry";
	
	public static final String NO_PERSON_FOUND_MSG = "No person found with given search criteria";
	
	public static final String NO_MPIID_FOR_UPDATE_MSG = "Update can not be execute without MPIId.";
	
	public static final String IDENTIFIER_VALUE_NULL_ERROR_MSG = "Identifier is failed or not available.";
	
	public static final String PERFECT_MATCH_FOR_PERSON_MSG = "Matching algorithm found perfect match for given person!";
	
	public static final String UPDATE_ERROR_MSG = "Update-Process is failed";
	
	public static final String PERFECT_MATCH_ERROR_MSG = "Error during perfectMatch";
	
	public static final String MATCH_SAME_LOCAL_IDENTIFIER_ERROR_MSG = "Error while matching same local identifier.";
	
	public static final String DIFFERENT_PERSON_GROUP_FOR_SAME_LOCAL_IDENTIFIER_MSG = "A Person already exists for the given local identifier" +
			" and identifier domain, but belongs to an other person group.";
	
	public static final String MATCH_ERROR_MSG = "Error while matching process.";
	
	public static final String CHECK_REFERENCE_PERSON_ERROR_MSG = "Error while checking if person is reference for group.";
	
	public static final String DIFFERENT_PERSON_GROUP_FOR_UPDATE_MSG = "Error: Matching process found match or perfectMatch which is related to another personGroup.";

	public static final String MERGE_PERSON_ERROR_MSG = "Error while merging persons.";
	
	public static final String UNKNOWN_PERSON_EXEPTION_MSG = "Peson with given Id can not be found.";
	
	public static final String UNKNOWN_ALIAS_EXEPTION_MSG = "Alias with given Id can not be found.";
	
	public static final String SAME_PERSON_FOR_MERGE_MSG = "Cannot merge same personId.";
	
	public static final String UNPARSEABLE_DBID_ERROR_MSG = "The given database id can not be parsed into a Long value.";
	
	public static final String MULTIPLE_MATCH_FOR_SAME_LOCAL_IDENTIFIER_IN_SAME_GROUP_MSG = "The given identifier already exists and matches with more than one"
			+ " person in the same group.";
	
	public static final String SAFE_IDENTIFIERDOMAIN_NOT_EXISTS_MSG = "No safe identifierDomain found.";
	
	public static final String SET_SAFE_IDENTIFIERDOMAIN_ERROR_MSG = "Error while checking safe identifierDomain.";
	
	public static final String NO_MATCH_FOR_EXISTING_IDENTIFIER_MSG = "The given identifier already existing in database, but the requested person "
			+ "does not match any person in the database.";
	
	public static final String INCOMPLETE_DATE_MSG = "The given date was incomplete.";
	
	public static final String NO_CONFIGURATION_FOR_EPIX_MSG = "The configuration file epix.properties is missing.";
	
	public static final String IDENTIFIER_OR_DOMAIN_NULL_MSG = "Either identifier and identifierDomain has to be filled or none of them.";
	
	public static final String SOURCE_NOT_EXISTS_MSG = "The source does not exist or no source was committed.";
	
	public static final String MPIID_NOT_EXISTS_MSG = "The given MPI-ID does not exist.";
	
	public static final String MPI_INITIALISING_MSG = "The system is initialising. Try again later.";
	
	public static String getMessage(int code) {
		ErrorCode ec = new ErrorCode(code);
		return ec.getMessage();
	}

}
