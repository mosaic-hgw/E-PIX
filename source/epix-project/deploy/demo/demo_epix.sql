USE epix;

-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server Version:               8.0.14 - MySQL Community Server - GPL
-- Server Betriebssystem:        Linux
-- HeidiSQL Version:             11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Exportiere Daten aus Tabelle epix.contact: ~0 rows (ungefähr)
/*!40000 ALTER TABLE `contact` DISABLE KEYS */;
INSERT INTO `contact` (`id`, `version`, `city`, `country`, `country_code`, `district`, `email`, `phone`, `state`, `street`, `zip_code`, `municipality_key`, `date_of_move_in`, `date_of_move_out`, `deactivated`, `external_timestamp`, `create_timestamp`, `timestamp`, `identity_id`) VALUES
  (1, 1, 'Greifswald', NULL, NULL, NULL, NULL, NULL, NULL, 'Ellernholzstr. 1', '18489', NULL, NULL, NULL, b'0', NULL, '2019-03-29 14:12:40.431', '2019-03-29 14:12:40.431', 1);
/*!40000 ALTER TABLE `contact` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.contact_history: ~0 rows (ungefähr)
/*!40000 ALTER TABLE `contact_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `contact_history` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.domain: ~0 rows (ungefähr)
/*!40000 ALTER TABLE `domain` DISABLE KEYS */;
INSERT INTO `domain` (`name`, `label`, `description`, `create_timestamp`, `timestamp`, `mpi_domain_name`, `safe_source_name`, `config`) VALUES
  ('Demo', 'Demo', 'Demo-Domäne', '2019-03-29 14:11:35.224', '2019-03-29 14:11:35.224', 'MPI', 'dummy_safe_source', '<?xml version="1.0" encoding="UTF-8"?>\r\n<!-- ###license-information-start### E-PIX - Enterprise Patient Identifier Cross-referencing __ Copyright (C) 2009 - 2017 The MOSAIC Project - Institut \r\n  fuer Community Medicine der Universitaetsmedizin Greifswald - mosaic-projekt@uni-greifswald.de concept and implementation c. schack, l. geidel, d. langner, \r\n  g. koetzschke web client a. blumentritt please cite our publications http://dx.doi.org/10.3414/ME14-01-0133 http://dx.doi.org/10.1186/s12967-015-0545-6 \r\n  __ This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the \r\n  Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that it will \r\n be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public \r\n  License for more details. You should have received a copy of the GNU Affero General Public License along with this program. If not, see <http://www.gnu.org/licenses/>. \r\n  ###license-information-end### -->\r\n\r\n<ma:MatchingConfiguration xmlns:ma="http://www.ttp.icmvc.emau.org/deduplication/config/model"\r\n  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"\r\n xsi:schemaLocation="http://www.ttp.icmvc.emau.org/deduplication/config/model matching-config-2.9.0.xsd ">\r\n <matching-mode>MATCHING_IDENTITIES</matching-mode>\r\n  <mpi-generator>org.emau.icmvc.ttp.epix.gen.impl.EAN13Generator</mpi-generator>\r\n  <mpi-prefix>1001</mpi-prefix>\r\n <use-notifications>false</use-notifications>\r\n  <limit-search-to-reduce-memory-consumption>false</limit-search-to-reduce-memory-consumption>\r\n\r\n  <required-fields>\r\n   <name>firstName</name>\r\n    <name>lastName</name>\r\n   <name>birthDate</name>\r\n    <name>gender</name>\r\n </required-fields>\r\n <value-fields-mapping>\r\n  <value6>Bloomfilter NUM-Projekt A</value6>\r\n  <value8>Bloomfilter MII-Projekt X</value8>\r\n </value-fields-mapping>\r\n <privacy> \r\n  <bloomfilter-config>\r\n   <algorithm>org.emau.icmvc.ttp.deduplication.impl.bloomfilter.RandomHashingStrategy</algorithm>\r\n   <field>value6</field>\r\n   <length>1000</length>\r\n   <ngrams>2</ngrams>\r\n   <bits-per-ngram>15</bits-per-ngram>\r\n   <fold>1</fold>\r\n   <alphabet>ABCDEFGHIJKLMNOPQRSTUVWXYZ .-0123456789mfoux</alphabet>\r\n   <balanced>\r\n    <seed>4623829476</seed>\r\n   </balanced>\r\n   <source-field>\r\n    <name>firstName</name>\r\n    <seed>456542343</seed>\r\n   </source-field>\r\n   <source-field>\r\n    <name>lastName</name>\r\n    <seed>374027465</seed>\r\n   </source-field>\r\n   <source-field>\r\n    <name>gender</name>\r\n    <seed>29465653282</seed>\r\n   </source-field>\r\n  </bloomfilter-config>\r\n  <bloomfilter-config>\r\n   <algorithm>org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategy</algorithm>\r\n   <field>value8</field>\r\n   <length>500</length>\r\n   <ngrams>2</ngrams>\r\n   <bits-per-ngram>15</bits-per-ngram>\r\n   <source-field>\r\n    <name>firstName</name>\r\n    <salt-field>birthDate</salt-field>\r\n   </source-field>\r\n   <source-field>\r\n    <name>lastName</name>\r\n    <salt-field>birthDate</salt-field>\r\n   </source-field>\r\n   <source-field>\r\n    <name>birthDate</name>\r\n    <salt-value>ah36K340F3#x3790</salt-value>\r\n   </source-field>\r\n   <source-field>\r\n    <name>gender</name>\r\n    <salt-value>Q2fh-Fk2#CjP+s5#</salt-value>\r\n   </source-field>\r\n  </bloomfilter-config>\r\n </privacy>\r\n  <preprocessing-config>\r\n    <preprocessing-field>\r\n     <field-name>firstName</field-name>\r\n      <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>?</input-pattern>\r\n        <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>é</input-pattern>\r\n       <output-pattern>e</output-pattern>\r\n      </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>Dr.</input-pattern>\r\n        <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>Prof.</input-pattern>\r\n        <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>med.</input-pattern>\r\n       <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>rer.</input-pattern>\r\n       <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>nat.</input-pattern>\r\n       <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>Ing.</input-pattern>\r\n       <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>Dipl.</input-pattern>\r\n        <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>,</input-pattern>\r\n        <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>-</input-pattern>\r\n        <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <complex-transformation-type xsi:type="ma:ComplexTransformation">\r\n       <qualified-class-name>org.emau.icmvc.ttp.deduplication.preprocessing.impl.ToUpperCaseTransformation</qualified-class-name>\r\n      </complex-transformation-type>\r\n      <complex-transformation-type xsi:type="ma:ComplexTransformation">\r\n       <qualified-class-name>org.emau.icmvc.ttp.deduplication.preprocessing.impl.CharsMutationTransformation</qualified-class-name>\r\n      </complex-transformation-type>\r\n    </preprocessing-field>\r\n    <preprocessing-field>\r\n     <field-name>lastName</field-name>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>?</input-pattern>\r\n        <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>ÃƒÂƒÃ‚ÂƒÃƒÂ‚Ã‚Â©</input-pattern>\r\n       <output-pattern>e</output-pattern>\r\n      </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>Dr.</input-pattern>\r\n        <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>Prof.</input-pattern>\r\n        <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>med.</input-pattern>\r\n       <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>rer.</input-pattern>\r\n       <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>nat.</input-pattern>\r\n       <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>Ing.</input-pattern>\r\n       <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>Dipl.</input-pattern>\r\n        <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>,</input-pattern>\r\n        <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern> </input-pattern>\r\n        <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <simple-transformation-type xsi:type="ma:SimpleTransformation">\r\n       <input-pattern>-</input-pattern>\r\n        <output-pattern></output-pattern>\r\n     </simple-transformation-type>\r\n     <complex-transformation-type xsi:type="ma:ComplexTransformation">\r\n       <qualified-class-name>org.emau.icmvc.ttp.deduplication.preprocessing.impl.ToUpperCaseTransformation</qualified-class-name>\r\n      </complex-transformation-type>\r\n      <complex-transformation-type xsi:type="ma:ComplexTransformation">\r\n       <qualified-class-name>org.emau.icmvc.ttp.deduplication.preprocessing.impl.CharsMutationTransformation</qualified-class-name>\r\n      </complex-transformation-type>\r\n    </preprocessing-field>\r\n  </preprocessing-config>\r\n <matching>\r\n    <threshold-possible-match>1</threshold-possible-match>\r\n    <threshold-automatic-match>14.5</threshold-automatic-match>\r\n   <use-cemfim>false</use-cemfim>\r\n    <parallel-matching-after>1000</parallel-matching-after>\r\n   <number-of-threads-for-matching>4</number-of-threads-for-matching>\r\n    <field>\r\n     <name>firstName</name>\r\n      <blocking-threshold>0.4</blocking-threshold>\r\n      <matching-threshold>0.8</matching-threshold>\r\n      <weight>8</weight>\r\n      <algorithm>org.emau.icmvc.ttp.deduplication.impl.LevenshteinAlgorithm</algorithm>\r\n     <multiple-values>\r\n       <separator> </separator>\r\n        <penalty-not-a-perfect-match>0.1</penalty-not-a-perfect-match>\r\n        <penalty-one-short>0.1</penalty-one-short>\r\n        <penalty-both-short>0.2</penalty-both-short>\r\n      </multiple-values>\r\n    </field>\r\n    <field>\r\n     <name>lastName</name>\r\n     <matching-threshold>0.8</matching-threshold>\r\n      <weight>6</weight>\r\n      <algorithm>org.emau.icmvc.ttp.deduplication.impl.LevenshteinAlgorithm</algorithm>\r\n   </field>\r\n    <field>\r\n     <name>gender</name>\r\n     <matching-threshold>0.75</matching-threshold>\r\n     <weight>3</weight>\r\n      <algorithm>org.emau.icmvc.ttp.deduplication.impl.LevenshteinAlgorithm</algorithm>\r\n   </field>\r\n    <field>\r\n     <name>birthDate</name>\r\n      <blocking-threshold>0.6</blocking-threshold>\r\n      <blocking-mode>NUMBERS</blocking-mode>\r\n      <matching-threshold>1</matching-threshold>\r\n      <weight>9</weight>\r\n      <algorithm>org.emau.icmvc.ttp.deduplication.impl.LevenshteinAlgorithm</algorithm>\r\n   </field>\r\n  </matching>\r\n</ma:MatchingConfiguration>');
/*!40000 ALTER TABLE `domain` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.identifier: ~6 rows (ungefähr)
/*!40000 ALTER TABLE `identifier` DISABLE KEYS */;
INSERT INTO `identifier` (`identifier_domain_name`, `value`, `active`, `description`, `create_timestamp`) VALUES
  ('MPI', '1001000000011', b'1', 'generated MPI id', '2019-03-29 14:12:40.431'),
  ('MPI', '1001000000028', b'1', 'generated MPI id', '2019-03-29 14:13:08.924'),
  ('MPI', '1001000000035', b'1', 'generated MPI id', '2019-03-29 14:13:41.402'),
  ('MPI', '1001000000042', b'1', 'generated MPI id', '2019-03-29 14:14:39.853');
/*!40000 ALTER TABLE `identifier` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.identity: ~6 rows (ungefähr)
/*!40000 ALTER TABLE `identity` DISABLE KEYS */;
INSERT INTO `identity` (`id`, `version`, `date_of_birth`, `birth_place`, `vital_status`, `date_of_death`, `civil_status`, `degree`, `first_name`, `gender`, `last_name`, `middle_name`, `mother_tongue`, `mothers_maiden_name`, `nationality`, `prefix`, `race`, `religion`, `suffix`, `external_timestamp`, `create_timestamp`, `timestamp`, `value1`, `value10`, `value2`, `value3`, `value4`, `value5`, `value6`, `value7`, `value8`, `value9`, `person_id`, `source_name`, `forced_reference`, `deactivated`) VALUES
  (1, 2, '1990-01-01', 'Greifswald', 1, NULL, NULL, NULL, 'Max', 'm', 'Meier', NULL, NULL, 'Gartenfeld', NULL, NULL, NULL, NULL, NULL, NULL, '2019-03-29 14:12:40.431', '2022-03-15 16:41:43.048', NULL, NULL, NULL, NULL, NULL, NULL, 'oHspDeEbts4Mw0izHYC1ucJ9zqsWvEwjOqjIuADRrICvs+Wku4FEbh9oT1smD3YV2fpPdItpmaY4X7gdTWkb6kGE/dP3ayvDSTX20HG58C6UZnt54GC5Yex+Cmp4CKMHB/tLK4eJCkcDSLsNq9OF8vQ3pZipfhpHA3e6Z8M=', NULL, 'cY3DwfHth8G00Br+wIkJjJU+cVkYtgU5Skvc3NxdUZECMU4SqFKXeGGEdaTJoPGxbnlASDsRnVRBGCBbOFIO', NULL, 1, 'dummy_safe_source', b'0', b'0'),
  (2, 2, '1900-01-01', 'Greifswald', 1, NULL, NULL, NULL, 'Max', 'm', 'Maier', NULL, NULL, 'Gartenfeld', NULL, NULL, NULL, NULL, NULL, NULL, '2019-03-29 14:13:08.924', '2022-03-15 16:41:43.327', NULL, NULL, NULL, NULL, NULL, NULL, '4HtoD+EbNs4JwkiHHYCVueZ9jqsUvEZrOqjI6ADTrIAdMeWk+4FM7h5sx5siD/Y12dpPdIdp2S4oX7gZT2kb6kGEvdv3awrDSzXWlHG58H6dZntx4mixZW5uCmpYCKMnB/NLK4eJikcCTLNtqdOFNPQ2pNi4fhhHB3eaZ88=', NULL, 'YIZSQFDogkegRGi4ZqmAGNUeMQgc5sQ4AArGFdjdE5UAMUIDvEMFWfOlX7f8YfFhRmtCeSIDif3/EUZZDHIC', NULL, 2, 'dummy_safe_source', b'0', b'0'),
  (3, 2, '1980-12-03', NULL, 1, NULL, NULL, NULL, 'Max', 'm', 'Mustermann', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2019-03-29 14:13:41.402', '2022-03-15 16:41:43.360', NULL, NULL, NULL, NULL, NULL, NULL, 'untpLem4Ns4A0lmHDQA1muh5nKuUvmZvsv7ImODQKSEdsfQk6gVM7BfsV/oiBxN0mf8LBMfL0G7w/bgU4v0f3gmG+ar9awnSQbeWkGGrIny1ZDl7+0AyIW5qFEppCqs3F3FDCwaJikcADPfsqceF9LByrNyY/j1mE3fOb8c=', NULL, 'epx4gLPskvuie0i73ouhzp7oOQA9jR68L6im3OF8ENujmGOlnCAXWbvgzSSorfGj5Trv/IDzLqxyE496PHYK', NULL, 3, 'dummy_safe_source', b'0', b'0'),
  (4, 2, '1983-11-17', NULL, 1, NULL, NULL, NULL, 'Maria', 'f', 'Musterfrau', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2019-03-29 14:14:39.853', '2022-03-15 16:41:43.395', NULL, NULL, NULL, NULL, NULL, NULL, 'gnlrqSmaduyNgV3zQcgpmaByrTmW2nwrpeIInNMWAbSfEVQkw0yefxIhV3cmJkKUSc7aDEO+1ebxX+EGy+2U+yEEK3ye73NDaH/HpnHrpGydbx9+e8A7w2w2ggqpGK+HRFdFC22bigdIRagHmXOkdWxbmJSIVhvzI9fm+8s=', NULL, '6NBC/3jGgsQMaYycpzK545/6eaytzT6uD+i1lssm/MKtGDv0GhArueGp1lkZ1c0DSOn7aEabgZ3qd8i+0lQH', NULL, 4, 'dummy_safe_source', b'0', b'0');
/*!40000 ALTER TABLE `identity` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.identitylink: ~3 rows (ungefähr)
/*!40000 ALTER TABLE `identitylink` DISABLE KEYS */;
INSERT INTO `identitylink` (`id`, `algorithm`, `threshold`, `create_timestamp`, `dest_identity`, `src_identity`) VALUES
  (1, 'org.emau.icmvc.ttp.deduplication.FellegiSunterAlgorithm', 14.044444508022732, '2019-03-29 14:13:08.924', 1, 2);
/*!40000 ALTER TABLE `identitylink` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.identitylink_history: ~2 rows (ungefähr)
/*!40000 ALTER TABLE `identitylink_history` DISABLE KEYS */;
INSERT INTO `identitylink_history` (`id`, `algorithm`, `event`, `comment`, `threshold`, `history_timestamp`, `dest_identity`, `dest_person`, `src_identity`, `src_person`, `updated_identity`, `identity_link_id`) VALUES
  (1, 'org.emau.icmvc.ttp.deduplication.FellegiSunterAlgorithm', 'SPLIT', 'Nach telefonischer Rückfrage geklärt', 1.2643678264867395, '2019-03-29 14:14:09.490', 1, 1, 3, 3, NULL, 2),
  (2, 'org.emau.icmvc.ttp.deduplication.FellegiSunterAlgorithm', 'SPLIT', 'Nach telefonischer Rückfrage geklärt', 1.2643678264867395, '2019-03-29 14:14:19.072', 2, 2, 3, 3, NULL, 3);
/*!40000 ALTER TABLE `identitylink_history` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.identity_history: ~8 rows (ungefähr)
/*!40000 ALTER TABLE `identity_history` DISABLE KEYS */;
INSERT INTO `identity_history` (`id`, `identity_version`, `date_of_birth`, `birth_place`, `vital_status`, `date_of_death`, `civil_status`, `degree`, `first_name`, `gender`, `last_name`, `middle_name`, `mother_tongue`, `mothers_maiden_name`, `nationality`, `event`, `person_id`, `prefix`, `race`, `religion`, `suffix`, `external_timestamp`, `history_timestamp`, `value1`, `value10`, `value2`, `value3`, `value4`, `value5`, `value6`, `value7`, `value8`, `value9`, `identity_id`, `source_name`, `forced_reference`, `deactivated`, `comment`, `matchingScore`) VALUES
  (1, 0, '1990-01-01 00:00:00', 'Greifswald', 1, NULL, NULL, NULL, 'Max', 'm', 'Meier', '', NULL, 'Gartenfeld', NULL, 'NEW', 1, '', NULL, NULL, '', NULL, '2019-03-29 14:12:40.431', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 'dummy_safe_source', b'0', b'0', NULL, 0),
  (2, 0, '1900-01-01 00:00:00', 'Greifswald', 1, NULL, NULL, NULL, 'Max', 'm', 'Maier', '', NULL, 'Gartenfeld', NULL, 'NEW', 2, '', NULL, NULL, '', NULL, '2019-03-29 14:13:08.924', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 2, 'dummy_safe_source', b'0', b'0', NULL, 0),
  (3, 0, '1980-12-03 00:00:00', NULL, 1, NULL, NULL, NULL, 'Max', 'm', 'Mustermann', '', NULL, NULL, NULL, 'NEW', 3, '', NULL, NULL, '', NULL, '2019-03-29 14:13:41.402', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 3, 'dummy_safe_source', b'0', b'0', NULL, 0),
  (4, 0, '1983-11-17 00:00:00', NULL, 1, NULL, NULL, NULL, 'Maria', 'f', 'Musterfrau', '', NULL, NULL, NULL, 'NEW', 4, '', NULL, NULL, '', NULL, '2019-03-29 14:14:39.853', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 4, 'dummy_safe_source', b'0', b'0', NULL, 0),
	(151, 1, '1990-01-01 00:00:00', 'Greifswald', 1, NULL, NULL, NULL, 'Max', 'm', 'Meier', '', NULL, 'Gartenfeld', NULL, 'UPDATE', 1, '', NULL, NULL, '', NULL, '2022-03-15 16:41:43.048', NULL, NULL, NULL, NULL, NULL, NULL, 'oHspDeEbts4Mw0izHYC1ucJ9zqsWvEwjOqjIuADRrICvs+Wku4FEbh9oT1smD3YV2fpPdItpmaY4X7gdTWkb6kGE/dP3ayvDSTX20HG58C6UZnt54GC5Yex+Cmp4CKMHB/tLK4eJCkcDSLsNq9OF8vQ3pZipfhpHA3e6Z8M=', NULL, 'cY3DwfHth8G00Br+wIkJjJU+cVkYtgU5Skvc3NxdUZECMU4SqFKXeGGEdaTJoPGxbnlASDsRnVRBGCBbOFIO', NULL, 1, 'dummy_safe_source', b'0', b'0', 'Bloom filter added retroactively', 1000),
	(152, 1, '1900-01-01 00:00:00', 'Greifswald', 1, NULL, NULL, NULL, 'Max', 'm', 'Maier', '', NULL, 'Gartenfeld', NULL, 'UPDATE', 2, '', NULL, NULL, '', NULL, '2022-03-15 16:41:43.327', NULL, NULL, NULL, NULL, NULL, NULL, '4HtoD+EbNs4JwkiHHYCVueZ9jqsUvEZrOqjI6ADTrIAdMeWk+4FM7h5sx5siD/Y12dpPdIdp2S4oX7gZT2kb6kGEvdv3awrDSzXWlHG58H6dZntx4mixZW5uCmpYCKMnB/NLK4eJikcCTLNtqdOFNPQ2pNi4fhhHB3eaZ88=', NULL, 'YIZSQFDogkegRGi4ZqmAGNUeMQgc5sQ4AArGFdjdE5UAMUIDvEMFWfOlX7f8YfFhRmtCeSIDif3/EUZZDHIC', NULL, 2, 'dummy_safe_source', b'0', b'0', 'Bloom filter added retroactively', 1000),
	(153, 1, '1980-12-03 00:00:00', NULL, 1, NULL, NULL, NULL, 'Max', 'm', 'Mustermann', '', NULL, NULL, NULL, 'UPDATE', 3, '', NULL, NULL, '', NULL, '2022-03-15 16:41:43.360', NULL, NULL, NULL, NULL, NULL, NULL, 'untpLem4Ns4A0lmHDQA1muh5nKuUvmZvsv7ImODQKSEdsfQk6gVM7BfsV/oiBxN0mf8LBMfL0G7w/bgU4v0f3gmG+ar9awnSQbeWkGGrIny1ZDl7+0AyIW5qFEppCqs3F3FDCwaJikcADPfsqceF9LByrNyY/j1mE3fOb8c=', NULL, 'epx4gLPskvuie0i73ouhzp7oOQA9jR68L6im3OF8ENujmGOlnCAXWbvgzSSorfGj5Trv/IDzLqxyE496PHYK', NULL, 3, 'dummy_safe_source', b'0', b'0', 'Bloom filter added retroactively', 1000),
	(154, 1, '1983-11-17 00:00:00', NULL, 1, NULL, NULL, NULL, 'Maria', 'f', 'Musterfrau', '', NULL, NULL, NULL, 'UPDATE', 4, '', NULL, NULL, '', NULL, '2022-03-15 16:41:43.395', NULL, NULL, NULL, NULL, NULL, NULL, 'gnlrqSmaduyNgV3zQcgpmaByrTmW2nwrpeIInNMWAbSfEVQkw0yefxIhV3cmJkKUSc7aDEO+1ebxX+EGy+2U+yEEK3ye73NDaH/HpnHrpGydbx9+e8A7w2w2ggqpGK+HRFdFC22bigdIRagHmXOkdWxbmJSIVhvzI9fm+8s=', NULL, '6NBC/3jGgsQMaYycpzK545/6eaytzT6uD+i1lssm/MKtGDv0GhArueGp1lkZ1c0DSOn7aEabgZ3qd8i+0lQH', NULL, 4, 'dummy_safe_source', b'0', b'0', 'Bloom filter added retroactively', 1000);
/*!40000 ALTER TABLE `identity_history` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.identity_history_identifier: ~0 rows (ungefähr)
/*!40000 ALTER TABLE `identity_history_identifier` DISABLE KEYS */;
/*!40000 ALTER TABLE `identity_history_identifier` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.identity_identifier: ~0 rows (ungefähr)
/*!40000 ALTER TABLE `identity_identifier` DISABLE KEYS */;
/*!40000 ALTER TABLE `identity_identifier` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.identity_preprocessed: ~6 rows (ungefähr)
/*!40000 ALTER TABLE `identity_preprocessed` DISABLE KEYS */;
INSERT INTO `identity_preprocessed` (`identity_id`, `date_of_birth`, `birth_place`, `civil_status`, `degree`, `first_name`, `gender`, `last_name`, `middle_name`, `mother_tongue`, `mothers_maiden_name`, `nationality`, `external_timestamp`, `person_id`, `prefix`, `race`, `religion`, `suffix`, `create_timestamp`, `timestamp`, `value1`, `value10`, `value2`, `value3`, `value4`, `value5`, `value6`, `value7`, `value8`, `value9`, `domain_name`, `forced_reference`, `deactivated`) VALUES
  (1, '19900101', 'Greifswald', '', '', 'MAX', 'm', 'MEIER', '', '', 'Gartenfeld', '', NULL, 1, '', '', '', '', '2019-03-29 14:12:40.431', '2019-03-29 14:12:40.431', '', '', '', '', '', '', 'oHspDeEbts4Mw0izHYC1ucJ9zqsWvEwjOqjIuADRrICvs+Wku4FEbh9oT1smD3YV2fpPdItpmaY4X7gdTWkb6kGE/dP3ayvDSTX20HG58C6UZnt54GC5Yex+Cmp4CKMHB/tLK4eJCkcDSLsNq9OF8vQ3pZipfhpHA3e6Z8M=', '', 'cY3DwfHth8G00Br+wIkJjJU+cVkYtgU5Skvc3NxdUZECMU4SqFKXeGGEdaTJoPGxbnlASDsRnVRBGCBbOFIO', '', 'Demo', b'0', b'0'),
  (2, '19000101', 'Greifswald', '', '', 'MAX', 'm', 'MAIER', '', '', 'Gartenfeld', '', NULL, 2, '', '', '', '', '2019-03-29 14:13:08.924', '2019-03-29 14:13:08.924', '', '', '', '', '', '', '4HtoD+EbNs4JwkiHHYCVueZ9jqsUvEZrOqjI6ADTrIAdMeWk+4FM7h5sx5siD/Y12dpPdIdp2S4oX7gZT2kb6kGEvdv3awrDSzXWlHG58H6dZntx4mixZW5uCmpYCKMnB/NLK4eJikcCTLNtqdOFNPQ2pNi4fhhHB3eaZ88=', '', 'YIZSQFDogkegRGi4ZqmAGNUeMQgc5sQ4AArGFdjdE5UAMUIDvEMFWfOlX7f8YfFhRmtCeSIDif3/EUZZDHIC', '', 'Demo', b'0', b'0'),
  (3, '19801203', '', '', '', 'MAX', 'm', 'MUSTERMANN', '', '', '', '', NULL, 3, '', '', '', '', '2019-03-29 14:13:41.402', '2019-03-29 14:13:41.402', '', '', '', '', '', '', 'untpLem4Ns4A0lmHDQA1muh5nKuUvmZvsv7ImODQKSEdsfQk6gVM7BfsV/oiBxN0mf8LBMfL0G7w/bgU4v0f3gmG+ar9awnSQbeWkGGrIny1ZDl7+0AyIW5qFEppCqs3F3FDCwaJikcADPfsqceF9LByrNyY/j1mE3fOb8c=', '', 'epx4gLPskvuie0i73ouhzp7oOQA9jR68L6im3OF8ENujmGOlnCAXWbvgzSSorfGj5Trv/IDzLqxyE496PHYK', '', 'Demo', b'0', b'0'),
  (4, '19831117', '', '', '', 'MARIA', 'f', 'MUSTERFRAU', '', '', '', '', NULL, 4, '', '', '', '', '2019-03-29 14:14:39.853', '2019-03-29 14:14:39.853', '', '', '', '', '', '', 'gnlrqSmaduyNgV3zQcgpmaByrTmW2nwrpeIInNMWAbSfEVQkw0yefxIhV3cmJkKUSc7aDEO+1ebxX+EGy+2U+yEEK3ye73NDaH/HpnHrpGydbx9+e8A7w2w2ggqpGK+HRFdFC22bigdIRagHmXOkdWxbmJSIVhvzI9fm+8s=', '', '6NBC/3jGgsQMaYycpzK545/6eaytzT6uD+i1lssm/MKtGDv0GhArueGp1lkZ1c0DSOn7aEabgZ3qd8i+0lQH', '', 'Demo', b'0', b'0');
/*!40000 ALTER TABLE `identity_preprocessed` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.person: ~6 rows (ungefähr)
/*!40000 ALTER TABLE `person` DISABLE KEYS */;
INSERT INTO `person` (`id`, `deactivated`, `first_mpi_identifier_domain_name`, `first_mpi_value`, `domain_name`, `create_timestamp`, `timestamp`) VALUES
  (1, b'0', 'MPI', '1001000000011', 'Demo', '2019-03-29 14:12:40.431', '2019-03-29 14:12:40.431'),
  (2, b'0', 'MPI', '1001000000028', 'Demo', '2019-03-29 14:13:08.924', '2019-03-29 14:13:08.924'),
  (3, b'0', 'MPI', '1001000000035', 'Demo', '2019-03-29 14:13:41.402', '2019-03-29 14:13:41.402'),
  (4, b'0', 'MPI', '1001000000042', 'Demo', '2019-03-29 14:14:39.853', '2019-03-29 14:14:39.853');
/*!40000 ALTER TABLE `person` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.person_history: ~6 rows (ungefähr)
/*!40000 ALTER TABLE `person_history` DISABLE KEYS */;
INSERT INTO `person_history` (`id`, `deactivated`, `history_timestamp`, `first_mpi_identifier_domain_name`, `first_mpi_value`, `person_id`, `domain_name`, `comment`) VALUES
  (1, b'0', '2019-03-29 14:12:40.431', 'MPI', '1001000000011', 1, 'Demo', 'NEW'),
  (2, b'0', '2019-03-29 14:13:08.924', 'MPI', '1001000000028', 2, 'Demo', 'NEW'),
  (3, b'0', '2019-03-29 14:13:41.402', 'MPI', '1001000000035', 3, 'Demo', 'NEW'),
  (4, b'0', '2019-03-29 14:14:39.853', 'MPI', '1001000000042', 4, 'Demo', 'NEW');
/*!40000 ALTER TABLE `person_history` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.sequence: ~9 rows (ungefähr)
/*!40000 ALTER TABLE `sequence` DISABLE KEYS */;
INSERT INTO `sequence` (`SEQ_NAME`, `SEQ_COUNT`) VALUES
  ('contact_history_index', 0),
  ('contact_index', 50),
  ('identity_history_index', 200),
  ('identity_index', 150),
  ('identitylink_history_index', 50),
  ('identitylink_index', 100),
  ('person_history_index', 150),
  ('person_index', 150),
    ('statistic_index', 60);
/*!40000 ALTER TABLE `sequence` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.stat_entry: ~1 rows (ungefähr)
/*!40000 ALTER TABLE `stat_entry` DISABLE KEYS */;
INSERT INTO `stat_entry` (`STAT_ENTRY_ID`, `ENTRYDATE`) VALUES
  (1, '2022-01-04 18:52:40.936'),
  (2, '2022-02-04 18:52:40.936'),
  (3, '2022-03-04 18:52:40.936');
/*!40000 ALTER TABLE `stat_entry` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle epix.stat_value: ~16 rows (ungefähr)
/*!40000 ALTER TABLE `stat_value` DISABLE KEYS */;
INSERT INTO `stat_value` (`stat_value_id`, `stat_value`, `stat_attr`) VALUES
  (1, 0, 'possible_matches.per_domain.Demo'),
  (1, 0, 'identity_match.per_domain.Demo'),
  (1, 0, 'possible_matches'),
  (1, 76, 'calculation_time'),
  (1, 1, 'domains'),
  (1, 0, 'possible_matches_merged'),
  (1, 0, 'persons'),
  (1, 0, 'identity_no_match.per_domain.Demo'),
  (1, 0, 'identity_perfect_match'),
  (1, 0, 'possible_matches_merged.per_domain.Demo'),
  (1, 0, 'identities'),
  (1, 0, 'identity_no_match'),
  (1, 0, 'persons.per_domain.Demo'),
  (1, 0, 'identity_perfect_match.per_domain.Demo'),
  (1, 0, 'identities.per_domain.Demo'),
  (1, 0, 'identity_match'),
  (1, 0, 'possible_matches_separated.per_domain.Demo'),
  (1, 0, 'possible_matches_separated'),
  (2, 0, 'possible_matches.per_domain.Demo'),
  (2, 0, 'identity_match.per_domain.Demo'),
  (2, 0, 'possible_matches'),
  (2, 76, 'calculation_time'),
  (2, 1, 'domains'),
  (2, 0, 'possible_matches_merged'),
  (2, 1, 'persons'),
  (2, 1, 'identity_no_match.per_domain.Demo'),
  (2, 0, 'identity_perfect_match'),
  (2, 0, 'possible_matches_merged.per_domain.Demo'),
  (2, 1, 'identities'),
  (2, 1, 'identity_no_match'),
  (2, 1, 'persons.per_domain.Demo'),
  (2, 0, 'identity_perfect_match.per_domain.Demo'),
  (2, 1, 'identities.per_domain.Demo'),
  (2, 0, 'identity_match'),
  (2, 0, 'possible_matches_separated.per_domain.Demo'),
  (2, 0, 'possible_matches_separated'),
  (3, 1, 'possible_matches.per_domain.Demo'),
  (3, 0, 'identity_match.per_domain.Demo'),
  (3, 1, 'possible_matches'),
  (3, 76, 'calculation_time'),
  (3, 1, 'domains'),
  (3, 0, 'possible_matches_merged'),
  (3, 4, 'persons'),
  (3, 4, 'identity_no_match.per_domain.Demo'),
  (3, 0, 'identity_perfect_match'),
  (3, 0, 'possible_matches_merged.per_domain.Demo'),
  (3, 4, 'identities'),
  (3, 4, 'identity_no_match'),
  (3, 4, 'persons.per_domain.Demo'),
  (3, 0, 'identity_perfect_match.per_domain.Demo'),
  (3, 4, 'identities.per_domain.Demo'),
  (3, 0, 'identity_match'),
  (3, 0, 'possible_matches_separated.per_domain.Demo'),
  (3, 0, 'possible_matches_separated'),
  (11, 1, 'possible_matches.per_domain.Demo'),
  (11, 1, 'identity_match.per_domain.Demo'),
  (11, 1, 'possible_matches'),
  (11, 17, 'calculation_time'),
  (11, 3, 'identity_possible_match.per_domain.Demo'),
  (11, 1, 'domains'),
  (11, 0, 'possible_matches_merged'),
  (11, 5, 'persons'),
  (11, 5, 'identity_no_match.per_domain.Demo'),
  (11, 0, 'identity_perfect_match'),
  (11, 0, 'possible_matches_merged.per_domain.Demo'),
  (11, 6, 'identities'),
  (11, 5, 'identity_no_match'),
  (11, 5, 'persons.per_domain.Demo'),
  (11, 0, 'identity_perfect_match.per_domain.Demo'),
  (11, 6, 'identities.per_domain.Demo'),
  (11, 2, 'possible_matches_separated.per_domain.Demo'),
  (11, 2, 'possible_matches_separated'),
  (11, 1, 'identity_match'),
  (11, 1, 'possible_matches.per_domain.Demo'),
  (11, 0, 'identity_match.per_domain.Demo'),
  (11, 1, 'possible_matches'),
  (11, 114, 'calculation_time'),
  (11, 3, 'identity_possible_match.per_domain.Demo'),
  (11, 1, 'domains'),
  (11, 0, 'possible_matches_merged'),
  (11, 2, 'possible_matches_separated'),
  (11, 4, 'persons'),
  (11, 4, 'identity_no_match.per_domain.Demo'),
  (11, 0, 'identity_perfect_match'),
  (11, 0, 'possible_matches_merged.per_domain.Demo'),
  (11, 4, 'identities'),
  (11, 4, 'identity_no_match'),
  (11, 4, 'persons.per_domain.Demo'),
  (11, 0, 'identity_perfect_match.per_domain.Demo'),
  (11, 4, 'identities.per_domain.Demo'),
  (11, 2, 'possible_matches_separated.per_domain.Demo'),
  (11, 0, 'identity_match');
/*!40000 ALTER TABLE `stat_value` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
