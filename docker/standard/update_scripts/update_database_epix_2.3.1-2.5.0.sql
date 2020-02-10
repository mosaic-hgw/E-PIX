USE epix;
-- der fk war im create-script ohne on update cascade
ALTER TABLE person_preprocessed
DROP FOREIGN KEY `FK_person_preprocessed-person_group`;

update person_preprocessed pp set person_group_id = (select personGroup_id from person p where p.id = pp.person_id);

ALTER TABLE person_preprocessed
ADD CONSTRAINT `FK_person_preprocessed-person_group`
  FOREIGN KEY (person_group_id)
  REFERENCES person (personGroup_id)
  ON DELETE NO ACTION
  ON UPDATE CASCADE;
