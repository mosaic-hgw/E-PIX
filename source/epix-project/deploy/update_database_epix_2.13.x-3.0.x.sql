
alter table identity
    add column vital_status int DEFAULT 0 AFTER `birth_place`,
    add column date_of_death datetime DEFAULT NULL AFTER `vital_status`;

alter table identity_history
    add column vital_status int DEFAULT 0 AFTER `birth_place`,
    add column date_of_death datetime DEFAULT NULL AFTER `vital_status`;

alter table contact
    add column date_of_move_in datetime DEFAULT NULL AFTER `municipality_key`,
    add column date_of_move_out datetime DEFAULT NULL AFTER `date_of_move_in`;

alter table contact_history
    add column date_of_move_in datetime DEFAULT NULL AFTER `municipality_key`,
    add column date_of_move_out datetime DEFAULT NULL AFTER `date_of_move_in`;

alter table identitylink
    add column priority char(14) NOT NULL DEFAULT 'OPEN';
