-- MySQL dump 10.13  Distrib 5.5.40, for debian-linux-gnu (i686)
--
-- Host: localhost    Database: epix_new
-- ------------------------------------------------------
-- Server version	5.5.40-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `entry_date` datetime DEFAULT NULL,
  `password` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `username` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `project_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_dd0e832f71214fdfaa611d95081` (`project_id`),
  CONSTRAINT `FK_dd0e832f71214fdfaa611d95081` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'2014-12-08 11:23:19','2ada0888b0e0f030cf14c006adb02ba1260278f4','user',1),(2,'2014-12-08 11:23:19','9a8d9671c62be43afde041056550b017b32479c5','ganimed',1),(3,'2014-12-08 11:23:19','63f2ddec821aba7f29f58f6b4b91487824d4775a','epixweb',1);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_role`
--

DROP TABLE IF EXISTS `account_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_role` (
  `account_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  KEY `FK_4b97ef4df30a428da09e1d3693b` (`role_id`),
  KEY `FK_0dede0c8a855479fb70cbf593e1` (`account_id`),
  CONSTRAINT `FK_0dede0c8a855479fb70cbf593e1` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`),
  CONSTRAINT `FK_4b97ef4df30a428da09e1d3693b` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_role`
--

LOCK TABLES `account_role` WRITE;
/*!40000 ALTER TABLE `account_role` DISABLE KEYS */;
INSERT INTO `account_role` VALUES (1,1),(3,2),(3,3),(2,1),(3,1),(1,2);
/*!40000 ALTER TABLE `account_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contact`
--

DROP TABLE IF EXISTS `contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contact` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `city` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `country` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `country_code` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `district` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `email` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `phone` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `state` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `street` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `zip_code` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `person_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_22a3ec199e26432b822f703a20d` (`person_id`),
  CONSTRAINT `FK_22a3ec199e26432b822f703a20d` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact`
--

LOCK TABLES `contact` WRITE;
/*!40000 ALTER TABLE `contact` DISABLE KEYS */;
/*!40000 ALTER TABLE `contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contact_history`
--

DROP TABLE IF EXISTS `contact_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contact_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `city` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `country` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `country_code` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `district` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `email` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `phone` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `state` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `street` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `zip_code` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `person_history_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_043bcf7de9a245f7b4ffa56e44c` (`person_history_id`),
  CONSTRAINT `FK_043bcf7de9a245f7b4ffa56e44c` FOREIGN KEY (`person_history_id`) REFERENCES `person_history` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact_history`
--

LOCK TABLES `contact_history` WRITE;
/*!40000 ALTER TABLE `contact_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `contact_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contact_preprocessed`
--

DROP TABLE IF EXISTS `contact_preprocessed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contact_preprocessed` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `city` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `country` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `country_code` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `district` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `email` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `phone` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `state` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `street` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `zip_code` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact_preprocessed`
--

LOCK TABLES `contact_preprocessed` WRITE;
/*!40000 ALTER TABLE `contact_preprocessed` DISABLE KEYS */;
/*!40000 ALTER TABLE `contact_preprocessed` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `id_type`
--

DROP TABLE IF EXISTS `id_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `id_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `entry_date` datetime DEFAULT NULL,
  `value` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `id_type`
--

LOCK TABLES `id_type` WRITE;
/*!40000 ALTER TABLE `id_type` DISABLE KEYS */;
INSERT INTO `id_type` VALUES (1,'EAN13 Barcode','2014-12-08 11:23:20','EAN13');
/*!40000 ALTER TABLE `id_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `identifier`
--

DROP TABLE IF EXISTS `identifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  KEY `FK_078943a4e26b48a5a7b12394772` (`domain_id`),
  CONSTRAINT `FK_078943a4e26b48a5a7b12394772` FOREIGN KEY (`domain_id`) REFERENCES `identifier_domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `identifier`
--

LOCK TABLES `identifier` WRITE;
/*!40000 ALTER TABLE `identifier` DISABLE KEYS */;
INSERT INTO `identifier` VALUES ('HGW_01','',NULL,'2014-12-08 11:28:28','STANDARD','Eclipse',NULL,1),('HGW_02','',NULL,'2014-12-08 11:28:28','STANDARD','Eclipse',NULL,1),('HGW_03','',NULL,'2014-12-08 11:28:28','STANDARD','Eclipse',NULL,1),('HGW_04','',NULL,'2014-12-08 11:28:28','STANDARD','Eclipse',NULL,1),('HGW_05','',NULL,'2014-12-08 11:28:28','STANDARD','Eclipse',NULL,1),('HGW_06','',NULL,'2014-12-08 11:28:28','STANDARD','Eclipse',NULL,1),('HGW_07','',NULL,'2014-12-08 11:28:28','STANDARD','Eclipse',NULL,1),('HGW_08','',NULL,'2014-12-08 11:28:28','STANDARD','Eclipse',NULL,1),('HGW_78','',NULL,'2014-12-08 11:28:29','STANDARD','Eclipse',NULL,1),('HGW_88','',NULL,'2014-12-08 11:28:29','STANDARD','Eclipse',NULL,1),('HGW_89','',NULL,'2014-12-08 11:28:29','STANDARD','Eclipse',NULL,1),('HGW_90','',NULL,'2014-12-08 11:28:29','STANDARD','Eclipse',NULL,1),('1001000000013','',NULL,'2014-12-08 11:28:28','MPI',NULL,NULL,4),('1001000000020','',NULL,'2014-12-08 11:28:28','MPI',NULL,NULL,4),('1001000000037','',NULL,'2014-12-08 11:28:28','MPI',NULL,NULL,4),('1001000000044','',NULL,'2014-12-08 11:28:28','MPI',NULL,NULL,4),('1001000000051','',NULL,'2014-12-08 11:28:28','MPI',NULL,NULL,4),('1001000000068','',NULL,'2014-12-08 11:28:28','MPI',NULL,NULL,4),('1001000000075','',NULL,'2014-12-08 11:28:28','MPI',NULL,NULL,4),('1001000000082','',NULL,'2014-12-08 11:28:28','MPI',NULL,NULL,4),('1001000000099','',NULL,'2014-12-08 11:28:29','MPI',NULL,NULL,4),('1001000000105','',NULL,'2014-12-08 11:28:29','MPI',NULL,NULL,4),('1001000000112','',NULL,'2014-12-08 11:28:30','MPI',NULL,NULL,4);
/*!40000 ALTER TABLE `identifier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `identifier_domain`
--

DROP TABLE IF EXISTS `identifier_domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identifier_domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `domain_type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `entry_date` datetime DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `oid` varchar(255) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `identifier_domain`
--

LOCK TABLES `identifier_domain` WRITE;
/*!40000 ALTER TABLE `identifier_domain` DISABLE KEYS */;
INSERT INTO `identifier_domain` VALUES (1,'STANDARD_DOMAIN','2014-12-08 11:23:20','TEST','1.2.276.0.76.3.1.132.100.1.1.1.3.0.1.1'),(2,'STANDARD_DOMAIN','2014-12-08 11:23:20','TEST A','1.2.276.0.76.3.1.132.100'),(3,'STANDARD_DOMAIN','2014-12-08 11:23:20','TEST B','1.2.276.0.76.3.1.132.100.1.1.1'),(4,'MPI_DOMAIN','2014-12-08 11:23:20','TEST MPI','1.2.276.0.76.3.1.132.1.1.1');
/*!40000 ALTER TABLE `identifier_domain` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person`
--

DROP TABLE IF EXISTS `person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person` (
  `person_type` varchar(31) COLLATE utf8_bin NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_of_birth` date DEFAULT NULL,
  `birthPlace` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `civilStatus` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `degree` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `first_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `gender` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `last_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `middle_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `motherTongue` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `mothersMaidenName` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `nationality` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `origin_date` datetime DEFAULT NULL,
  `prefix` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `race` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `religion` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `suffix` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `value1` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value10` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value2` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value3` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value4` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value5` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value6` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value7` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value8` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value9` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `department` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `institute` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `personGroup_id` bigint(20) NOT NULL,
  `source_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_a526fbd8600c4e27b3d08e5c87b` (`personGroup_id`),
  KEY `FK_7bfe34cb778e4a559719389d0d0` (`source_id`),
  CONSTRAINT `FK_7bfe34cb778e4a559719389d0d0` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`),
  CONSTRAINT `FK_a526fbd8600c4e27b3d08e5c87b` FOREIGN KEY (`personGroup_id`) REFERENCES `person_group` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person`
--

LOCK TABLES `person` WRITE;
/*!40000 ALTER TABLE `person` DISABLE KEYS */;
INSERT INTO `person` VALUES ('Patient',1,'1982-04-21',NULL,NULL,NULL,'Martin','M','Bialke',NULL,NULL,NULL,NULL,'2014-12-08 11:28:31',NULL,NULL,NULL,NULL,'2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,NULL,NULL,1,1),('Patient',2,'1980-09-15',NULL,NULL,NULL,'Konrad','M','Bialke',NULL,NULL,NULL,NULL,'2014-12-08 11:28:31',NULL,NULL,NULL,NULL,'2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,NULL,NULL,2,1),('Patient',3,'1985-11-20',NULL,NULL,NULL,'Angela','F','Bialke',NULL,NULL,NULL,NULL,'2014-12-08 11:28:31',NULL,NULL,NULL,NULL,'2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,NULL,NULL,3,1),('Patient',4,'1954-12-07',NULL,NULL,NULL,'Heinz','M','Bialke',NULL,NULL,NULL,NULL,'2014-12-08 11:28:31',NULL,NULL,NULL,NULL,'2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,NULL,NULL,4,1),('Patient',5,'1982-04-21',NULL,NULL,NULL,'Dieter','M','Nuhr',NULL,NULL,NULL,NULL,'2014-12-08 11:28:31',NULL,NULL,NULL,NULL,'2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,NULL,NULL,5,1),('Patient',6,'1980-09-15',NULL,NULL,NULL,'Olaf','M','Schubert',NULL,NULL,NULL,NULL,'2014-12-08 11:28:31',NULL,NULL,NULL,NULL,'2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,6,NULL,NULL,6,1),('Patient',7,'1985-11-20',NULL,NULL,NULL,'Cindy','F','Marzahn',NULL,NULL,NULL,NULL,'2014-12-08 11:28:32',NULL,NULL,NULL,NULL,'2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,NULL,NULL,7,1),('Patient',8,'1954-12-07',NULL,NULL,NULL,'Johann','M','König',NULL,NULL,NULL,NULL,'2014-12-08 11:28:32',NULL,NULL,NULL,NULL,'2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,NULL,NULL,8,1),('Patient',9,'1982-03-10',NULL,NULL,NULL,'Peter','M','Rau',NULL,NULL,NULL,NULL,'2014-12-08 11:28:32',NULL,NULL,NULL,NULL,'2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,9,1),('Patient',10,'1912-12-12',NULL,NULL,NULL,'Franz-Heinrich','M','Rau',NULL,NULL,NULL,NULL,'2014-12-08 11:28:29',NULL,NULL,NULL,NULL,'2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,NULL,NULL,10,1),('Patient',11,'1942-03-10',NULL,NULL,NULL,'Dieter','M','Rau',NULL,NULL,NULL,NULL,'2014-12-08 11:28:30',NULL,NULL,NULL,NULL,'2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,11,1);
/*!40000 ALTER TABLE `person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_group`
--

DROP TABLE IF EXISTS `person_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `locked` bit(1) DEFAULT NULL,
  `firstMpi_domain_id` bigint(20) DEFAULT NULL,
  `firstMpi_local_identifier` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `referencePerson_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_5bb8d73e794941f0a76cd0dc885` (`firstMpi_domain_id`,`firstMpi_local_identifier`),
  KEY `FK_e7ab9889e8c24a4cb9ed0406b4c` (`referencePerson_id`),
  CONSTRAINT `FK_e7ab9889e8c24a4cb9ed0406b4c` FOREIGN KEY (`referencePerson_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_5bb8d73e794941f0a76cd0dc885` FOREIGN KEY (`firstMpi_domain_id`, `firstMpi_local_identifier`) REFERENCES `identifier` (`domain_id`, `local_identifier`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_group`
--

LOCK TABLES `person_group` WRITE;
/*!40000 ALTER TABLE `person_group` DISABLE KEYS */;
INSERT INTO `person_group` VALUES (1,'\0',4,'1001000000013',1),(2,'\0',4,'1001000000020',2),(3,'\0',4,'1001000000037',3),(4,'\0',4,'1001000000044',4),(5,'\0',4,'1001000000051',5),(6,'\0',4,'1001000000068',6),(7,'\0',4,'1001000000075',7),(8,'\0',4,'1001000000082',8),(9,'\0',4,'1001000000099',9),(10,'\0',4,'1001000000105',10),(11,'\0',4,'1001000000112',11);
/*!40000 ALTER TABLE `person_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_group_history`
--

DROP TABLE IF EXISTS `person_group_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_group_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `locked` bit(1) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `firstMpi_domain_id` bigint(20) DEFAULT NULL,
  `firstMpi_local_identifier` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `personGroup_id` bigint(20) NOT NULL,
  `referencePerson_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_f224cad7d4b441d2a92f4d56ccc` (`firstMpi_domain_id`,`firstMpi_local_identifier`),
  KEY `FK_fe03e62007aa4d14b05c4b524cb` (`personGroup_id`),
  KEY `FK_30b8eb2be03e40538ac9604d2b9` (`referencePerson_id`),
  CONSTRAINT `FK_30b8eb2be03e40538ac9604d2b9` FOREIGN KEY (`referencePerson_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_f224cad7d4b441d2a92f4d56ccc` FOREIGN KEY (`firstMpi_domain_id`, `firstMpi_local_identifier`) REFERENCES `identifier` (`domain_id`, `local_identifier`),
  CONSTRAINT `FK_fe03e62007aa4d14b05c4b524cb` FOREIGN KEY (`personGroup_id`) REFERENCES `person_group` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_group_history`
--

LOCK TABLES `person_group_history` WRITE;
/*!40000 ALTER TABLE `person_group_history` DISABLE KEYS */;
INSERT INTO `person_group_history` VALUES (1,'\0','2014-12-08 11:28:28',4,'1001000000013',1,1),(2,'\0','2014-12-08 11:28:28',4,'1001000000020',2,2),(3,'\0','2014-12-08 11:28:28',4,'1001000000037',3,3),(4,'\0','2014-12-08 11:28:28',4,'1001000000044',4,4),(5,'\0','2014-12-08 11:28:28',4,'1001000000051',5,5),(6,'\0','2014-12-08 11:28:28',4,'1001000000068',6,6),(7,'\0','2014-12-08 11:28:28',4,'1001000000075',7,7),(8,'\0','2014-12-08 11:28:28',4,'1001000000082',8,8),(9,'\0','2014-12-08 11:28:29',4,'1001000000099',9,9),(10,'\0','2014-12-08 11:28:29',4,'1001000000105',10,10),(11,'\0','2014-12-08 11:28:30',4,'1001000000112',11,11);
/*!40000 ALTER TABLE `person_group_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_history`
--

DROP TABLE IF EXISTS `person_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_history` (
  `person_type` varchar(31) COLLATE utf8_bin NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_of_birth` datetime DEFAULT NULL,
  `birthPlace` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `civilStatus` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `degree` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `first_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `gender` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `last_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `middle_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `motherTongue` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `mothersMaidenName` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `nationality` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `origin_date` datetime DEFAULT NULL,
  `originalEvent` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `person_group_id` bigint(20) NOT NULL,
  `prefix` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `race` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `religion` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `suffix` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `value1` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value10` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value2` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value3` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value4` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value5` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value6` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value7` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value8` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value9` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `department` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `institute` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `person_id` bigint(20) DEFAULT NULL,
  `source_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_683d7d74853144259442bb84513` (`person_id`),
  KEY `FK_c157fd911c484f7db15848d1595` (`source_id`),
  CONSTRAINT `FK_c157fd911c484f7db15848d1595` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`),
  CONSTRAINT `FK_683d7d74853144259442bb84513` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_history`
--

LOCK TABLES `person_history` WRITE;
/*!40000 ALTER TABLE `person_history` DISABLE KEYS */;
INSERT INTO `person_history` VALUES ('Patient_History',1,'1982-04-21 00:00:00',NULL,NULL,NULL,'Martin','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:28','NEW',1,'',NULL,NULL,'','2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,1),('Patient_History',2,'1980-09-15 00:00:00',NULL,NULL,NULL,'Konrad','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:28','NEW',2,'',NULL,NULL,'','2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,1),('Patient_History',3,'1985-11-20 00:00:00',NULL,NULL,NULL,'Angela','F','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:28','NEW',3,'',NULL,NULL,'','2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,1),('Patient_History',4,'1954-12-07 00:00:00',NULL,NULL,NULL,'Heinz','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:28','NEW',4,'',NULL,NULL,'','2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,4,1),('Patient_History',5,'1982-04-21 00:00:00',NULL,NULL,NULL,'Dieter','M','Nuhr','',NULL,NULL,NULL,'2014-12-08 11:28:28','NEW',5,'',NULL,NULL,'','2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,1),('Patient_History',6,'1980-09-15 00:00:00',NULL,NULL,NULL,'Olaf','M','Schubert','',NULL,NULL,NULL,'2014-12-08 11:28:28','NEW',6,'',NULL,NULL,'','2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,6,1),('Patient_History',7,'1985-11-20 00:00:00',NULL,NULL,NULL,'Cindy','F','Marzahn','',NULL,NULL,NULL,'2014-12-08 11:28:28','NEW',7,'',NULL,NULL,'','2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,7,1),('Patient_History',8,'1954-12-07 00:00:00',NULL,NULL,NULL,'Johann','M','König','',NULL,NULL,NULL,'2014-12-08 11:28:28','NEW',8,'',NULL,NULL,'','2014-12-08 11:28:28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,8,1),('Patient_History',9,'1982-03-10 00:00:00',NULL,NULL,NULL,'Peter','M','Rau','',NULL,NULL,NULL,'2014-12-08 11:28:29','NEW',9,'',NULL,NULL,'','2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,9,1),('Patient_History',10,'1982-04-21 00:00:00',NULL,NULL,NULL,'Martin','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:29','PERFECTMATCH',1,'',NULL,NULL,'','2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,1),('Patient_History',11,'1980-09-15 00:00:00',NULL,NULL,NULL,'Konrad','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:29','PERFECTMATCH',2,'',NULL,NULL,'','2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,1),('Patient_History',12,'1985-11-20 00:00:00',NULL,NULL,NULL,'Angela','F','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:29','PERFECTMATCH',3,'',NULL,NULL,'','2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,1),('Patient_History',13,'1954-12-07 00:00:00',NULL,NULL,NULL,'Heinz','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:29','PERFECTMATCH',4,'',NULL,NULL,'','2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,4,1),('Patient_History',14,'1982-04-21 00:00:00',NULL,NULL,NULL,'Dieter','M','Nuhr','',NULL,NULL,NULL,'2014-12-08 11:28:29','PERFECTMATCH',5,'',NULL,NULL,'','2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,1),('Patient_History',15,'1980-09-15 00:00:00',NULL,NULL,NULL,'Olaf','M','Schubert','',NULL,NULL,NULL,'2014-12-08 11:28:29','PERFECTMATCH',6,'',NULL,NULL,'','2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,6,1),('Patient_History',16,'1985-11-20 00:00:00',NULL,NULL,NULL,'Cindy','F','Marzahn','',NULL,NULL,NULL,'2014-12-08 11:28:29','PERFECTMATCH',7,'',NULL,NULL,'','2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,7,1),('Patient_History',17,'1954-12-07 00:00:00',NULL,NULL,NULL,'Johann','M','König','',NULL,NULL,NULL,'2014-12-08 11:28:29','PERFECTMATCH',8,'',NULL,NULL,'','2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,8,1),('Patient_History',18,'1912-12-12 00:00:00',NULL,NULL,NULL,'Franz-Heinrich','M','Rau','',NULL,NULL,NULL,'2014-12-08 11:28:29','NEW',10,'',NULL,NULL,'','2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,10,1),('Patient_History',19,'1912-12-12 00:00:00',NULL,NULL,NULL,'Franz-Heinrich','M','Rau','',NULL,NULL,NULL,'2014-12-08 11:28:29','PERFECTMATCH',10,'',NULL,NULL,'','2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,10,1),('Patient_History',20,'1912-12-12 00:00:00',NULL,NULL,NULL,'Franz-Heinrich','M','Rau','',NULL,NULL,NULL,'2014-12-08 11:28:29','PERFECTMATCH',10,'',NULL,NULL,'','2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,10,1),('Patient_History',21,'1982-04-21 00:00:00',NULL,NULL,NULL,'Martin','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:29','PERFECTMATCH',1,'',NULL,NULL,'','2014-12-08 11:28:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,1),('Patient_History',22,'1980-09-15 00:00:00',NULL,NULL,NULL,'Konrad','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:29','PERFECTMATCH',2,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,1),('Patient_History',23,'1985-11-20 00:00:00',NULL,NULL,NULL,'Angela','F','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:30','PERFECTMATCH',3,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,1),('Patient_History',24,'1954-12-07 00:00:00',NULL,NULL,NULL,'Heinz','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:30','PERFECTMATCH',4,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,4,1),('Patient_History',25,'1982-04-21 00:00:00',NULL,NULL,NULL,'Dieter','M','Nuhr','',NULL,NULL,NULL,'2014-12-08 11:28:30','PERFECTMATCH',5,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,1),('Patient_History',26,'1980-09-15 00:00:00',NULL,NULL,NULL,'Olaf','M','Schubert','',NULL,NULL,NULL,'2014-12-08 11:28:30','PERFECTMATCH',6,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,6,1),('Patient_History',27,'1985-11-20 00:00:00',NULL,NULL,NULL,'Cindy','F','Marzahn','',NULL,NULL,NULL,'2014-12-08 11:28:30','PERFECTMATCH',7,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,7,1),('Patient_History',28,'1954-12-07 00:00:00',NULL,NULL,NULL,'Johann','M','König','',NULL,NULL,NULL,'2014-12-08 11:28:30','PERFECTMATCH',8,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,8,1),('Patient_History',29,'1942-03-10 00:00:00',NULL,NULL,NULL,'Dieter','M','Rau','',NULL,NULL,NULL,'2014-12-08 11:28:30','NEW',11,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,11,1),('Patient_History',30,'1982-04-21 00:00:00',NULL,NULL,NULL,'Martin','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:30','PERFECTMATCH',1,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,1),('Patient_History',31,'1980-09-15 00:00:00',NULL,NULL,NULL,'Konrad','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:30','PERFECTMATCH',2,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,1),('Patient_History',32,'1985-11-20 00:00:00',NULL,NULL,NULL,'Angela','F','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:30','PERFECTMATCH',3,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,1),('Patient_History',33,'1954-12-07 00:00:00',NULL,NULL,NULL,'Heinz','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:30','PERFECTMATCH',4,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,4,1),('Patient_History',34,'1982-04-21 00:00:00',NULL,NULL,NULL,'Dieter','M','Nuhr','',NULL,NULL,NULL,'2014-12-08 11:28:30','PERFECTMATCH',5,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,1),('Patient_History',35,'1980-09-15 00:00:00',NULL,NULL,NULL,'Olaf','M','Schubert','',NULL,NULL,NULL,'2014-12-08 11:28:30','PERFECTMATCH',6,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,6,1),('Patient_History',36,'1985-11-20 00:00:00',NULL,NULL,NULL,'Cindy','F','Marzahn','',NULL,NULL,NULL,'2014-12-08 11:28:30','PERFECTMATCH',7,'',NULL,NULL,'','2014-12-08 11:28:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,7,1),('Patient_History',37,'1954-12-07 00:00:00',NULL,NULL,NULL,'Johann','M','König','',NULL,NULL,NULL,'2014-12-08 11:28:30','PERFECTMATCH',8,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,8,1),('Patient_History',38,'1982-04-21 00:00:00',NULL,NULL,NULL,'Martin','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',1,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,1),('Patient_History',39,'1980-09-15 00:00:00',NULL,NULL,NULL,'Konrad','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',2,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,1),('Patient_History',40,'1985-11-20 00:00:00',NULL,NULL,NULL,'Angela','F','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',3,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,1),('Patient_History',41,'1954-12-07 00:00:00',NULL,NULL,NULL,'Heinz','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',4,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,4,1),('Patient_History',42,'1982-04-21 00:00:00',NULL,NULL,NULL,'Dieter','M','Nuhr','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',5,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,1),('Patient_History',43,'1980-09-15 00:00:00',NULL,NULL,NULL,'Olaf','M','Schubert','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',6,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,6,1),('Patient_History',44,'1985-11-20 00:00:00',NULL,NULL,NULL,'Cindy','F','Marzahn','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',7,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,7,1),('Patient_History',45,'1954-12-07 00:00:00',NULL,NULL,NULL,'Johann','M','König','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',8,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,8,1),('Patient_History',46,'1980-09-15 00:00:00',NULL,NULL,NULL,'Olaf','M','Schubert','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',6,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,6,1),('Patient_History',47,'1982-04-21 00:00:00',NULL,NULL,NULL,'Martin','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',1,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,1),('Patient_History',48,'1980-09-15 00:00:00',NULL,NULL,NULL,'Konrad','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',2,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,1),('Patient_History',49,'1985-11-20 00:00:00',NULL,NULL,NULL,'Angela','F','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',3,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,1),('Patient_History',50,'1954-12-07 00:00:00',NULL,NULL,NULL,'Heinz','M','Bialke','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',4,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,4,1),('Patient_History',51,'1982-04-21 00:00:00',NULL,NULL,NULL,'Dieter','M','Nuhr','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',5,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5,1),('Patient_History',52,'1980-09-15 00:00:00',NULL,NULL,NULL,'Olaf','M','Schubert','',NULL,NULL,NULL,'2014-12-08 11:28:31','PERFECTMATCH',6,'',NULL,NULL,'','2014-12-08 11:28:31',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,6,1),('Patient_History',53,'1985-11-20 00:00:00',NULL,NULL,NULL,'Cindy','F','Marzahn','',NULL,NULL,NULL,'2014-12-08 11:28:32','PERFECTMATCH',7,'',NULL,NULL,'','2014-12-08 11:28:32',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,7,1),('Patient_History',54,'1954-12-07 00:00:00',NULL,NULL,NULL,'Johann','M','König','',NULL,NULL,NULL,'2014-12-08 11:28:32','PERFECTMATCH',8,'',NULL,NULL,'','2014-12-08 11:28:32',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,8,1),('Patient_History',55,'1982-03-10 00:00:00',NULL,NULL,NULL,'Peter','M','Rau','',NULL,NULL,NULL,'2014-12-08 11:28:32','PERFECTMATCH',9,'',NULL,NULL,'','2014-12-08 11:28:32',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,9,1);
/*!40000 ALTER TABLE `person_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_history_identifier`
--

DROP TABLE IF EXISTS `person_history_identifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_history_identifier` (
  `person_history_id` bigint(20) NOT NULL,
  `identifier_domain_id` bigint(20) NOT NULL,
  `identifier_local_identifier` varchar(255) COLLATE utf8_bin NOT NULL,
  KEY `FK_254e822af7e842fb943805753f4` (`identifier_domain_id`,`identifier_local_identifier`),
  KEY `FK_f213bab2de05489e85f475bb37f` (`person_history_id`),
  CONSTRAINT `FK_f213bab2de05489e85f475bb37f` FOREIGN KEY (`person_history_id`) REFERENCES `person_history` (`id`),
  CONSTRAINT `FK_254e822af7e842fb943805753f4` FOREIGN KEY (`identifier_domain_id`, `identifier_local_identifier`) REFERENCES `identifier` (`domain_id`, `local_identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_history_identifier`
--

LOCK TABLES `person_history_identifier` WRITE;
/*!40000 ALTER TABLE `person_history_identifier` DISABLE KEYS */;
INSERT INTO `person_history_identifier` VALUES (1,1,'HGW_01'),(2,1,'HGW_02'),(3,1,'HGW_03'),(4,1,'HGW_04'),(5,1,'HGW_05'),(6,1,'HGW_06'),(7,1,'HGW_07'),(8,1,'HGW_08'),(9,1,'HGW_78'),(10,1,'HGW_01'),(11,1,'HGW_02'),(12,1,'HGW_03'),(13,1,'HGW_04'),(14,1,'HGW_05'),(15,1,'HGW_06'),(16,1,'HGW_07'),(17,1,'HGW_08'),(18,1,'HGW_88'),(19,1,'HGW_89'),(20,1,'HGW_90'),(21,1,'HGW_01'),(22,1,'HGW_02'),(23,1,'HGW_03'),(24,1,'HGW_04'),(25,1,'HGW_05'),(26,1,'HGW_06'),(27,1,'HGW_07'),(28,1,'HGW_08'),(30,1,'HGW_01'),(31,1,'HGW_02'),(32,1,'HGW_03'),(33,1,'HGW_04'),(34,1,'HGW_05'),(35,1,'HGW_06'),(36,1,'HGW_07'),(37,1,'HGW_08'),(38,1,'HGW_01'),(39,1,'HGW_02'),(40,1,'HGW_03'),(41,1,'HGW_04'),(42,1,'HGW_05'),(43,1,'HGW_06'),(44,1,'HGW_07'),(45,1,'HGW_08'),(47,1,'HGW_01'),(48,1,'HGW_02'),(49,1,'HGW_03'),(50,1,'HGW_04'),(51,1,'HGW_05'),(52,1,'HGW_06'),(53,1,'HGW_07'),(54,1,'HGW_08'),(55,1,'HGW_78');
/*!40000 ALTER TABLE `person_history_identifier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_identifier`
--

DROP TABLE IF EXISTS `person_identifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_identifier` (
  `persons_id` bigint(20) NOT NULL,
  `localIdentifiers_domain_id` bigint(20) NOT NULL,
  `localIdentifiers_local_identifier` varchar(255) COLLATE utf8_bin NOT NULL,
  KEY `FK_c9e41ae625f34e3d8c1f142ba9b` (`localIdentifiers_domain_id`,`localIdentifiers_local_identifier`),
  KEY `FK_bb74d20a392f4930886ca1c8ec7` (`persons_id`),
  CONSTRAINT `FK_bb74d20a392f4930886ca1c8ec7` FOREIGN KEY (`persons_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_c9e41ae625f34e3d8c1f142ba9b` FOREIGN KEY (`localIdentifiers_domain_id`, `localIdentifiers_local_identifier`) REFERENCES `identifier` (`domain_id`, `local_identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_identifier`
--

LOCK TABLES `person_identifier` WRITE;
/*!40000 ALTER TABLE `person_identifier` DISABLE KEYS */;
INSERT INTO `person_identifier` VALUES (1,1,'HGW_01'),(2,1,'HGW_02'),(3,1,'HGW_03'),(4,1,'HGW_04'),(5,1,'HGW_05'),(6,1,'HGW_06'),(7,1,'HGW_07'),(8,1,'HGW_08'),(9,1,'HGW_78'),(10,1,'HGW_88'),(10,1,'HGW_89'),(10,1,'HGW_90');
/*!40000 ALTER TABLE `person_identifier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_preprocessed`
--

DROP TABLE IF EXISTS `person_preprocessed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_preprocessed` (
  `person_type` varchar(31) COLLATE utf8_bin NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_of_birth` date DEFAULT NULL,
  `birthPlace` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `civilStatus` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `degree` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `first_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `gender` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `last_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `middle_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `motherTongue` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `mothersMaidenName` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `nationality` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `origin_date` datetime DEFAULT NULL,
  `prefix` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `race` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `religion` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `suffix` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `value1` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value10` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value2` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value3` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value4` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value5` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value6` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value7` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value8` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value9` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `department` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `institute` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `person_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_fc834fe2bf844229b54fa90fddb` (`person_id`),
  KEY `FK_bf23260ae15d44f0a351acf8a74` (`person_id`),
  CONSTRAINT `FK_bf23260ae15d44f0a351acf8a74` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_preprocessed`
--

LOCK TABLES `person_preprocessed` WRITE;
/*!40000 ALTER TABLE `person_preprocessed` DISABLE KEYS */;
INSERT INTO `person_preprocessed` VALUES ('Patient_Preprocessed',1,'1982-04-21','','','','MARTIN','M','BIALKE','','','','','2014-12-08 11:28:31','','','','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),('Patient_Preprocessed',2,'1980-09-15','','','','KONRAD','M','BIALKE','','','','','2014-12-08 11:28:31','','','','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),('Patient_Preprocessed',3,'1985-11-20','','','','ANGELA','F','BIALKE','','','','','2014-12-08 11:28:31','','','','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3),('Patient_Preprocessed',4,'1954-12-07','','','','HEINZ','M','BIALKE','','','','','2014-12-08 11:28:31','','','','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,4),('Patient_Preprocessed',5,'1982-04-21','','','','DIETER','M','NUHR','','','','','2014-12-08 11:28:31','','','','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5),('Patient_Preprocessed',6,'1980-09-15','','','','OLAF','M','SCHUBERT','','','','','2014-12-08 11:28:31','','','','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,6),('Patient_Preprocessed',7,'1985-11-20','','','','CINDY','F','MARZAHN','','','','','2014-12-08 11:28:32','','','','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,7),('Patient_Preprocessed',8,'1954-12-07','','','','JOHANN','M','KOENIG','','','','','2014-12-08 11:28:32','','','','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,8),('Patient_Preprocessed',9,'1982-03-10','','','','PETER','M','RAU','','','','','2014-12-08 11:28:32','','','','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,9),('Patient_Preprocessed',10,'1912-12-12','','','','FRANZHEINRICH','M','RAU','','','','','2014-12-08 11:28:29','','','','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,10),('Patient_Preprocessed',11,'1942-03-10','','','','DIETER','M','RAU','','','','','2014-12-08 11:28:30','','','','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,11);
/*!40000 ALTER TABLE `person_preprocessed` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personlink`
--

DROP TABLE IF EXISTS `personlink`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `personlink` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `algorithm` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `destPerson_id` bigint(20) DEFAULT NULL,
  `srcPerson_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_aacf31f0db2e457db47c6b2b3c3` (`destPerson_id`),
  KEY `FK_09f9d01048fa4a4683f40f79a1c` (`srcPerson_id`),
  CONSTRAINT `FK_09f9d01048fa4a4683f40f79a1c` FOREIGN KEY (`srcPerson_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_aacf31f0db2e457db47c6b2b3c3` FOREIGN KEY (`destPerson_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `personlink`
--

LOCK TABLES `personlink` WRITE;
/*!40000 ALTER TABLE `personlink` DISABLE KEYS */;
/*!40000 ALTER TABLE `personlink` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personlink_history`
--

DROP TABLE IF EXISTS `personlink_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `personlink_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `algorithm` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `event` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `explanation` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `personLinkId` bigint(20) DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `destPerson_id` bigint(20) DEFAULT NULL,
  `from_group` bigint(20) DEFAULT NULL,
  `srcPerson_id` bigint(20) DEFAULT NULL,
  `to_group` bigint(20) DEFAULT NULL,
  `updated_person` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_82cacc3f3f814069b0d8cb1d303` (`destPerson_id`),
  KEY `FK_0e274e22dda441c6a14e7d7eba6` (`from_group`),
  KEY `FK_353b31dc198f4f1d933ce56f8a8` (`srcPerson_id`),
  KEY `FK_5beba0ff82f14894b663e2a1f9d` (`to_group`),
  KEY `FK_2e1c3d8f299c4448b7efbba16bb` (`updated_person`),
  CONSTRAINT `FK_2e1c3d8f299c4448b7efbba16bb` FOREIGN KEY (`updated_person`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_0e274e22dda441c6a14e7d7eba6` FOREIGN KEY (`from_group`) REFERENCES `person_group` (`id`),
  CONSTRAINT `FK_353b31dc198f4f1d933ce56f8a8` FOREIGN KEY (`srcPerson_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_5beba0ff82f14894b663e2a1f9d` FOREIGN KEY (`to_group`) REFERENCES `person_group` (`id`),
  CONSTRAINT `FK_82cacc3f3f814069b0d8cb1d303` FOREIGN KEY (`destPerson_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `personlink_history`
--

LOCK TABLES `personlink_history` WRITE;
/*!40000 ALTER TABLE `personlink_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `personlink_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `entry_date` datetime DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `idType_id` bigint(20) NOT NULL,
  `mpi_domain` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_91cc12b722834a879cdbc2e674e` (`idType_id`),
  KEY `FK_57bd9391cbbf45348bc42c63deb` (`mpi_domain`),
  CONSTRAINT `FK_57bd9391cbbf45348bc42c63deb` FOREIGN KEY (`mpi_domain`) REFERENCES `identifier_domain` (`id`),
  CONSTRAINT `FK_91cc12b722834a879cdbc2e674e` FOREIGN KEY (`idType_id`) REFERENCES `id_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` VALUES (1,NULL,'2014-10-27 11:26:45','GANI_MED',1,4);
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'Standard role','user'),(2,'Administrator role','admin'),(3,'Administrator role','trusty');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `source`
--

DROP TABLE IF EXISTS `source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `source` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value` varchar(255) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_9d7506eb0a324cb4abdd30ac61b` (`value`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `source`
--

LOCK TABLES `source` WRITE;
/*!40000 ALTER TABLE `source` DISABLE KEYS */;
INSERT INTO `source` VALUES (1,NULL,'GANI_MED'),(2,NULL,'nicht_GANI_MED');
/*!40000 ALTER TABLE `source` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-12-08 11:28:56
