USE epix;

-- declare global boundary conditions
set @from_date := '2022-08-31';
set @domain_name := 'Test2';

-- create a common table expression (CTE) with comprehensive info from the history of identities never linked as source for matches
WITH info_for_identities_never_linked_as_src AS (
    SELECT oih.*, i.person_id AS personid
    FROM identity_history oih
             INNER JOIN person p ON p.id = oih.person_id
             INNER JOIN identity i ON i.id = oih.identity_id
    WHERE p.domain_name = @domain_name
      AND p.create_timestamp > TIMESTAMP(@from_date)
      AND i.id NOT IN (SELECT il.src_identity FROM identitylink il)
)

-- Felder, die im KKR nicht besetzt sind, koennen noch entfernt werden
SELECT DISTINCT
    r.personid, r.identity_version, r.date_of_birth, r.gender, r.birth_place, r.vital_status, r.date_of_death, r.civil_status, r.degree,
    r.first_name, r.last_name, r.middle_name, r.mother_tongue, r.mothers_maiden_name, r.nationality, r.`event`,	r.prefix, r.race,
    r.religion, r.suffix, r.value1, r.value2, r.value3, r.value4, r.value5, r.value6, r.value7, r.value8, r.value9,
    r.forced_reference, r.`comment`, r.matchingScore
FROM
(
    (
        -- Datensaetze die einmalig registriert worden sind
        SELECT * FROM info_for_identities_never_linked_as_src
        WHERE person_id in
        (
            SELECT ih.person_id
            FROM identity_history ih
            GROUP BY ih.person_id
            HAVING COUNT(ih.person_id) = 1
        )
	   	AND deactivated = 0 -- only ignore deactivated persons if they are registered only once
        LIMIT 5000
    )
    UNION ALL
    (
        -- Datensaetze, die manuell gemerged wurden
        SELECT * FROM info_for_identities_never_linked_as_src
        WHERE person_id in
        (
              SELECT manih.person_id
              FROM
              (
                  SELECT *
                  FROM identity_history ih1
                  WHERE ih1.`event` IN ('NEW', 'MERGE')
              ) AS manih
              GROUP BY manih.person_id
              HAVING COUNT(manih.person_id) > 1
        )
        AND `event` IN ('NEW', 'MERGE')
        LIMIT 2500
    )
    UNION ALL
    (
        -- Datensaetze, die automatisch gemerged wurden
        SELECT * FROM info_for_identities_never_linked_as_src
        WHERE person_id in
        (
              SELECT autih.person_id
              FROM
              (
                  SELECT *
                  FROM identity_history ih
                  WHERE ih.`event` IN ('NEW', 'MATCH') -- Perfect Match bewusst raus gelassen, damit identische Datensaetze nicht doppelt enthalten sind
              ) AS autih
              GROUP BY autih.person_id
              HAVING COUNT(autih.person_id) > 1
        )
        AND `event` IN ('NEW', 'MATCH')
        LIMIT 2500
    )
) AS r
ORDER BY r.personid,
         CASE
             WHEN r.`event` = 'NEW' THEN 1
             WHEN r.`event` = 'MATCH' THEN 2
             WHEN r.`event` = 'MERGE' THEN 3
         END
;
