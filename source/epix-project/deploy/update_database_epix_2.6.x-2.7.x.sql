-- attention! dependent on your sql-client it may be neccessary to change "modify" with "change column"

USE epix;

alter table project add column label varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL after name;
alter table project add column generator varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL after mpi_domain;
alter table project add column safe_source varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL after generator;
alter table project add column config text NOT NULL after safe_source;

alter table identifier_domain drop column domain_type;

alter table project drop foreign key `FK_project-id_type`;
alter table project drop column idType_id;
alter table project modify name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;

alter table project drop column id;
alter table project add primary key(name);

update project set generator = 'org.emau.icmvc.ttp.epix.gen.impl.EAN13Generator' where generator = '';

drop table id_type;

alter table source change value name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;

update person_history ph set source_id = (select source_id from person p where p.id = ph.person_id) where source_id is NULL;
alter table person drop foreign key `FK_person-source`;
alter table person drop key `FK_person-source`;
alter table person change source_id source_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
update person set source_name = (select name from source where id = source_name);
alter table person add key `FK_identity-source` (source_name);
alter table person add constraint `FK_identity-source` foreign key (source_name) references source (name);

alter table person_history drop foreign key `FK_person_history-source`;
alter table person_history drop key `FK_person_history-source`;
alter table person_history change source_id source_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
update person_history set source_name = (select name from source where id = source_name);
alter table person_history add key `FK_identity_history-source` (source_name);
alter table person_history add constraint `FK_identity_history-source` foreign key (source_name) references source (name);

alter table person_group add column domain_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
alter table person_group add key `FK_person-domain` (domain_name);
-- maybe need to change the project name
update person_group set domain_name = 'MOSAIC';
alter table person_group add constraint `FK_person-domain` foreign key (domain_name) references project (name);

alter table person_group_history add column domain_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
alter table person_group add key `FK_person_history-domain` (domain_name);
-- maybe need to change the project name
update person_group_history set domain_name = 'MOSAIC';
alter table person_group_history add constraint `FK_person_history-domain` foreign key (domain_name) references project (name);

alter table person_preprocessed drop column department;
alter table person_preprocessed drop column institute;
alter table person_preprocessed add column domain_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
-- maybe need to change the project name
update person_preprocessed set domain_name = 'MOSAIC';
alter table person_preprocessed add constraint `FK_identity_preprocessed-domain` foreign key (domain_name) references project (name);

alter table source drop column id;
alter table source add primary key(name);
alter table source add column label varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;

-- safe_source namen eventuell anpassen!
update project set safe_source = 'dummy_safe_source';
alter table project add constraint `FK_project-source` foreign key (safe_source) references source (name);

alter table identifier drop column sending_application;
alter table identifier drop column sending_facility;

alter table person_identifier drop foreign key `FK_person_identifier-person`;
alter table person_identifier drop key `FK_person_identifier-person`;
alter table person_identifier change persons_id person_id bigint(20) NOT NULL;
alter table person_identifier add key `FK_person_identifier-person` (person_id);
alter table person_identifier add constraint `FK_person_identifier-person` foreign key (person_id) references person (id);

alter table identifier modify local_identifier varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
alter table identifier modify domain_id bigint(20) NOT NULL;

alter table person_group drop foreign key `FK_person_group-identifier`;
alter table person_group drop key `FK_person_group-identifier`;
alter table person_group modify firstMpi_domain_id bigint(20) NOT NULL;
alter table person_group modify firstMpi_local_identifier varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
alter table person_group add key `FK_person-identifier` (firstMpi_domain_id, firstMpi_local_identifier);
alter table person_group add constraint `FK_person-identifier` foreign key (firstMpi_domain_id, firstMpi_local_identifier) references identifier (domain_id, local_identifier);

alter table person add forced_reference bit(1) DEFAULT 0;
alter table person_history add forced_reference bit(1) DEFAULT 0;
update person p set forced_reference = 1 where p.id in (select referencePerson_id from person_group);
update person_history p set forced_reference = 1 where p.id in (select referencePerson_id from person_group_history);
update person p set forced_reference = 1 where p.source_name = 
(select safe_source from project pro join person_group pg on pg.domain_name = pro.name where pg.id = p.personGroup_id);
update person_history p set forced_reference = 1 where p.source_name = 
(select safe_source from project pro join person_group_history pg on pg.domain_name = pro.name where pg.id = p.person_group_id);

alter table person_group drop foreign key `FK_person_group-person`;
alter table person_group drop key `FK_person_group-person`;
alter table person_group drop referencePerson_id;

alter table person_group_history drop foreign key `FK_person_group_history-person`;
alter table person_group_history drop key `FK_person_group_history-person`;
alter table person_group_history drop referencePerson_id;

rename table project to domain;
rename table personlink to identitylink;
rename table personlink_history to identitylink_history;
rename table person to identity;
rename table person_history to identity_history;
rename table person_preprocessed to identity_preprocessed;
rename table person_group to person;
rename table person_group_history to person_history;
rename table person_identifier to identity_identifier;
rename table person_history_identifier to identity_history_identifier;

alter table identity_preprocessed drop foreign key `FK_person_preprocessed-person`;
alter table identity_preprocessed drop foreign key `FK_person_preprocessed-person_group`;
alter table identity drop foreign key `FK_person-person_group`;
alter table identity drop key `FK_person-person_group`;
alter table identity change personGroup_id person_id bigint(20) NOT NULL;
alter table identity add key `FK_identity-person` (person_id);
alter table identity add constraint `FK_identity-person` foreign key (person_id) references person (id);
alter table identity_preprocessed change person_id identity_id bigint(20) NOT NULL;
alter table identity_preprocessed change person_group_id person_id bigint(20) NOT NULL;
alter table identity_preprocessed add key `FK_identity_preprocessed-identity_person` (person_id);
alter table identity_preprocessed add key `FK_identity_preprocessed-domain` (domain_name);
alter table identity_preprocessed add constraint `FK_identity_preprocessed-identity` foreign key (identity_id) references identity (id);
alter table identity_preprocessed add constraint `FK_identity_preprocessed-identity_person` foreign key (person_id) references identity (person_id);

alter table identity_history drop foreign key `FK_person_history-person`;
alter table identity_history drop key `FK_person_history-person`;
alter table identity_history change person_id identity_id bigint(20) NOT NULL;
alter table identity_history change person_group_id person_id bigint(20) NOT NULL;
alter table identity_history add key `FK_identity_history-identity` (identity_id);
alter table identity_history add constraint `FK_identity_history-identity` foreign key (identity_id) references identity (id);

alter table contact drop foreign key `FK_contact-person`;
alter table contact drop key `FK_contact-person`;
alter table contact change person_id identity_id bigint(20) NOT NULL;
alter table contact add key `FK_contact-identity` (identity_id);
alter table contact add constraint `FK_contact-identity` foreign key (identity_id) references identity (id);

alter table contact_history drop foreign key `FK_contact_history-person_history`;
alter table contact_history drop key `FK_contact_history-person_history`;
alter table contact_history change person_history_id identity_history_id bigint(20) NOT NULL;
alter table contact_history add key `FK_contact_history-identity_history` (identity_history_id);
alter table contact_history add constraint `FK_contact_history-identity_history` foreign key (identity_history_id) references identity_history (id);

alter table person_history drop foreign key `FK_person_group_history-identifier`;
alter table person_history drop key `FK_person_group_history-identifier`;
alter table person_history add key `FK_person_history-identifier` (firstMpi_domain_id, firstMpi_local_identifier);
alter table person_history add constraint `FK_person_history-identifier` foreign key (firstMpi_domain_id, firstMpi_local_identifier) references identifier (domain_id, local_identifier);

alter table person_history drop foreign key `FK_person_group_history-person_group`;
alter table person_history drop key `FK_person_group_history-person_group`;
alter table person_history change personGroup_id person_id bigint(20) NOT NULL;
alter table person_history add key `FK_person_history-person` (person_id);
alter table person_history add constraint `FK_person_history-person` foreign key (person_id) references person (id);

alter table identity_identifier drop foreign key `FK_person_identifier-identifier`;
alter table identity_identifier drop foreign key `FK_person_identifier-person`;
alter table identity_identifier drop key `FK_person_identifier-identifier`;
alter table identity_identifier drop key `FK_person_identifier-person`;
alter table identity_identifier change person_id identity_id bigint(20) NOT NULL;
alter table identity_identifier change localIdentifiers_domain_id identifiers_domain_id bigint(20) NOT NULL;
alter table identity_identifier change localIdentifiers_local_identifier identifiers_local_identifier varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
alter table identity_identifier add key `FK_identity_identifier-identity` (identity_id);
alter table identity_identifier add key `FK_identity_identifier-identifier` (identifiers_domain_id, identifiers_local_identifier);
alter table identity_identifier add constraint `FK_identity_identifier-identity` foreign key (identity_id) references identity (id);
alter table identity_identifier add constraint `FK_identity_identifier-identifier` foreign key (identifiers_domain_id, identifiers_local_identifier) references identifier (domain_id, local_identifier);

alter table identity_history_identifier drop foreign key `FK_person_history_identifier-identifier`;
alter table identity_history_identifier drop foreign key `FK_person_history_identifier-person_history`;
alter table identity_history_identifier drop key `FK_person_history_identifier-identifier`;
alter table identity_history_identifier drop key `FK_person_history_identifier-person_history`;
alter table identity_history_identifier change person_history_id identity_history_id bigint(20) NOT NULL;
alter table identity_history_identifier change identifier_domain_id identifiers_domain_id bigint(20) NOT NULL;
alter table identity_history_identifier change identifier_local_identifier identifiers_local_identifier varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
alter table identity_history_identifier add key `FK_identity_history_identifier-identity_history` (identity_history_id);
alter table identity_history_identifier add key `FK_identity_history_identifier-identifier` (identifiers_domain_id, identifiers_local_identifier);
alter table identity_history_identifier add constraint `FK_identity_history_identifier-identity_history` foreign key (identity_history_id) references identity_history (id);
alter table identity_history_identifier add constraint `FK_identity_history_identifier-identifier` foreign key (identifiers_domain_id, identifiers_local_identifier) references identifier (domain_id, local_identifier);

alter table identitylink drop foreign key `FK_personlink-dest_person`;
alter table identitylink drop foreign key `FK_personlink-src_person`;
alter table identitylink drop key `FK_personlink-dest_person`;
alter table identitylink drop key `FK_personlink-src_person`;
alter table identitylink change destPerson_id dest_identity bigint(20) NOT NULL;
alter table identitylink change srcPerson_id src_identity bigint(20) NOT NULL;
alter table identitylink add key `FK_identitylink-dest_identity` (dest_identity);
alter table identitylink add key `FK_identitylink-src_identity` (src_identity);
alter table identitylink add constraint `FK_identitylink-dest_identity` foreign key (dest_identity) references identity (id);
alter table identitylink add constraint `FK_identitylink-src_identity` foreign key (src_identity) references identity (id);

alter table identitylink_history drop foreign key `FK_personlink_history-dest_person`;
alter table identitylink_history drop foreign key `FK_personlink_history-from_person_group`;
alter table identitylink_history drop foreign key `FK_personlink_history-src_person`;
alter table identitylink_history drop foreign key `FK_personlink_history-to_person_group`;
alter table identitylink_history drop foreign key `FK_personlink_history-upd_person`;
alter table identitylink_history drop key `FK_personlink_history-dest_person`;
alter table identitylink_history drop key `FK_personlink_history-from_person_group`;
alter table identitylink_history drop key `FK_personlink_history-src_person`;
alter table identitylink_history drop key `FK_personlink_history-to_person_group`;
alter table identitylink_history drop key `FK_personlink_history-upd_person`;
alter table identitylink_history drop personLinkId;
alter table identitylink_history change destPerson_id dest_identity bigint(20) NOT NULL;
alter table identitylink_history change from_group src_person bigint(20) NOT NULL;
alter table identitylink_history change srcPerson_id src_identity bigint(20) NOT NULL;
alter table identitylink_history change to_group dest_person bigint(20) NOT NULL;
alter table identitylink_history change updated_person updated_identity bigint(20) DEFAULT NULL;
alter table identitylink_history add key `FK_identitylink_history-dest_identity` (dest_identity);
alter table identitylink_history add key `FK_identitylink_history-src_person` (src_person);
alter table identitylink_history add key `FK_identitylink_history-src_identity` (src_identity);
alter table identitylink_history add key `FK_identitylink_history-dest_person` (dest_person);
alter table identitylink_history add key `FK_identitylink_history-updated_identity` (updated_identity);
alter table identitylink_history add constraint `FK_identitylink_history-dest_identity` foreign key (dest_identity) references identity (id);
alter table identitylink_history add constraint `FK_identitylink_history-src_person` foreign key (src_person) references person (id);
alter table identitylink_history add constraint `FK_identitylink_history-src_identity` foreign key (src_identity) references identity (id);
alter table identitylink_history add constraint `FK_identitylink_history-dest_person` foreign key (dest_person) references person (id);
alter table identitylink_history add constraint `FK_identitylink_history-updated_identity` foreign key (updated_identity) references identity (id);

alter table identity_preprocessed add forced_reference bit(1) DEFAULT 0;
alter table identity add deactivated bit(1) DEFAULT 0;
alter table identity_history add deactivated bit(1) DEFAULT 0;
alter table identity_preprocessed add deactivated bit(1) DEFAULT 0;
alter table person change locked deactivated bit(1) DEFAULT 0;
alter table person_history change locked deactivated bit(1) DEFAULT 0;
update identity i set deactivated = (select deactivated from person p where p.id = i.person_id);
update identity_preprocessed ip set deactivated = (select deactivated from identity i where i.id = ip.identity_id);

alter table identifier drop column identifier_type;

alter table identity change birthPlace birth_place varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity change civilStatus civil_status varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity change motherTongue mother_tongue varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity change mothersMaidenName mothers_maiden_name varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_history change birthPlace birth_place varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_history change civilStatus civil_status varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_history change motherTongue mother_tongue varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_history change mothersMaidenName mothers_maiden_name varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_history change originalEvent original_event varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_preprocessed change birthPlace birth_place varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_preprocessed change civilStatus civil_status varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_preprocessed change motherTongue mother_tongue varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_preprocessed change mothersMaidenName mothers_maiden_name varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;

alter table identifier drop foreign key `FK_identifier-identifier_domain`;
alter table identifier drop key `FK_identifier-identifier_domain`;
alter table domain drop foreign key `FK_project-identifier_domain`;
alter table domain drop key `FK_project-identifier_domain`;
alter table identity_history_identifier drop foreign key `FK_identity_history_identifier-identifier`;
alter table identity_history_identifier drop key `FK_identity_history_identifier-identifier`;
alter table identity_identifier drop foreign key `FK_identity_identifier-identifier`;
alter table identity_identifier drop key `FK_identity_identifier-identifier`;
alter table person drop foreign key `FK_person-identifier`;
alter table person drop key `FK_person-identifier`;
alter table person_history drop foreign key `FK_person_history-identifier`;
alter table person_history drop key `FK_person_history-identifier`;

alter table domain change safe_source safe_source_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
alter table domain add key `FK_domain-source` (safe_source_name);
alter table domain add constraint `FK_domain-source` foreign key (safe_source_name) references source (name);
update identity_history ih set person_id = (select person_id from identity i where i.id = ih.identity_id) where person_id is NULL;
alter table identity_history add key `FK_identity_history-person` (person_id);
alter table identity_history add constraint `FK_identity_history-person` foreign key (person_id) references person (id);
alter table identifier_domain add unique key UK_oid (oid);
alter table identifier_domain add column label varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL after name;
alter table identifier drop primary key;
alter table identifier change local_identifier value varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
alter table identifier change domain_id identifier_domain_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
update identifier x set identifier_domain_name = (select name from identifier_domain where id = x.identifier_domain_name);
alter table identifier add primary key (identifier_domain_name, value);
alter table domain change mpi_domain mpi_domain_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
update domain x set mpi_domain_name = (select name from identifier_domain where id = x.mpi_domain_name);

alter table identity_history_identifier change identifiers_domain_id identifiers_identifier_domain_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
alter table identity_history_identifier change identifiers_local_identifier identifiers_value varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
update identity_history_identifier x set identifiers_identifier_domain_name = (select name from identifier_domain where id = x.identifiers_identifier_domain_name);
alter table identity_identifier change identifiers_domain_id identifiers_identifier_domain_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
alter table identity_identifier change identifiers_local_identifier identifiers_value varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
update identity_identifier x set identifiers_identifier_domain_name = (select name from identifier_domain where id = x.identifiers_identifier_domain_name);
alter table person change firstMpi_domain_id first_mpi_identifier_domain_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
alter table person change firstMpi_local_identifier first_mpi_value varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
update person x set first_mpi_identifier_domain_name = (select name from identifier_domain where id = x.first_mpi_identifier_domain_name);
alter table person_history change firstMpi_domain_id first_mpi_identifier_domain_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
alter table person_history change firstMpi_local_identifier first_mpi_value varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL;
update person_history x set first_mpi_identifier_domain_name = (select name from identifier_domain where id = x.first_mpi_identifier_domain_name);

alter table identifier_domain drop column id;
alter table identifier_domain add primary key (name);

alter table person_history add key `FK_person_history-identifier` (first_mpi_identifier_domain_name, first_mpi_value);
alter table person_history add constraint `FK_person_history-identifier` foreign key (first_mpi_identifier_domain_name, first_mpi_value) references identifier (identifier_domain_name, value);
alter table person add key `FK_person-identifier` (first_mpi_identifier_domain_name, first_mpi_value);
alter table person add constraint `FK_person-identifier` foreign key (first_mpi_identifier_domain_name, first_mpi_value) references identifier (identifier_domain_name, value);
alter table identity_history_identifier add key `FK_identity_history_identifier-identifier` (identifiers_identifier_domain_name, identifiers_value);
alter table identity_history_identifier add constraint `FK_identity_history_identifier-identifier` foreign key (identifiers_identifier_domain_name, identifiers_value) references identifier (identifier_domain_name, value);
alter table identity_identifier add key `FK_identity_identifier-identifier` (identifiers_identifier_domain_name, identifiers_value);
alter table identity_identifier add constraint `FK_identity_identifier-identifier` foreign key (identifiers_identifier_domain_name, identifiers_value) references identifier (identifier_domain_name, value);
alter table domain add key `FK_domain-identifier_domain` (mpi_domain_name);
alter table domain add constraint `FK_domain-identifier_domain` foreign key (mpi_domain_name) references identifier_domain (name);
alter table identifier add key `FK_identifier-identifier_domain` (identifier_domain_name);
alter table identifier add constraint `FK_identifier-identifier_domain` foreign key (identifier_domain_name) references identifier_domain (name);

update identity_preprocessed set value1 = '' where value1 is null;
update identity_preprocessed set value2 = '' where value2 is null;
update identity_preprocessed set value3 = '' where value3 is null;
update identity_preprocessed set value4 = '' where value4 is null;
update identity_preprocessed set value5 = '' where value5 is null;
update identity_preprocessed set value6 = '' where value6 is null;
update identity_preprocessed set value7 = '' where value7 is null;
update identity_preprocessed set value8 = '' where value8 is null;
update identity_preprocessed set value9 = '' where value9 is null;
update identity_preprocessed set value10 = '' where value10 is null;
update identity_preprocessed ip set timestamp = (select timestamp from identity i where i.id = ip.identity_id) where timestamp is null;
alter table identity_preprocessed modify gender character(1) DEFAULT ' ';
alter table identity modify gender character(1) DEFAULT NULL;
alter table identity_history modify gender character(1) DEFAULT NULL;

alter table person_history modify timestamp datetime NOT NULL DEFAULT NOW();
alter table person_history add comment varchar(255) DEFAULT NULL;
alter table identity modify timestamp datetime NOT NULL DEFAULT NOW();
alter table identity change origin_date external_timestamp datetime DEFAULT NULL;
alter table identity_preprocessed change origin_date external_timestamp datetime DEFAULT NULL;
alter table identity_history change timestamp history_timestamp datetime NOT NULL DEFAULT NOW();
alter table identity_history change origin_date external_timestamp datetime DEFAULT NULL;
alter table identity_history change original_event event char(13) NOT NULL;
alter table identity_history add comment varchar(255) DEFAULT NULL;

alter table identitylink_history modify timestamp datetime NOT NULL DEFAULT NOW();
update identitylink_history set event = 'MERGE' where event = 'BIND';
alter table identitylink_history modify event char(5) NOT NULL;
alter table identitylink modify threshold double NOT NULL;
alter table identitylink_history modify threshold double NOT NULL;

alter table identity_preprocessed modify date_of_birth date NOT NULL;
alter table identity_preprocessed modify birth_place varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify civil_status varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify degree varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify first_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify last_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify middle_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify mother_tongue varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify mothers_maiden_name varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify nationality varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify external_timestamp datetime DEFAULT NULL;
alter table identity_preprocessed modify prefix varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify race varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify religion varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify suffix varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify timestamp datetime NOT NULL DEFAULT NOW();
alter table identity_preprocessed modify value1 varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value10 varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value2 varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value3 varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value4 varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value5 varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value6 varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value7 varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value8 varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value9 varchar(255) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';

alter table identity_preprocessed change date_of_birth date_of_birth_old date NOT NULL;
alter table identity_preprocessed add date_of_birth char(8) after date_of_birth_old;
update identity_preprocessed ip set date_of_birth = DATE_FORMAT((select t.date_of_birth_old from
(select date_of_birth_old, identity_id from identity_preprocessed) t where t.identity_id = ip.identity_id), '%Y%m%d');
alter table identity_preprocessed drop column date_of_birth_old;
alter table identity_preprocessed modify date_of_birth char(8) NOT NULL;

CREATE TABLE sequence(SEQ_NAME varchar(50) PRIMARY KEY NOT NULL, SEQ_COUNT decimal(38,0));
insert into sequence(seq_name, seq_count) values
("contact_index", (select ifnull((select max(id) from contact), 0))),
("contact_history_index", (select ifnull((select max(id) from contact_history), 0))),
("identity_index", (select ifnull((select max(id) from identity), 0))),
("identity_history_index", (select ifnull((select max(id) from identity_history), 0))),
("identitylink_index", (select ifnull((select max(id) from identitylink), 0))),
("identitylink_history_index", (select ifnull((select max(id) from identitylink_history), 0))),
("person_index", (select ifnull((select max(id) from person), 0))),
("person_history_index", (select ifnull((select max(id) from person_history), 0))),
("statistic_index", (select ifnull((select max(stat_entry_id) from stat_entry), 0)));

alter table identity_history add identity_version int(11) DEFAULT 0 after id;
alter table contact add version int(11) DEFAULT 0 after id;
alter table contact add municipality_key varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table contact add deactivated bit(1) DEFAULT 0;
alter table contact add external_timestamp datetime DEFAULT NULL;
alter table contact add timestamp datetime NOT NULL DEFAULT NOW();
-- bisher nicht gespeichert, daher 0 eintragen
alter table contact_history add contact_version int(11) DEFAULT 0 after id;
alter table contact_history modify contact_version int(11) DEFAULT NULL;
alter table contact_history add municipality_key varchar(255) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table contact_history add deactivated bit(1) DEFAULT 0;
alter table contact_history add external_timestamp datetime DEFAULT NULL;
alter table contact_history add history_timestamp datetime NOT NULL DEFAULT NOW();
alter table contact_history drop foreign key `FK_contact_history-identity_history`;
alter table contact_history drop key `FK_contact_history-identity_history`;
alter table contact_history change identity_history_id identity_id bigint(20) NOT NULL;
-- umbiegen der contact_history-eintraege von identity_history auf identity
update contact_history ch set identity_id = (select identity_id from identity_history where id = ch.identity_id);
alter table contact_history add key `FK_contact_history-identity` (identity_id);
alter table contact_history add constraint `FK_contact_history-identity` foreign key (identity_id) references identity (id);
alter table domain drop column generator;

alter table person add create_timestamp datetime NOT NULL DEFAULT NOW();
alter table person add timestamp datetime NOT NULL DEFAULT NOW();
alter table identity add create_timestamp datetime NOT NULL DEFAULT NOW();
alter table person_history add history_timestamp datetime NOT NULL DEFAULT NOW();
alter table contact add create_timestamp datetime NOT NULL DEFAULT NOW();

alter table contact_history add contact_id bigint(20) DEFAULT 0 after id;
-- einem zufaelligem contact (aber passend zur identity) zuordnen, da die info bisher nicht erfasst wurde
update contact_history ch set contact_id = (select id from contact where identity_id = ch.identity_id limit 1);
alter table contact_history modify contact_id bigint(20) NOT NULL;
alter table contact_history add key `FK_contact_history-contact` (contact_id);
alter table contact_history add constraint `FK_contact_history-contact` foreign key (contact_id) references contact (id);

update identity set value1 = 'XXX_NULL_DUMMY_XXX' where value1 is null;
update identity set value2 = 'XXX_NULL_DUMMY_XXX' where value2 is null;
update identity set value3 = 'XXX_NULL_DUMMY_XXX' where value3 is null;
update identity set value4 = 'XXX_NULL_DUMMY_XXX' where value4 is null;
update identity set value5 = 'XXX_NULL_DUMMY_XXX' where value5 is null;
alter table identity modify value1 varchar(50) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity modify value2 varchar(50) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity modify value3 varchar(50) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity modify value4 varchar(50) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity modify value5 varchar(50) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
update identity set value1 = null where value1 = 'XXX_NULL_DUMMY_XXX';
update identity set value2 = null where value2 = 'XXX_NULL_DUMMY_XXX';
update identity set value3 = null where value3 = 'XXX_NULL_DUMMY_XXX';
update identity set value4 = null where value4 = 'XXX_NULL_DUMMY_XXX';
update identity set value5 = null where value5 = 'XXX_NULL_DUMMY_XXX';
alter table identity modify value8 varchar(1000) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity modify value9 varchar(1000) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity modify value10 varchar(15000) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;

update identity_history set value1 = 'XXX_NULL_DUMMY_XXX' where value1 is null;
update identity_history set value2 = 'XXX_NULL_DUMMY_XXX' where value2 is null;
update identity_history set value3 = 'XXX_NULL_DUMMY_XXX' where value3 is null;
update identity_history set value4 = 'XXX_NULL_DUMMY_XXX' where value4 is null;
update identity_history set value5 = 'XXX_NULL_DUMMY_XXX' where value5 is null;
alter table identity_history modify value1 varchar(50) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_history modify value2 varchar(50) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_history modify value3 varchar(50) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_history modify value4 varchar(50) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_history modify value5 varchar(50) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
update identity_history set value1 = null where value1 = 'XXX_NULL_DUMMY_XXX';
update identity_history set value2 = null where value2 = 'XXX_NULL_DUMMY_XXX';
update identity_history set value3 = null where value3 = 'XXX_NULL_DUMMY_XXX';
update identity_history set value4 = null where value4 = 'XXX_NULL_DUMMY_XXX';
update identity_history set value5 = null where value5 = 'XXX_NULL_DUMMY_XXX';
alter table identity_history modify value8 varchar(1000) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_history modify value9 varchar(1000) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table identity_history modify value10 varchar(15000) CHARSET utf8 COLLATE utf8_bin DEFAULT NULL;

alter table identity_preprocessed modify value1 varchar(50) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value2 varchar(50) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value3 varchar(50) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value4 varchar(50) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value5 varchar(50) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value8 varchar(1000) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value9 varchar(1000) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';
alter table identity_preprocessed modify value10 varchar(15000) CHARSET utf8 COLLATE utf8_bin NOT NULL DEFAULT '';

update identity_history set event = 'NEW' where event = '0';
update identity_history set event = 'UPDATE' where event = '1';
update identity_history set event = 'MERGE' where event = '2';
update identity_history set event = 'MATCH' where event = '3';
update identity_history set event = 'PERFECT_MATCH' where event = '4';

alter table identity_history add matchingScore double DEFAULT 0;
alter table identifier_domain change entry_date create_timestamp datetime NOT NULL DEFAULT NOW();
alter table identifier_domain add description varchar(255) DEFAULT NULL;
alter table identifier change entry_date create_timestamp datetime NOT NULL DEFAULT NOW();
alter table source add create_timestamp datetime NOT NULL DEFAULT NOW();
alter table domain change entry_date create_timestamp datetime NOT NULL DEFAULT NOW();
alter table identitylink add create_timestamp datetime NOT NULL DEFAULT NOW();
alter table identitylink_history change timestamp history_timestamp datetime NOT NULL DEFAULT NOW();
alter table identitylink_history change explanation comment varchar(255) DEFAULT NULL;
alter table identitylink_history modify event char(14) NOT NULL;

update identity set gender = lower(gender);
update identity_preprocessed set gender = lower(gender);
update identity_history set gender = lower(gender);

drop procedure if exists updateStats;

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
