-- Fix event name for perfect match in epix installations before version 2.7
UPDATE identity_history SET `event` = "PERFECT_MATCH" WHERE `event` = "PERFECTMATCH";

ALTER TABLE identity_history
    ADD COLUMN `matching_identity_id` bigint(20) DEFAULT NULL after `matchingScore`;
