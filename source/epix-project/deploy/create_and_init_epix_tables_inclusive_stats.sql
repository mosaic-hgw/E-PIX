CREATE DATABASE `epix` DEFAULT CHARACTER SET utf8;

USE `epix`;

CREATE TABLE `id_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `entry_date` datetime DEFAULT NULL,
  `value` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `id_type` VALUES (1,'EAN13 Barcode',NOW(),'EAN13');

CREATE TABLE `identifier_domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `domain_type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `entry_date` datetime DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `oid` varchar(255) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `identifier_domain` VALUES 
(1,'STANDARD_DOMAIN',NOW(),'MOSAIC','1.2.276.0.76.3.1.132.100.1.1.1.3.0.1.1'),
(2,'MPI_DOMAIN',NOW(),'MPI','1.2.276.0.76.3.1.132.1.1.1');

CREATE TABLE `identifier` (
  `local_identifier` varchar(255) COLLATE utf8_bin NOT NULL,
  `active` bit(1) DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `entry_date` datetime NOT NULL,
  `identifier_type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `sending_application` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `sending_facility` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `domain_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`domain_id`,`local_identifier`),
  KEY `FK_identifier-identifier_domain` (`domain_id`),
  CONSTRAINT `FK_identifier-identifier_domain` FOREIGN KEY (`domain_id`) REFERENCES `identifier_domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `entry_date` datetime DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `idType_id` bigint(20) NOT NULL,
  `mpi_domain` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_project-id_type` (`idType_id`),
  KEY `FK_project-identifier_domain` (`mpi_domain`),
  CONSTRAINT `FK_project-identifier_domain` FOREIGN KEY (`mpi_domain`) REFERENCES `identifier_domain` (`id`),
  CONSTRAINT `FK_project-id_type` FOREIGN KEY (`idType_id`) REFERENCES `id_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `project` VALUES (1,NULL,NOW(),'MOSAIC',1,2);

CREATE TABLE `account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `entry_date` datetime DEFAULT NULL,
  `password` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `username` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `project_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_account-project` (`project_id`),
  CONSTRAINT `FK_account-project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `account` VALUES (1,NOW(),'2ada0888b0e0f030cf14c006adb02ba1260278f4','user',1),(2,NOW(),'63f2ddec821aba7f29f58f6b4b91487824d4775a','epixweb',1);

CREATE TABLE `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `role` VALUES (1,'Standard role','user'),(2,'Administrator role','admin'),(3,'Administrator role','trusty');

CREATE TABLE `account_role` (
  `account_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  KEY `FK_account_role-role` (`role_id`),
  KEY `FK_account_role-account` (`account_id`),
  CONSTRAINT `FK_account_role-account` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_account_role-role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `account_role` VALUES (1,1),(2,1),(1,2);

CREATE TABLE `source` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_source-value` (`value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `source` VALUES (1,'dummy because of the default-property "safe_source" in epix.properties','dummy_safe_source');
INSERT INTO `source` VALUES (2,'dummy for the web-client','EPIX-WEB');

/*!40014 SET FOREIGN_KEY_CHECKS=0 */;

CREATE TABLE `person` (
  `person_type` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_of_birth` date DEFAULT NULL,
  `birthPlace` varchar(255) DEFAULT NULL,
  `civilStatus` varchar(255) DEFAULT NULL,
  `degree` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  `motherTongue` varchar(255) DEFAULT NULL,
  `mothersMaidenName` varchar(255) DEFAULT NULL,
  `nationality` varchar(255) DEFAULT NULL,
  `origin_date` datetime DEFAULT NULL,
  `prefix` varchar(255) DEFAULT NULL,
  `race` varchar(255) DEFAULT NULL,
  `religion` varchar(255) DEFAULT NULL,
  `suffix` varchar(255) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `value1` varchar(255) DEFAULT NULL,
  `value10` varchar(255) DEFAULT NULL,
  `value2` varchar(255) DEFAULT NULL,
  `value3` varchar(255) DEFAULT NULL,
  `value4` varchar(255) DEFAULT NULL,
  `value5` varchar(255) DEFAULT NULL,
  `value6` varchar(255) DEFAULT NULL,
  `value7` varchar(255) DEFAULT NULL,
  `value8` varchar(255) DEFAULT NULL,
  `value9` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `institute` varchar(255) DEFAULT NULL,
  `personGroup_id` bigint(20) NOT NULL,
  `source_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_person-person_group` (`personGroup_id`),
  KEY `FK_person-source` (`source_id`),
  CONSTRAINT `FK_person-source` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`),
  CONSTRAINT `FK_person-person_group` FOREIGN KEY (`personGroup_id`) REFERENCES `person_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `person_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `locked` bit(1) DEFAULT NULL,
  `firstMpi_domain_id` bigint(20) DEFAULT NULL,
  `firstMpi_local_identifier` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `referencePerson_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_person_group-identifier` (`firstMpi_domain_id`,`firstMpi_local_identifier`),
  KEY `FK_person_group-person` (`referencePerson_id`),
  CONSTRAINT `FK_person_group-person` FOREIGN KEY (`referencePerson_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_person_group-identifier` FOREIGN KEY (`firstMpi_domain_id`, `firstMpi_local_identifier`) REFERENCES `identifier` (`domain_id`, `local_identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `person_history` (
  `person_type` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_of_birth` datetime DEFAULT NULL,
  `birthPlace` varchar(255) DEFAULT NULL,
  `civilStatus` varchar(255) DEFAULT NULL,
  `degree` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  `motherTongue` varchar(255) DEFAULT NULL,
  `mothersMaidenName` varchar(255) DEFAULT NULL,
  `nationality` varchar(255) DEFAULT NULL,
  `origin_date` datetime DEFAULT NULL,
  `originalEvent` varchar(255) DEFAULT NULL,
  `person_group_id` bigint(20) NOT NULL,
  `prefix` varchar(255) DEFAULT NULL,
  `race` varchar(255) DEFAULT NULL,
  `religion` varchar(255) DEFAULT NULL,
  `suffix` varchar(255) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `value1` varchar(255) DEFAULT NULL,
  `value10` varchar(255) DEFAULT NULL,
  `value2` varchar(255) DEFAULT NULL,
  `value3` varchar(255) DEFAULT NULL,
  `value4` varchar(255) DEFAULT NULL,
  `value5` varchar(255) DEFAULT NULL,
  `value6` varchar(255) DEFAULT NULL,
  `value7` varchar(255) DEFAULT NULL,
  `value8` varchar(255) DEFAULT NULL,
  `value9` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `institute` varchar(255) DEFAULT NULL,
  `person_id` bigint(20) DEFAULT NULL,
  `source_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_person_history-person` (`person_id`),
  KEY `FK_person_history-source` (`source_id`),
  CONSTRAINT `FK_person_history-source` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`),
  CONSTRAINT `FK_person_history-person` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `person_group_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `locked` bit(1) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `firstMpi_domain_id` bigint(20) DEFAULT NULL,
  `firstMpi_local_identifier` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `personGroup_id` bigint(20) NOT NULL,
  `referencePerson_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_person_group_history-identifier` (`firstMpi_domain_id`,`firstMpi_local_identifier`),
  KEY `FK_person_group_history-person_group` (`personGroup_id`),
  KEY `FK_person_group_history-person` (`referencePerson_id`),
  CONSTRAINT `FK_person_group_history-person` FOREIGN KEY (`referencePerson_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_person_group_history-person_group` FOREIGN KEY (`personGroup_id`) REFERENCES `person_group` (`id`),
  CONSTRAINT `FK_person_group_history-identifier` FOREIGN KEY (`firstMpi_domain_id`, `firstMpi_local_identifier`) REFERENCES `identifier` (`domain_id`, `local_identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40014 SET FOREIGN_KEY_CHECKS=1 */;

CREATE TABLE `contact` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `country_code` varchar(255) DEFAULT NULL,
  `district` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `zip_code` varchar(255) DEFAULT NULL,
  `person_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_contact-person` (`person_id`),
  CONSTRAINT `FK_contact-person` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `contact_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `country_code` varchar(255) DEFAULT NULL,
  `district` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `zip_code` varchar(255) DEFAULT NULL,
  `person_history_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_contact_history-person_history` (`person_history_id`),
  CONSTRAINT `FK_contact_history-person_history` FOREIGN KEY (`person_history_id`) REFERENCES `person_history` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `person_identifier` (
  `persons_id` bigint(20) NOT NULL,
  `localIdentifiers_domain_id` bigint(20) NOT NULL,
  `localIdentifiers_local_identifier` varchar(255) COLLATE utf8_bin NOT NULL,
  KEY `FK_person_identifier-identifier` (`localIdentifiers_domain_id`,`localIdentifiers_local_identifier`),
  KEY `FK_person_identifier-person` (`persons_id`),
  CONSTRAINT `FK_person_identifier-person` FOREIGN KEY (`persons_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_person_identifier-identifier` FOREIGN KEY (`localIdentifiers_domain_id`, `localIdentifiers_local_identifier`) REFERENCES `identifier` (`domain_id`, `local_identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `person_history_identifier` (
  `person_history_id` bigint(20) NOT NULL,
  `identifier_domain_id` bigint(20) NOT NULL,
  `identifier_local_identifier` varchar(255) COLLATE utf8_bin NOT NULL,
  KEY `FK_person_history_identifier-identifier` (`identifier_domain_id`,`identifier_local_identifier`),
  KEY `FK_person_history_identifier-person_history` (`person_history_id`),
  CONSTRAINT `FK_person_history_identifier-person_history` FOREIGN KEY (`person_history_id`) REFERENCES `person_history` (`id`),
  CONSTRAINT `FK_person_history_identifier-identifier` FOREIGN KEY (`identifier_domain_id`, `identifier_local_identifier`) REFERENCES `identifier` (`domain_id`, `local_identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `person_preprocessed` (
  `person_type` varchar(31) NOT NULL,
  `person_id` bigint(20) NOT NULL,
  `date_of_birth` date DEFAULT NULL,
  `birthPlace` varchar(255) DEFAULT NULL,
  `civilStatus` varchar(255) DEFAULT NULL,
  `degree` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  `motherTongue` varchar(255) DEFAULT NULL,
  `mothersMaidenName` varchar(255) DEFAULT NULL,
  `nationality` varchar(255) DEFAULT NULL,
  `origin_date` datetime DEFAULT NULL,
  `person_group_id` bigint(20) DEFAULT NULL,
  `prefix` varchar(255) DEFAULT NULL,
  `race` varchar(255) DEFAULT NULL,
  `religion` varchar(255) DEFAULT NULL,
  `suffix` varchar(255) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `value1` varchar(255) DEFAULT NULL,
  `value10` varchar(255) DEFAULT NULL,
  `value2` varchar(255) DEFAULT NULL,
  `value3` varchar(255) DEFAULT NULL,
  `value4` varchar(255) DEFAULT NULL,
  `value5` varchar(255) DEFAULT NULL,
  `value6` varchar(255) DEFAULT NULL,
  `value7` varchar(255) DEFAULT NULL,
  `value8` varchar(255) DEFAULT NULL,
  `value9` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `institute` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`person_id`),
  CONSTRAINT `FK_person_preprocessed-person` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_person_preprocessed-person_group` FOREIGN KEY (`person_group_id`) REFERENCES `person` (`personGroup_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `personlink` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `algorithm` varchar(255) DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `destPerson_id` bigint(20) DEFAULT NULL,
  `srcPerson_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_personlink-dest_person` (`destPerson_id`),
  KEY `FK_personlink-src_person` (`srcPerson_id`),
  CONSTRAINT `FK_personlink-src_person` FOREIGN KEY (`srcPerson_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_personlink-dest_person` FOREIGN KEY (`destPerson_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `personlink_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `algorithm` varchar(255) DEFAULT NULL,
  `event` varchar(255) DEFAULT NULL,
  `explanation` varchar(255) DEFAULT NULL,
  `personLinkId` bigint(20) DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `destPerson_id` bigint(20) DEFAULT NULL,
  `from_group` bigint(20) DEFAULT NULL,
  `srcPerson_id` bigint(20) DEFAULT NULL,
  `to_group` bigint(20) DEFAULT NULL,
  `updated_person` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_personlink_history-dest_person` (`destPerson_id`),
  KEY `FK_personlink_history-from_person_group` (`from_group`),
  KEY `FK_personlink_history-src_person` (`srcPerson_id`),
  KEY `FK_personlink_history-to_person_group` (`to_group`),
  KEY `FK_personlink_history-upd_person` (`updated_person`),
  CONSTRAINT `FK_personlink_history-upd_person` FOREIGN KEY (`updated_person`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_personlink_history-dest_person` FOREIGN KEY (`destPerson_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_personlink_history-src_person` FOREIGN KEY (`srcPerson_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_personlink_history-to_person_group` FOREIGN KEY (`to_group`) REFERENCES `person_group` (`id`),
  CONSTRAINT `FK_personlink_history-from_person_group` FOREIGN KEY (`from_group`) REFERENCES `person_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE  TABLE IF NOT EXISTS `stat_entry` (
  `STAT_ENTRY_ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `ENTRYDATE` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`STAT_ENTRY_ID`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE  TABLE IF NOT EXISTS `stat_value` (
  `stat_value_id` BIGINT(20) NULL DEFAULT NULL,
  `stat_value` VARCHAR(255) NULL DEFAULT NULL,
  `stat_attr` VARCHAR(50) NULL DEFAULT NULL,
  INDEX `FK_stat_value_stat_value_id` (`stat_value_id` ASC),
  CONSTRAINT `FK_stat_value_stat_value_id`
    FOREIGN KEY (`stat_value_id` )
    REFERENCES `stat_entry` (`STAT_ENTRY_ID` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

DELIMITER $$

CREATE PROCEDURE `updateStats`()
begin
	--  add new entry with current timestamp
	INSERT INTO 
		stat_entry (entrydate) values (NOW());
	--  get current count of entries in table stat_entry
	SET @id = (select max(stat_entry_id) from stat_entry);

	--  the tool specific logic follows, inserting entries in table stat_value using the same id
	--  BEGIN OF TOOL SPECIFIC LOGIC 
	INSERT INTO stat_value (stat_value_id,stat_attr,stat_value) values (@id, 'persons',
		(SELECT count(id) FROM person_group WHERE locked is false));

	INSERT INTO stat_value (stat_value_id,stat_attr,stat_value) values (@id, 'possible_matches',
		(SELECT cast(count(pl.id)/2 as decimal)
			FROM  ( 
				SELECT pl.id as id
				FROM personlink as pl, person as p1, person_group as pg1, person as p2, person_group as pg2
				WHERE pl.destPerson_id = p1.id AND p1.personGroup_id = pg1.id AND 
					pl.srcPerson_id = p2.id AND p2.personGroup_id = pg2.id
				GROUP BY pg1.id, pg2.id
				) as pl));

	INSERT INTO stat_value (stat_value_id,stat_attr,stat_value) values (@id, 'possible_matches_separated',
		(SELECT cast(count(id)/2 as decimal)
			FROM personlink_history WHERE event = 'SPLIT'));

	INSERT INTO stat_value (stat_value_id,stat_attr,stat_value) values (@id, 'possible_matches_merged',
		(SELECT cast(count(id)/2 as decimal) 
			FROM personlink_history WHERE event = 'BIND'));
	--  END OF TOOL SPECIFIC LOGIC 
	--  show and return data sets
	SELECT t1.stat_entry_id as id, t1.entrydate as timestamp, t2.stat_attr as attribut, t2.stat_value as value 
		FROM stat_entry AS t1, stat_value AS t2
		WHERE t1.stat_entry_id = t2.stat_value_id;
end$$

DELIMITER ;

CREATE USER 'epix'@'localhost' IDENTIFIED BY 'epix2012';
GRANT ALL PRIVILEGES ON epix.* TO 'epix'@'localhost' IDENTIFIED BY 'epix2012';
