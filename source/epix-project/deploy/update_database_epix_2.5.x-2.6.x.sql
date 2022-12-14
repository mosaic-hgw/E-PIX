USE epix;

alter table person drop column person_type;
alter table person drop column department;
alter table person drop column institute;
alter table person_history drop column person_type;
alter table person_history drop column department;
alter table person_history drop column institute;
alter table person_preprocessed drop column person_type;

drop table account_role;
drop table account;
drop table role;
