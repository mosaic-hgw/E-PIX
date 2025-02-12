${ttp.readme.header}

# E-PIX 2024.3.0

## Improvements
*  Anzeige ob eine mögliche Dublette manuell hinzugefügt oder automatisch erkannt wurde
*  Konfiguration der Threads für paralleles Matching in der Weboberfläche
*  Anzeige der Datenquelle bei Dublettenauflösung und Personendetails
*  Allgemeine Verbesserungen in der Weboberfläche
*  Beschleunigter Aufruf des Dashboards

## Bug Fixes
*  Export aus Identifier-Domäne enthält pro Domäne und Teilnehmer nur einen Wert, obwohl der Teilnehmer mehrere Identifier in der Domäne besitzt


# E-PIX 2024.2.0

## Improvements
*  Domäne des lokalen Identifiers in Notifications mitsenden
*  Notifications bei verbleibenden Methoden sofern verfügbar via Weboberfläche senden


# E-PIX 2024.1.0

## New Features
*  Umfangreich konfigurierbare Validierung von Personen- und Zusatzfeldern

## Improvements
*  Prüfung ob Bloomfilter in gewähltes Feld passt
*  Mehr Hinweise bei Konfiguration einer Domäne in der Weboberfläche
*  Dashboard: Anzeige zurückgestellter Dubletten

## Bug Fixes
*  Suche nach Matchingfeldern bei aktivierter Option limitSearchForLowMemory nicht möglich
*  Fehlermeldung "no counter found for domain" aufgrund bisher verpflichtender Verwendung eins 4-stelliges Prefix + 9-stelliger MPI-ID
*  Bei Aufruf der Methode updateDomain wird der vorherige Zustand zurückgegeben
*  Unvollständige Übernahme von Änderungen der Domänenkonfiguration in den Cache

## Docker
*  Geänderte Logging-Variable: TTP_EPIX_LOG_TO_FILE zu TTP_EPIX_LOG_TO


# E-PIX 2023.2.1

## Bug Fixes
*  Merge Bugfixes von 2023.1.3


# E-PIX 2023.2.0

## New Features
*  Funktion updateActivePerson

## Improvements
*  Verwendung von JakartaEE statt JavaEE
*  Export und Import der Felder Vitalstatus und Sterbedatum
*  Download des Importergebnis mit Excel kompatiblem Encoding
*  Berücksichtigung des flexiblen Context-Roots beim Link vom E-PIX zum gPAS
*  Anpassbarer Context Root der Weboberfläche und SOAP-Schnittstelle
*  Benutzerdefinierte Auswahl der Spalte mit Identifiern beim Export anhand einer Identifierliste
*  Verbesserungen in der Oberfläche bei Export von Personen 3
*  Verbessertes Feedback in der Oberfläche, wenn IDAT bei Änderung stark abweichen
*  Historisierung der konkreten Identitäts-ID mit welcher ein automatischer Match stattgefunden hat
*  Ausblenden der Gesamtstatistik, sofern kein Recht für alle Domänen besteht
*  Lokalisierter Kalender in Datumsauswahl
*  Wählbarer Start und Endzeitpunkt in Statistik-Diagrammen

## Bug Fixes
*  Teilweise fehlende Erkennung des Identifiers beim Import mit automatischer Spaltenerkennung und externem Identifier
*  Falsche Auswahl des Feldtyps beim Import mit automatischer Spaltenerkennung und externem Identifier 0
*  Export enthält den Eintrag "null", wenn ein Feld den Wert null hat
*  Schneller Seitenwechsel führt zu leerer Protokollansicht 2 o
*  Interner Fehler beim Update einer aktiven Person mit deaktivierten Identitäten

## Docker
*  Anpassbarer Context Root der Weboberfläche und SOAP-Schnittstelle


# E-PIX 2023.1.3

## Bug Fixes
*  CSV-Download von Importergebnis enthält gekürzte Bloomfilter
*  Import einer Konfiguration mit "Mindestscore für möglichen Match = nie" schlägt fehl


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
*  Vollständige Domänen-Konfiguration in der Weboberfläche
*  Verknüpfung zu möglicher Dublette, wenn eine neue Person diese erzeugt
*  Bearbeitung von Kontakten
*  Historie über die Löschung von Identitäten
*  Historie über die Löschung von Kontakten und Identifiern
*  Historie über das Hinzufügen von Identifiern
*  [Rechte und Rollen: Domänenspezifische Vergabe von Berechtigungen per OIDC](https://www.ths-greifswald.de/ttp-tools/domain-auth)

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

## API Changes
*  Umbenennung von Funktionen welche ausschließlich aktive Personen (person ->activePerson) zurückgeben, betrifft getPersonsForDomain, getPersonsForDomainFiltered, getPersonsForDomainPaginated, getPersonByMPI, getPersonsByMPIBatch, getPersonByLocalIdentifier, getPersonByMultipleLocalIdentifier, sowie zugehörige count-Methoden
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

${ttp.readme.footer}
