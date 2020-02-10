CREATE DATABASE epix DEFAULT CHARACTER SET utf8;

USE epix;

CREATE TABLE identifier_domain (
  name varchar(255) NOT NULL,
  label varchar(255) NOT NULL,
  create_timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  description varchar(255) DEFAULT NULL,
  oid varchar(255) NOT NULL,
  PRIMARY KEY (name),
  UNIQUE KEY UK_oid (oid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO identifier_domain VALUES
('MPI','MPI',current_timestamp(3),current_timestamp(3),null,'1.2.276.0.76.3.1.132.1.1.1');

CREATE TABLE identifier (
  identifier_domain_name varchar(255) NOT NULL,
  value varchar(255) NOT NULL,
  active bit(1) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  create_timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  PRIMARY KEY (identifier_domain_name, value),
  KEY `FK_identifier-identifier_domain` (identifier_domain_name),
  CONSTRAINT `FK_identifier-identifier_domain` FOREIGN KEY (identifier_domain_name) REFERENCES identifier_domain (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE source (
  name varchar(255) NOT NULL,
  description varchar(255) DEFAULT NULL,
  label varchar(255) DEFAULT NULL,
  create_timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  PRIMARY KEY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO source VALUES ('dummy_safe_source','dummy because of the default-property "safe_source" in table domain','dummy_safe_source',current_timestamp(3),current_timestamp(3));

CREATE TABLE domain (
  name varchar(255) NOT NULL,
  label varchar(255) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  create_timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  mpi_domain_name varchar(255) NOT NULL,
  safe_source_name varchar(255) NOT NULL,
  config text NOT NULL,
  PRIMARY KEY (name),
  KEY `FK_domain-identifier_domain` (mpi_domain_name),
  KEY `FK_domain-source` (safe_source_name),
  CONSTRAINT `FK_domain-identifier_domain` FOREIGN KEY (mpi_domain_name) REFERENCES identifier_domain (name),
  CONSTRAINT `FK_domain-source` FOREIGN KEY (safe_source_name) REFERENCES source (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE person (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  deactivated bit(1) DEFAULT 0,
  first_mpi_identifier_domain_name varchar(255) NOT NULL,
  first_mpi_value varchar(255) NOT NULL,
  domain_name varchar(255) NOT NULL,
  create_timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  PRIMARY KEY (id),
  KEY `FK_person-identifier` (first_mpi_identifier_domain_name, first_mpi_value),
  KEY `FK_person-domain` (domain_name),
  CONSTRAINT `FK_person-identifier` FOREIGN KEY (first_mpi_identifier_domain_name, first_mpi_value) REFERENCES identifier (identifier_domain_name, value),
  CONSTRAINT `FK_person-domain` FOREIGN KEY (domain_name) REFERENCES domain (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE identity (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  version int(11) DEFAULT NULL,
  date_of_birth date DEFAULT NULL,
  birth_place varchar(255) DEFAULT NULL,
  civil_status varchar(255) DEFAULT NULL,
  degree varchar(255) DEFAULT NULL,
  first_name varchar(255) DEFAULT NULL,
  gender character(1) DEFAULT ' ',
  last_name varchar(255) DEFAULT NULL,
  middle_name varchar(255) DEFAULT NULL,
  mother_tongue varchar(255) DEFAULT NULL,
  mothers_maiden_name varchar(255) DEFAULT NULL,
  nationality varchar(255) DEFAULT NULL,
  prefix varchar(255) DEFAULT NULL,
  race varchar(255) DEFAULT NULL,
  religion varchar(255) DEFAULT NULL,
  suffix varchar(255) DEFAULT NULL,
  external_timestamp timestamp(3) NULL,
  create_timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  value1 varchar(50) DEFAULT NULL,
  value10 varchar(15000) DEFAULT NULL,
  value2 varchar(50) DEFAULT NULL,
  value3 varchar(50) DEFAULT NULL,
  value4 varchar(50) DEFAULT NULL,
  value5 varchar(50) DEFAULT NULL,
  value6 varchar(255) DEFAULT NULL,
  value7 varchar(255) DEFAULT NULL,
  value8 varchar(1000) DEFAULT NULL,
  value9 varchar(1000) DEFAULT NULL,
  person_id bigint(20) NOT NULL,
  source_name varchar(255) NOT NULL,
  forced_reference bit(1) DEFAULT 0,
  deactivated bit(1) DEFAULT 0,
  PRIMARY KEY (id),
  KEY `FK_identity-person` (person_id),
  KEY `FK_identity-source` (source_name),
  CONSTRAINT `FK_identity-source` FOREIGN KEY (source_name) REFERENCES source (name),
  CONSTRAINT `FK_identity-person` FOREIGN KEY (person_id) REFERENCES person (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE person_history (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  deactivated bit(1) DEFAULT 0,
  history_timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  first_mpi_identifier_domain_name varchar(255) NOT NULL,
  first_mpi_value varchar(255) NOT NULL,
  person_id bigint(20) NOT NULL,
  domain_name varchar(255) NOT NULL,
  comment varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY `FK_person_history-identifier` (first_mpi_identifier_domain_name, first_mpi_value),
  KEY `FK_person_history-person` (person_id),
  KEY `FK_person_history-domain` (domain_name),
  CONSTRAINT `FK_person_history-person` FOREIGN KEY (person_id) REFERENCES person (id),
  CONSTRAINT `FK_person_history-identifier` FOREIGN KEY (first_mpi_identifier_domain_name, first_mpi_value) REFERENCES identifier (identifier_domain_name, value),
  CONSTRAINT `FK_person_history-domain` FOREIGN KEY (domain_name) REFERENCES domain (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE identity_history (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  identity_version int(11) DEFAULT NULL,
  date_of_birth datetime DEFAULT NULL,
  birth_place varchar(255) DEFAULT NULL,
  civil_status varchar(255) DEFAULT NULL,
  degree varchar(255) DEFAULT NULL,
  first_name varchar(255) DEFAULT NULL,
  gender character(1) DEFAULT NULL,
  last_name varchar(255) DEFAULT NULL,
  middle_name varchar(255) DEFAULT NULL,
  mother_tongue varchar(255) DEFAULT NULL,
  mothers_maiden_name varchar(255) DEFAULT NULL,
  nationality varchar(255) DEFAULT NULL,
  event char(13) NOT NULL,
  person_id bigint(20) NOT NULL,
  prefix varchar(255) DEFAULT NULL,
  race varchar(255) DEFAULT NULL,
  religion varchar(255) DEFAULT NULL,
  suffix varchar(255) DEFAULT NULL,
  external_timestamp timestamp(3) NULL,
  history_timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  value1 varchar(50) DEFAULT NULL,
  value10 varchar(15000) DEFAULT NULL,
  value2 varchar(50) DEFAULT NULL,
  value3 varchar(50) DEFAULT NULL,
  value4 varchar(50) DEFAULT NULL,
  value5 varchar(50) DEFAULT NULL,
  value6 varchar(255) DEFAULT NULL,
  value7 varchar(255) DEFAULT NULL,
  value8 varchar(1000) DEFAULT NULL,
  value9 varchar(1000) DEFAULT NULL,
  identity_id bigint(20) NOT NULL,
  source_name varchar(255) NOT NULL,
  forced_reference bit(1) DEFAULT 0,
  deactivated bit(1) DEFAULT 0,
  comment varchar(255) DEFAULT NULL,
  matchingScore double DEFAULT 0,
  PRIMARY KEY (id),
  KEY `FK_identity_history-identity` (identity_id),
  KEY `FK_identity_history-source` (source_name),
  KEY `FK_identity_history-person` (person_id),
  CONSTRAINT `FK_identity_history-source` FOREIGN KEY (source_name) REFERENCES source (name),
  CONSTRAINT `FK_identity_history-identity` FOREIGN KEY (identity_id) REFERENCES identity (id),
  CONSTRAINT `FK_identity_history-person` FOREIGN KEY (person_id) REFERENCES person (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE contact (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  version int(11) DEFAULT NULL,
  city varchar(255) DEFAULT NULL,
  country varchar(255) DEFAULT NULL,
  country_code varchar(255) DEFAULT NULL,
  district varchar(255) DEFAULT NULL,
  email varchar(255) DEFAULT NULL,
  phone varchar(255) DEFAULT NULL,
  state varchar(255) DEFAULT NULL,
  street varchar(255) DEFAULT NULL,
  zip_code varchar(255) DEFAULT NULL,
  municipality_key varchar(255) DEFAULT NULL,
  deactivated bit(1) DEFAULT 0,
  external_timestamp timestamp(3) NULL,
  create_timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  identity_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  KEY `FK_contact-identity` (identity_id),
  CONSTRAINT `FK_contact-identity` FOREIGN KEY (identity_id) REFERENCES identity (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE contact_history (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  contact_id bigint(20) NOT NULL,
  contact_version int(11) DEFAULT NULL,
  city varchar(255) DEFAULT NULL,
  country varchar(255) DEFAULT NULL,
  country_code varchar(255) DEFAULT NULL,
  district varchar(255) DEFAULT NULL,
  email varchar(255) DEFAULT NULL,
  phone varchar(255) DEFAULT NULL,
  state varchar(255) DEFAULT NULL,
  street varchar(255) DEFAULT NULL,
  zip_code varchar(255) DEFAULT NULL,
  municipality_key varchar(255) DEFAULT NULL,
  deactivated bit(1) DEFAULT 0,
  external_timestamp timestamp(3) NULL,
  history_timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  identity_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  KEY `FK_contact_history-identity` (identity_id),
  KEY `FK_contact_history-contact` (contact_id),
  CONSTRAINT `FK_contact_history-contact` foreign key (contact_id) references contact (id),
  CONSTRAINT `FK_contact_history-identity` foreign key (identity_id) references identity (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE identity_identifier (
  identity_id bigint(20) NOT NULL,
  identifiers_identifier_domain_name varchar(255) NOT NULL,
  identifiers_value varchar(255) NOT NULL,
  KEY `FK_identity_identifier-identifier` (identifiers_identifier_domain_name,identifiers_value),
  KEY `FK_identity_identifier-identity` (identity_id),
  CONSTRAINT `FK_identity_identifier-identity` FOREIGN KEY (identity_id) REFERENCES identity (id),
  CONSTRAINT `FK_identity_identifier-identifier` FOREIGN KEY (identifiers_identifier_domain_name, identifiers_value) REFERENCES identifier (identifier_domain_name, value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE identity_history_identifier (
  identity_history_id bigint(20) NOT NULL,
  identifiers_identifier_domain_name varchar(255) NOT NULL,
  identifiers_value varchar(255) NOT NULL,
  KEY `FK_identity_history_identifier-identifier` (identifiers_identifier_domain_name, identifiers_value),
  KEY `FK_identity_history_identifier-identity_history` (identity_history_id),
  CONSTRAINT `FK_identity_history_identifier-identity_history` FOREIGN KEY (identity_history_id) REFERENCES identity_history (id),
  CONSTRAINT `FK_identity_history_identifier-identifier` FOREIGN KEY (identifiers_identifier_domain_name, identifiers_value) REFERENCES identifier (identifier_domain_name, value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE identity_preprocessed (
  identity_id bigint(20) NOT NULL,
  date_of_birth char(8) NOT NULL,
  birth_place varchar(255) NOT NULL DEFAULT '',
  civil_status varchar(255) NOT NULL DEFAULT '',
  degree varchar(255) NOT NULL DEFAULT '',
  first_name varchar(255) NOT NULL DEFAULT '',
  gender character(1) DEFAULT ' ',
  last_name varchar(255) NOT NULL DEFAULT '',
  middle_name varchar(255) NOT NULL DEFAULT '',
  mother_tongue varchar(255) NOT NULL DEFAULT '',
  mothers_maiden_name varchar(255) NOT NULL DEFAULT '',
  nationality varchar(255) NOT NULL DEFAULT '',
  external_timestamp timestamp(3) NULL,
  person_id bigint(20) NOT NULL,
  prefix varchar(255) NOT NULL DEFAULT '',
  race varchar(255) NOT NULL DEFAULT '',
  religion varchar(255) NOT NULL DEFAULT '',
  suffix varchar(255) NOT NULL DEFAULT '',
  create_timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  value1 varchar(50) NOT NULL DEFAULT '',
  value10 varchar(15000) NOT NULL DEFAULT '',
  value2 varchar(50) NOT NULL DEFAULT '',
  value3 varchar(50) NOT NULL DEFAULT '',
  value4 varchar(50) NOT NULL DEFAULT '',
  value5 varchar(50) NOT NULL DEFAULT '',
  value6 varchar(255) NOT NULL DEFAULT '',
  value7 varchar(255) NOT NULL DEFAULT '',
  value8 varchar(1000) NOT NULL DEFAULT '',
  value9 varchar(1000) NOT NULL DEFAULT '',
  domain_name varchar(255) NOT NULL,
  forced_reference bit(1) DEFAULT 0,
  deactivated bit(1) DEFAULT 0,
  PRIMARY KEY (identity_id),
  KEY `FK_identity_preprocessed-domain` (domain_name),
  KEY `FK_identity_preprocessed-identity_person` (person_id),
  CONSTRAINT `FK_identity_preprocessed-identity` FOREIGN KEY (identity_id) REFERENCES identity (id),
  CONSTRAINT `FK_identity_preprocessed-identity_person` FOREIGN KEY (person_id) REFERENCES identity (person_id) ON UPDATE CASCADE,
  CONSTRAINT `FK_identity_preprocessed-domain` FOREIGN KEY (domain_name) REFERENCES domain (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE identitylink (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  algorithm varchar(255) DEFAULT NULL,
  threshold double NOT NULL,
  create_timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  dest_identity bigint(20) NOT NULL,
  src_identity bigint(20) NOT NULL,
  PRIMARY KEY (id),
  KEY `FK_identitylink-dest_identity` (dest_identity),
  KEY `FK_identitylink-src_identity` (src_identity),
  CONSTRAINT `FK_identitylink-src_identity` FOREIGN KEY (src_identity) REFERENCES identity (id),
  CONSTRAINT `FK_identitylink-dest_identity` FOREIGN KEY (dest_identity) REFERENCES identity (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE identitylink_history (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  algorithm varchar(255) DEFAULT NULL,
  event char(14) NOT NULL,
  comment varchar(255) DEFAULT NULL,
  threshold double NOT NULL,
  history_timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
  dest_identity bigint(20) NOT NULL,
  dest_person bigint(20) NOT NULL,
  src_identity bigint(20) NOT NULL,
  src_person bigint(20) NOT NULL,
  updated_identity bigint(20) DEFAULT NULL,
  identity_link_id bigint(20) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY `FK_identitylink_history-dest_identity` (dest_identity),
  KEY `FK_identitylink_history-dest_person` (dest_person),
  KEY `FK_identitylink_history-src_identity` (src_identity),
  KEY `FK_identitylink_history-src_person` (src_person),
  KEY `FK_identitylink_history-updated_identity` (updated_identity),
  CONSTRAINT `FK_identitylink_history-updated_identity` FOREIGN KEY (updated_identity) REFERENCES identity (id),
  CONSTRAINT `FK_identitylink_history-src_person` FOREIGN KEY (src_person) REFERENCES person (id),
  CONSTRAINT `FK_identitylink_history-src_identity` FOREIGN KEY (src_identity) REFERENCES identity (id),
  CONSTRAINT `FK_identitylink_history-dest_person` FOREIGN KEY (dest_person) REFERENCES person (id),
  CONSTRAINT `FK_identitylink_history-dest_identity_id` FOREIGN KEY (dest_identity) REFERENCES identity (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS stat_entry (
  STAT_ENTRY_ID BIGINT(20) NOT NULL AUTO_INCREMENT,
  ENTRYDATE VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (STAT_ENTRY_ID)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS stat_value (
  stat_value_id BIGINT(20) DEFAULT NULL,
  stat_value VARCHAR(255) DEFAULT NULL,
  stat_attr VARCHAR(50) DEFAULT NULL,
  INDEX FK_stat_value_stat_value_id (stat_value_id ASC),
  CONSTRAINT FK_stat_value_stat_value_id FOREIGN KEY (stat_value_id) REFERENCES stat_entry (STAT_ENTRY_ID)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE=utf8_bin;

CREATE TABLE sequence
(
   SEQ_NAME varchar(50) PRIMARY KEY NOT NULL,
   SEQ_COUNT decimal(38,0)
)
;


DELIMITER $$

CREATE PROCEDURE updateStats()
begin
	--  add new entry with current timestamp
	INSERT INTO 
		stat_entry (entrydate) values (NOW());
	--  get current count of entries in table stat_entry
	SET @id = (select max(stat_entry_id) from stat_entry);

	--  the tool specific logic follows, inserting entries in table stat_value using the same id
	--  BEGIN OF TOOL SPECIFIC LOGIC 
	INSERT INTO stat_value (stat_value_id,stat_attr,stat_value) values (@id, 'persons',
		(SELECT count(id) FROM person WHERE deactivated is false));
		
	 INSERT INTO stat_value (stat_value_id,stat_attr,stat_value) values (@id, 'identities',
  (SELECT count(*) FROM identity where identity.deactivated = false));

	INSERT INTO stat_value (stat_value_id,stat_attr,stat_value) values (@id, 'possible_matches',
		(SELECT cast(count(il.p1id) as decimal) FROM  ( 
				SELECT p1.id as p1id, p2.id as p2id
				FROM identitylink as il, identity as i1, person as p1, identity as i2, person as p2
				WHERE il.dest_identity = i1.id AND i1.person_id = p1.id AND 
					il.src_identity = i2.id AND i2.person_id = p2.id
				GROUP BY p1.id, p2.id
				) as il));

	INSERT INTO stat_value (stat_value_id,stat_attr,stat_value) values (@id, 'possible_matches_separated',
		(SELECT cast(count(id) as decimal)
			FROM identitylink_history WHERE event = 'SPLIT'));

	INSERT INTO stat_value (stat_value_id,stat_attr,stat_value) values (@id, 'possible_matches_merged',
		(SELECT cast(count(id) as decimal) 
			FROM identitylink_history WHERE event = 'MERGE'));
	--  END OF TOOL SPECIFIC LOGIC 
	--  show and return data sets
	SELECT t1.stat_entry_id as id, t1.entrydate as timestamp, t2.stat_attr as attribut, t2.stat_value as value 
		FROM stat_entry AS t1, stat_value AS t2
		WHERE t1.stat_entry_id = t2.stat_value_id;
end$$

DELIMITER ;


CREATE USER 'epix_user'@'%' IDENTIFIED BY 'epix_password';
GRANT ALL PRIVILEGES ON epix.* TO 'epix_user'@'%';
