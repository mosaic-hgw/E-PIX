USE epix;
-- Neue Tabelle source erzeugen
CREATE TABLE `source` (
  `Id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(255) NULL,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE INDEX `value_UNIQUE` (`value` ASC));

-- Datensätze zu der neuen Tabelle hinzufügen
INSERT INTO `source` (`Id`, `description`, `value`) VALUES ('1', '', 'GANI_MED'); -- 'GANI_MED' ist ein beispiel und sollte angepasst werden
-- Hier weitere Sources aus dem Projekt ergänzen
-- INSERT INTO `source` (`Id`, `value`) VALUES ('2', 'ANDERE_QUELLE');

ALTER TABLE `person` 
DROP FOREIGN KEY `FK_xyz`; -- Austauschen gegen vorhandenen Key auf mpiid_id
ALTER TABLE `person` 
DROP COLUMN `mpiid_id`,
ADD COLUMN `source_id` BIGINT(20) NULL DEFAULT NULL AFTER `personGroup_id`,
ADD INDEX `fk_person-source` (`source_id` ASC),
DROP INDEX `FK_xyz`; -- Austauschen gegen vorhandenen Index auf mpiid_id
ALTER TABLE `person` 
ADD CONSTRAINT `fk_person-source`
  FOREIGN KEY (`source_id`)
  REFERENCES `source` (`Id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
  
-- Source in Person_History hinzufügen
ALTER TABLE `person_history` 
ADD COLUMN `source_id` BIGINT(20) NULL DEFAULT NULL AFTER `person_id`,
ADD INDEX `FK_person_history-source` (`source_id` ASC);
ALTER TABLE `person_history` 
ADD CONSTRAINT `FK_person_history-source`
  FOREIGN KEY (`source_id`)
  REFERENCES `source` (`Id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
  
UPDATE person SET source_id=1; -- Ist projektspezfisch. Source_Id darf nicht NULL sein! siehe weiter oben eingefuegte sources
  
-- Tabelle Project die MPI-Domain hinzufügen
ALTER TABLE `project`
ADD COLUMN `mpi_domain` BIGINT(20) NULL DEFAULT NULL,
ADD INDEX `FK_project-identifier_domain` (`mpi_domain` ASC);
ALTER TABLE `project`
ADD CONSTRAINT `FK_project-identifier_domain`
  FOREIGN KEY (`mpi_domain`)
  REFERENCES `identifier_domain` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
  
-- Neue Spalte pflegen
UPDATE `project` SET `mpi_domain`='XXXXX'; -- id fuer die mpi-domain aus tabelle identifier_domain einfuegen

DROP TABLE `mpi_id`;

DROP TABLE `epix`.`contact_preprocessed`;

ALTER TABLE `epix`.`person_preprocessed` 
DROP FOREIGN KEY `FK_xyz`; -- Austauschen gegen vorhandenen Key (person_id)
ALTER TABLE `epix`.`person_preprocessed` 
DROP COLUMN `person_id`,
DROP INDEX `UK_xyz`; -- Austauschen gegen vorhandenen Unique Index auf person_id

ALTER TABLE `epix`.`person_preprocessed` 
DROP COLUMN `id`,

ALTER TABLE `epix`.`person_preprocessed` 
add COLUMN `person_id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY;

ALTER TABLE `epix`.`person_preprocessed` 
ADD CONSTRAINT `FK_person_preprocessed-person`
  FOREIGN KEY (`person_id`)
  REFERENCES `epix`.`person` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `epix`.`person_preprocessed` 
ADD COLUMN `person_group_id` BIGINT(20) NOT NULL AFTER `value10`;

Update person_preprocessed pp set person_group_id = (select personGroup_id from person p where  p.id=pp.person_id)

ALTER TABLE `epix`.`person_preprocessed` 
ADD CONSTRAINT `FK_person_preprocessed-person_group`
  FOREIGN KEY (`person_group_id`)
  REFERENCES `epix`.`person` (`personGroup_id`)
  ON DELETE NO ACTION
  ON UPDATE CASCADE;
