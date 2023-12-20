${ttp.epix.readme.header}

# E-PIX 2023.1.2

## Bug Fixes
*  Quellfeld-Seed wird nicht in Bloomfilterkonfigurationen gespeichert
*  Löschen von Personen mit Identifiern nicht möglich
*  Mögliche NullPointerException bei Benutzung des SOAP-Interfaces ohne Authentifizierung

# E-PIX 2023.1.1

## Improvements
*  Dateibasierten Domain Import und Export wieder ermöglichen

## Bug Fixes
*  DomainConfig nicht speicherbar je nach Konfiguration
*  Aktualisierung von slf4j-log4j12 in ths-notification-client aufgrund von Vulnerabilities in log4j 1.2.16
*  Sprung-URL zum gPAS fehlerhaft
*  CSV Exception bei mehreren Anführungszeichen im Import
*  Filtern im Protokoll zeigt leeres Ergebnis bei schnellem Tippen

# E-PIX 2023.1.0

## New Features
* Vollständige Domänen-Konfiguration in der Weboberfläche
* Verknüpfung zu möglicher Dublette, wenn eine neue Person diese erzeugt
* Bearbeitung von Kontakten
* Historie über die Löschung von Identitäten
* Historie über die Löschung von Kontakten und Identifiern
* Historie über das Hinzufügen von Identifiern
* [Rechte und Rollen: Domänenspezifische Vergabe von Berechtigungen per OIDC](https://www.ths-greifswald.de/ttp-tools/domain-auth)

## Improvements
*  Hinweis, wenn keine Lokaler-Identifier Domäne zur Verfügung steht
*  Erkennung der Identifier-Domäne beim Import
*  Fortschrittsbalken bei Verwendung einer Identifier-Liste für den Export
*  Umbenennung von Funktionen welche ausschließlich aktive Personen zurückgeben
*  Umbenennung lokaler Identifier zu externen Identifiern (Weboberfläche)
*  DTOs für PreprocessingConfig
*  Anzeige des lesbaren Benutzernamens bei Login via OIDC (Keycloak)

## Bug Fixes
*  Fehler bei Suche nach Geburtsdatum, wenn Identitäten ohne Geburtsdatum gespeichert sind
*  Doppelte Anzeige externer Identifier, wenn sie mit mehreren Identitäten der gleichen Person verknüpft sind
*  Funktion setReferenceIdentity gibt NULL zurück
*  [Stored XSS Vulnerability in der Weboberfläche](https://github.com/mosaic-hgw/gICS/issues/2)
*  Anzeige des lesbaren Benutzernamens bei Login via OIDC (Keycloak)

## Docker
*  Fail-Fast-Strategie für Docker-CLI-Skripte


# E-PIX 3.0.1

## Improvements
* Löschen von lokalen Identifiern
* Dependency-Updates
* Anpassung LogLevel NotificationService TRACE -> INFO

# E-PIX 3.0.0

## New Features
*  Vitalstatus und Sterbedatum bei Personen
*  Auflistung aller Identitäten in Personenansicht
*  Notificationunterstützung für weitere Methoden
*  Warteliste zur späteren Bearbeitung möglicher Dubletten
*  Export möglicher Dubletten
*  Historie über Bearbeitungen, Dublettenaktionen, Kontakte und Identifier in Personenansicht
*  Löschen von Adressen, Identitäten und Personen
*  Keycloak-basierte Absicherung der SOAP-Requests
*  ComplexTransformator zur Umwandlung von Zeichen in ASCII
*  Batchfunktion für getPersonByMPI
*  Auflistung aller Kontakte in Personenansicht
*  Öffnen einer Person über GET Parameter
*  Anzeige möglicher Matches in Personenansicht
*  Funktion zum Ermitteln einer aktivierten oder deaktivierten Person anhand der firstMPI

## Improvements
*  Auflistung aller Identitäten in Personenansicht
*  Unterstützung von Anführungszeichen in CSV Imports
*  Festlegung der Referenzidentität in Personenansicht
*  Dashboard Statistiken für Summe aller Domänen
*  Einzugs- und Auszugsdatum bei Adressen
*  Filterung nach MPI bei Dublettenauflösung
*  Upgrade auf Java 17
*  Strukturierte Eingabemaske zum Anlegen neuer Personen
*  Nutzbarkeit aller IDAT Felder bei der Suche nach einer Person

## Bug Fixes
*  PropertyNotFoundException beim Erstellen einer neuen Identifier-Domäne
*  Null Pointer Exception im Frontend nach Anlage einer Domäne via SOAP
*  Frontend akzeptiert ungültige Datumsangaben und rechnet sie automatisch um
*  Fehlerhafte Zuordnung von Daten im Zuwachsdiagramm
*  Identifier-Domäne wird beim Löschen einer Domäne zurückgesetzt

## Docker
*  Docker Upgrade auf Wildfly 26
*  Erhöhung von MAX_ALLOWED_PACKETSIZE für MySQL8 in Docker auf 10MB
*  Vereinfachung Zusammenführung der separaten Docker-Compose-Pakete der einzelnen Tools
*  OIDC-Compliance: Unterstützung KeyCloak 19 für ALLE Schnittstellen
*  Vereinheitlichung der Konfiguration der Keycloak-basierten Authentifizierung für alle Schnittstellen
*  Unterstützung Client-basierter Rollen in KeyCloak

## FHIR

* FHIR-Endpoint zum Anlegen, Aktualisieren und Suchen von Personen. Details im [Implementation Guide](https://www.ths-greifswald.de/e-pix/fhir) sowie im [Handbuch](https://www.ths-greifswald.de/e-pix/handbuch/3-0-0)

# E-PIX 2.13.2

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

${ttp.epix.readme.footer}