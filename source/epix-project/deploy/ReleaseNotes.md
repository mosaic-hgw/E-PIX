![context](https://user-images.githubusercontent.com/12081369/49164561-a4481500-f32f-11e8-9f0d-fa7a730f4b9d.png)

Current Docker-Version of E-PIX: 2.13.1, May 2022

# E-PIX 2.13.1

## Improvements
*  Anzeige lokaler Identifier von Nebenidentitäten

## Bug Fixes
*  Fehlende Anzeige von Freitextfeldern im Frontend
*  Perfect Matches werden als Match behandelt
*  Fehler bei mehreren Identifier Domänen mit leerer OID

# E-PIX 2.13.0

## New Features
*  Filtern in der Dublettenauflösung
*  Notifications für weitere Methoden
*  Konfigurierbares Enum für Dublettenauflösungsbegründung
*  Dashboard

## Improvements
*  Erhöhte Geschwindigkeit der Protkollansicht im Frontend durch Lazy Loading
*  Erhöhte Geschwindigkeit der Dublettenauflösung im Frontend durch Lazy Loading
*  UI Verbesserungen bei Bloomfiltern
*  Nachträgliche Erzeugung von Bloomfiltern bei großen Datenbeständen führt zu Timeout
*  Parameter use-notifications nur noch im Frontend berücksichtigen
*  Eigenes Interface für Servicemethoden mit Versand von Benachrichtigungen

## Bug Fixes
*  Sprachwechsel beim Editieren einer Person führt zu Anzeige eines falschen Geburtsdatums
*  Falsche Data-Source in CLI für Docker
*  Löschen von Identitäten führt in bestimmten Fällen zu SQLException

## Docker

* Anpassung und Umstrukturierung der ENV-Files. Details und Änderungsübersicht in beiliegender ReadMe.MD
* Konfigurierbares Notification-Modul für E-PIX. Details zur Konfiguration [online](https://www.ths-greifswald.de/ttp-tools/notifications)

# E-PIX 2.12.1

## New Features
* Nachträgliches Generieren von Bloomfilter für bestehende Identitäten

# E-PIX 2.12.0

## Improvements
* Unterstützung für Possible Matches bei Konfiguration mit nur einem Matching-Feld
* Auslagerung von Docker in separates Modul
* Berücksichtigung von ß bei CharsMutationTransformation

## Bug Fixes
* Sicherstellung der Reihenfolge vom Alphabet beim Random-Hashing
* Dubletten-Details öffnen sich nicht mehr nach dem Hinzufügen oder Auflösen einer Dublette

# E-PIX 2.10.0

## New Features
* Optionales Senden einer Merge Notification an den Dispatcher
* Anpassbare Schriftgröße im Frontend
* Werkzeugübergreifendes Cookie zum Speichern von Frontendeinstellungen
* Darkmode im Frontend

## Improvements
* Vereinheitlichung von Date, Time, Datetime Pattern in allen Werkzeugen
* Kompatibilität mit Java 9+
* Allgemeine Verbesserungen der Accessibility
* Allgemeine Verbesserungen im Frontend
* Verwendung der Label aus Domänenkonfiguration für Werte-Felder
* Vereinheitlichung der Dateinamen von Exports aller Tools
* Automatischer Fokus auf erstem Eingabefeld in Dialogen und Formularen
* Änderung des Standard-Separator beim Import auf ;
* Änderung des Encoding beim Export in UTF-16LE
* Automatische Erkennung des Encodings beim Import
* Unterstützung mehrerer lokaler Identifier beim Import
* Erkennung des Datumsformats beim Import
* Update auf Primefaces 8.0
* Verbesserte Erkennung der Spaltenbezeichnungen beim Import
* Option für erzwungenes Update beim Import mit MPI
* Ausblendung nicht konfigurierter Werte-Felder beim Import
* Abschließen des Anlegeformulars mit Enter

## Bug Fixes
* Infoseite lässt sich bei leerer Datenbank nicht öffnen
* Alphabetische Sortierung von Domänen
* Fehlen der MPI in der Protokollansicht nach einem Merge
* RequestMPI mit null Gender auch möglich, wenn dies ein Pflichtfeld ist
* Exception bei RequestMPI mit null Gender

# Additional Information
Selected functionalities of E-PIX were developed as part of the following research projects:
- MIRACUM (funded by the German Federal Ministry of Education and Research 01ZZ1801M)
- NUM-CODEX (funded by the German Federal Ministry of Education and Research 01KX2021)

## Credits ####
Concept and implementation: L. Geidel

Web-Client: A. Blumentritt, F.M. Moser

Docker: R. Schuldt

Bloom-Filter: C. Hampf

## License ####
**License:** AGPLv3, https://www.gnu.org/licenses/agpl-3.0.en.html

**Copyright:** 2009 - 2022 University Medicine Greifswald

**Contact:** https://www.ths-greifswald.de/kontakt/

## Publications ####
- Hampf et al. 2020 "Assessment of scalability and performance of the record linkage tool E‑PIX® in managing multi‑million patients in research projects at a large university hospital in Germany", https://translational-medicine.biomedcentral.com/articles/10.1186/s12967-020-02257-4
- http://dx.doi.org/10.3414/ME14-01-0133
- http://dx.doi.org/10.1186/s12967-015-0545-6
