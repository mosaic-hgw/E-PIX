-- attention! dependent on your sql-client it may be neccessary to change "modify" with "change column"

USE epix;

alter table identity modify gender character(1) default ' ';
update identity set gender = ' ' where gender is null;
update identity_preprocessed set gender = ' ' where gender is null;

alter table contact modify external_timestamp timestamp(3) null;
alter table contact modify timestamp timestamp(3) not null default current_timestamp(3);
alter table contact modify create_timestamp timestamp(3) not null default current_timestamp(3);
alter table contact_history modify external_timestamp timestamp(3) null;
alter table contact_history modify history_timestamp timestamp(3) not null default current_timestamp(3);
alter table domain modify create_timestamp timestamp(3) not null default current_timestamp(3);
alter table identifier modify create_timestamp timestamp(3) not null default current_timestamp(3);
alter table identifier_domain modify create_timestamp timestamp(3) not null default current_timestamp(3);
alter table identity modify external_timestamp timestamp(3) null;
alter table identity modify timestamp timestamp(3) not null default current_timestamp(3);
alter table identity modify create_timestamp timestamp(3) not null default current_timestamp(3);
alter table identity_history modify external_timestamp timestamp(3) null;
alter table identity_history modify history_timestamp timestamp(3) not null default current_timestamp(3);
alter table identity_preprocessed modify external_timestamp timestamp(3) null;
alter table identity_preprocessed modify timestamp timestamp(3) not null default current_timestamp(3);
alter table identity_preprocessed add create_timestamp timestamp(3) not null default current_timestamp(3);
update identity_preprocessed ip set create_timestamp = (select create_timestamp from identity i where i.id = ip.identity_id);
alter table identitylink modify create_timestamp timestamp(3) not null default current_timestamp(3);
alter table identitylink_history modify history_timestamp timestamp(3) not null default current_timestamp(3);
alter table person modify timestamp timestamp(3) not null default current_timestamp(3);
alter table person modify create_timestamp timestamp(3) not null default current_timestamp(3);
alter table person_history modify history_timestamp timestamp(3) not null default current_timestamp(3);
alter table source modify create_timestamp timestamp(3) not null default current_timestamp(3);
alter table identitylink_history add identity_link_id bigint(20) default null;
alter table domain add timestamp timestamp(3) not null default current_timestamp(3);
update domain d set timestamp = (select d2.create_timestamp from (select create_timestamp, name from domain) d2 where d.name = d2.name);
alter table identifier_domain add timestamp timestamp(3) not null default current_timestamp(3);
update identifier_domain id set timestamp = (select id2.create_timestamp from (select create_timestamp, name from identifier_domain) id2 where id.name = id2.name);
alter table source add timestamp timestamp(3) not null default current_timestamp(3);
update source s set timestamp = (select s2.create_timestamp from (select create_timestamp, name from source) s2 where s.name = s2.name);
