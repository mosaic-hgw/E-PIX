-- Truncate epix_2.1
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
start Transaction;
Truncate Table contact_preprocessed;
Truncate Table person_preprocessed;
Truncate Table contact_history;
Truncate Table person_history;
Truncate Table contact;
Truncate Table identifier;
-- Truncate Table identifier_history;
Truncate Table person;
Truncate Table personlink;
Truncate Table person_history_identifier;

Truncate Table person_group;
Truncate Table person_group_history;
Truncate Table person_identifier;
Truncate Table personlink_history;
commit;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=1 */;