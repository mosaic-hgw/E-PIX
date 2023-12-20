![context](https://www.ths-greifswald.de/wp-content/uploads/2019/01/Design-Logo-THS-deutsch-542.png)

Stand: Novenmber 2022

# Aktualisierung der THS-Tools in Docker(-Compose)

## Hintergrund

**Am folgenden Beispiel wird die Aktualisierung der Docker-Container vom E-PIX gezeigt. Grundsätzlich findet die Aktualisierung aller THS-Tools (E-PIX, gPAS, gICS, Dispatcher) auf dieselbe Weise statt.**

Im Beispiel wird die bestehende und laufende Instanz vom E-PIX als `<epix-old>` bezeichnet. Die existierende Version (`<old-version>`) soll gesichert und ein Update auf eine neue Version vom E-PIX (`<epix-new>`, `<new-version>`) durchgeführt werden, ohne die bereits vorhandenen Daten in der MySQL-Datenbank zu verändern.

Ob die Instanzen vom E-PIX laufen, kann mit folgenden Befehl geprüft werden:
```
docker ps -a
```

## Handlungsanweisung

### Neue  Tool-Version von der THS-Webseite herunterladen

Die aktuelle Version von [ths-greifswald.de/epix](www.ths-greifswald.de/epix) herunterladen und entpacken, sowie auf das Host-System kopieren und sicherstellen, dass entsprechende Berechtigungen zum Ausführen der Dateien gesetzt sind.

```
CHMOD -R 755 /PFAD
```

### Sichern der aktuellen Docker-Konfiguration

Um auf dem Host-System den derzeitigen Stand der E-PIX-Konfiguration (Wildfly-Skripte, etc.) zu sichern, den entsprechenden Ordner per TAR-Archiv sichern:

```
tar czf backup-epix-2022-03-31.tgz <epix-old>/
```

### Sichern der existierenden Datenbank

Um zusätzlich die Sicherung der existierenden Datenbank durchzuführen, wird ein MySQL-Dump über die Docker-Konsole angestoßen und die resultierende Export-Datei im Dateisystem vom Host abgelegt.

```
sudo docker exec epix-<old-version>-mysql /usr/bin/mysqldump -u epix_user -p epix > backup-epix-<old-version>-2022-03-31.sql
```

Der Name der bestehenden MySQL-Instanz muss entsprechend angepasst werden.

### Aktualisieren der Datenbank

Für alle Versionen sind die Datenbank-Aktualisierungsskripte jeweils im Docker-Verzeichnis unter *`<epix-new>/update_scripts`* zu finden. Die Update-Skripte müssen in den Docker-Container kopiert werden, wobei nur die Skripte erforderlich sind, welche die Version zwischen `<epix-old>` zu `<epix-new>` betreffen.

```
docker cp <epix-new>/update_scripts/ epix-<old-version>-mysql:/update-files/
```

Je nachdem von welcher Version aus E-PIX aktualisiert werden soll, müssen die relevanten SQL-Skripte *chronologisch durchlaufen* werden.

**Beispiel:** Für ein Update von Version 2.11.0 auf 2.13.0 sind demzufolge die Skripte *update_database_epix_2.11.x-2.12.x.sql* und *update_database_epix_2.12.x-2.13.x.sql* auszuführen.

Dazu per MySQL Client mit der bestehenden Datenbank verbinden und die Update-Skripte nacheinander durchlaufen. Dies kann per *docker* realisiert werden (Nutzernamen und Passwörter ggf. anpassen).

**Beispiel:**

```
docker exec -it epix-2.11.0-mysql /usr/bin/mysql -u epix_user -p -e "USE epix; $(cat epix-new/standard/update_database_epix_2.11.x-2.12.x.sql)"
docker exec -it epix-2.11.0-mysql /usr/bin/mysql -u epix_user -p -e "USE epix; $(cat epix-new/standard/update_database_epix_2.12.x-2.13.x.sql)"
```

### Aktualisierung der Deployments und Wildfly-Konfiguration

Den Datenbank-Container nun herunterfahren

```
docker epix-<old-version>-mysql down
```

Die Deployments im `<epix-old>` Verzeichnis auf dem Host-System löschen und die neuen Deployments hinein kopieren

```
rm -f <epix-old>/deployments/* 
cp -R <epix-new>/deployments/ <epix-old>/deployments/
```

Aktualisierung der Bezeichnung des MySQL Containers

```
sudo docker rename epix-<old-version>-mysql epix-<new-version>-mysql
```

JBOSS Konfiguration aktualisieren

```
cp -R <epix-new>/jboss/ <epix-old>/jboss/
```

Docker-Compose-Konfiguration aktualisieren

```
cp -R <epix-new>/docker-compose.yml <epix-old>/docker-compose.yml
```

Anpassen des Eigentümer-Benutzers

```
chown 999 <epix-new>/sqls
chown 1000 <epix-new>/deployments
chown 1000 <epix-new>/logs
chown 1000 <epix-new>/jboss
```

### Starten des aktualisierten Containers

Den aktualisierten Container mittels folgendem Befehl starten (-d um Container im Hintergrund zu starten):

```
docker-compose up -d
```

Den Erfolg der Aktualisierung prüfen durch Aufruf des Web-Frontends unter [http://IPADDRESS:8080/epix-web](http://ipaddress:8080/epix-web).

## Im Fehlerfall: Wiederherstellung der Datenbank

Im Fehlerfall, kann die bisherige Datenbank wiederhergestellt werden (sofern die Anleitung befolgt wurde). Nutzernamen und Passwort ggf. anpassen.

```
docker exec -it epix-<new-version>-mysql /usr/bin/mysql -u epix_user -p -e "USE epix; $(cat backup-epix-2022-03-31.sql)"
```

# Additional Information #

The E-PIX was developed by the University Medicine Greifswald  and published in 2014 as part of the [MOSAIC-Project](https://ths-greifswald.de/mosaic "")  (funded by the DFG HO 1937/2-1).

Selected functionalities of E-PIX were developed as part of the following research projects:
- MIRACUM (funded by the German Federal Ministry of Education and Research 01ZZ1801M)
- NUM-CODEX (funded by the German Federal Ministry of Education and Research 01KX2021)

## Credits ##
Concept and implementation: L. Geidel

Web-Client: A. Blumentritt, F.M. Moser

Docker: R. Schuldt

Privacy Preserving Record Linkage: C. Hampf

## License ##
License: AGPLv3, https://www.gnu.org/licenses/agpl-3.0.en.html

Copyright: 2009 - 2022 University Medicine Greifswald

Contact: https://www.ths-greifswald.de/kontakt/

## Publications ##

Hampf et al. 2020 "Assessment of scalability and performance of the record linkage tool E‑PIX® in managing multi‑million patients in research projects at a large university hospital in Germany", https://translational-medicine.biomedcentral.com/articles/10.1186/s12967-020-02257-4

https://dx.doi.org/10.3414/ME14-01-0133

https://dx.doi.org/10.1186/s12967-015-0545-6

# Supported languages #
German, English