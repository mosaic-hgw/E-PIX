-- BEGIN Fix uppercase table name SEQUENCE in linux systems
-- Make sure both tables exist
CREATE TABLE IF NOT EXISTS `sequence` (
    SEQ_NAME varchar(50) PRIMARY KEY NOT NULL,
    SEQ_COUNT decimal(38,0)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE=UTF8_BIN;
CREATE TABLE IF NOT EXISTS `SEQUENCE` (
    SEQ_NAME varchar(50) PRIMARY KEY NOT NULL,
    SEQ_COUNT decimal(38,0)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE=utf8_bin;

-- Create backup table
CREATE TABLE `sequence_backup` (
    SEQ_NAME varchar(50) NOT NULL,
    SEQ_COUNT decimal(38,0)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE=utf8_bin;

-- Combine both sequence tables
INSERT INTO `sequence_backup` SELECT * FROM `sequence`;
INSERT INTO `sequence_backup` SELECT * FROM `SEQUENCE`;

-- Drop old tables
DROP TABLE IF EXISTS `SEQUENCE`;
DROP TABLE IF EXISTS `sequence`;

-- Create new sequence table
CREATE TABLE `sequence` (
    SEQ_NAME varchar(50) PRIMARY KEY NOT NULL,
    SEQ_COUNT decimal(38,0)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE=UTF8_BIN;

-- Insert combined values
INSERT INTO `sequence`
SELECT SEQ_NAME, MAX(SEQ_COUNT) FROM `sequence_backup` GROUP BY SEQ_NAME;

-- Drop backup
DROP TABLE `sequence_backup`;
-- END Fix uppercase table name SEQUENCE in linux systems

-- Update statistic
delete from `sequence` where `SEQ_NAME` = 'statistic_index';
insert into `sequence` values('statistic_index', (select max(`STAT_ENTRY_ID`) from `stat_entry`));
update `sequence`set `SEQ_COUNT` = 0 where `SEQ_NAME` = 'statistic_index' and `SEQ_COUNT` IS NULL;
alter table `stat_value` modify `stat_value` bigint(20);
alter table `stat_value` modify `stat_attr` varchar(255);
alter table `stat_entry` modify `ENTRYDATE` timestamp(3);

-- Remove deprected statistic procedure
DROP procedure IF EXISTS `updateStats`;

alter table identity_preprocessed drop constraint `FK_identity_preprocessed-identity_person`;
alter table identity_preprocessed add constraint `FK_identity_preprocessed-person` FOREIGN KEY (person_id) REFERENCES person (id);

