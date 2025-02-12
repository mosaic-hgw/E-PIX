-- Adds column to store type of creation (manual or automatic). default value will be overwritten later
ALTER TABLE identitylink
    ADD type char(16) NOT NULL DEFAULT 'DEFAULT';

-- Adds the timestamp of identitylink creation. The default value is 2000-01-01. The actual value cannot be set because this data cannot be reproduced.
-- Adds column to store type of creation (manual or automatic). default value will be overwritten later
ALTER TABLE identitylink_history
    ADD initial_create_timestamp timestamp(3) NOT NULL DEFAULT '2000-01-01 00:00:00',
    ADD type char(16) NOT NULL DEFAULT 'DEFAULT';


-- Overwrites the default value of creation type in identitylink, based on the defined thresholds in the domain config
UPDATE identitylink il
    INNER JOIN identity ident ON il.src_identity = ident.id
    INNER JOIN person pers ON ident.person_id = pers.id
    INNER JOIN domain d ON pers.domain_name = d.name
    SET
        il.TYPE =
        CASE
        WHEN il.threshold < EXTRACTVALUE(d.config, '//ma:MatchingConfiguration//matching//threshold-possible-match') THEN 'MANUAL'
        WHEN il.threshold >= EXTRACTVALUE(d.config, '//ma:MatchingConfiguration//matching//threshold-possible-match') THEN 'AUTOMATIC'
END
WHERE
    1 = 1;

-- Overwrites the default value of creation type in identitylink history, based on the defined thresholds in the domain config
UPDATE identitylink_history ilh
    INNER JOIN identity ident ON ilh.src_identity = ident.id
    INNER JOIN person pers ON ident.person_id = pers.id
    INNER JOIN domain d ON pers.domain_name = d.name
    SET
        ilh.TYPE =
        CASE
        WHEN ilh.threshold < EXTRACTVALUE(d.config, '//ma:MatchingConfiguration//matching//threshold-possible-match') THEN 'MANUAL'
        WHEN ilh.threshold >= EXTRACTVALUE(d.config, '//ma:MatchingConfiguration//matching//threshold-possible-match') THEN 'AUTOMATIC'
END
WHERE
    1 = 1;

-- Fix default for stat_entry in epix installations before version 2.13
ALTER TABLE stat_entry MODIFY COLUMN ENTRYDATE timestamp(3) DEFAULT CURRENT_TIMESTAMP(3)  NOT NULL; 