
ALTER TABLE person_history
    ADD COLUMN `event` char(20) NOT NULL DEFAULT 'UNKNOWN' after `domain_name`,
    ADD COLUMN `user` varchar(100) DEFAULT NULL after `comment`;

ALTER TABLE contact_history
    ADD COLUMN `event` char(13) NOT NULL DEFAULT 'UNKNOWN' after `identity_id`,
    ADD COLUMN `comment` varchar(255) DEFAULT NULL after `event`,
    ADD COLUMN `user` varchar(100) DEFAULT NULL after `comment`;

ALTER TABLE identity_history
    ADD COLUMN `user` varchar(100) DEFAULT NULL after `comment`;

ALTER TABLE identitylink_history
    ADD COLUMN `user` varchar(100) DEFAULT NULL after `updated_identity`;

CREATE TABLE identifier_history (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    history_timestamp timestamp(3) NOT NULL DEFAULT current_timestamp(3),
    identifier_domain_name varchar(255) NOT NULL,
    value varchar(255) NOT NULL,
    active bit(1) DEFAULT NULL,
    description varchar(255) DEFAULT NULL,
    event char(20) NOT NULL,
    comment varchar(255) DEFAULT NULL,
    `user` varchar(100) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY `FK_identifier_history-identifier` (identifier_domain_name, value),
    CONSTRAINT `FK_identifier_history-identifier` foreign key (identifier_domain_name, value) references identifier (identifier_domain_name, value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
