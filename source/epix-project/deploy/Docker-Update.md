${ttp.readme.header}

# Aktualisierung der THS-Tools per Docker

## Hintergrund

**Am folgenden Beispiel wird die Aktualisierung der Docker-Container vom E-PIX gezeigt. Grundsätzlich findet die Aktualisierung aller THS-Tools (E-PIX, gPAS, gICS, Dispatcher) auf dieselbe Weise statt.**

Im Beispiel wird die bestehende und laufende Instanz vom E-PIX als `<epix-old>` bezeichnet. Die existierende Version (`<old-version>`) soll gesichert und ein Update auf eine neue Version vom E-PIX (`<epix-new>`, `<new-version>`) durchgeführt werden, ohne die bereits vorhandenen Daten in der MySQL-Datenbank zu verändern.

Ob die Instanzen vom E-PIX laufen, kann mit folgenden Befehl geprüft werden:
```
docker ps -a
```

## Handlungsanweisung

### Neue  Tool-Version von der THS-Webseite herunterladen

Die aktuelle Version von [ths-greifswald.de/epix](https://www.ths-greifswald.de/epix) herunterladen und entpacken, sowie auf das Host-System kopieren und sicherstellen, dass entsprechende Berechtigungen zum Ausführen der Dateien gesetzt sind.

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

### Aktualisieren der Deployments und Wildfly-Konfiguration

Den Datenbank-Container nun herunterfahren

```
docker epix-<old-version>-mysql down
```

#### Aktualisieren der Deployments

Die Deployments im `<epix-old>` Verzeichnis auf dem Host-System löschen und die neuen Deployments hinein kopieren

```
rm -f <epix-old>/deployments/* 
cp -R <epix-new>/deployments/ <epix-old>/deployments/
```

#### Aktualisieren der Bezeichnung des MySQL Containers

```
sudo docker rename epix-<old-version>-mysql epix-<new-version>-mysql
```

#### Aktualisieren der JBOSS Konfigurationsskripte

Die alten Dateien können gesichert oder gelöscht und die neuen müssen eingespielt werden:

```
mv <epix-old>/jboss <epix-old>/jboss-<old-version>
cp -R <epix-new>/jboss/ <epix-old>/jboss
```

Mit Hilfe dieser Dateien wird JBOSS nach den Vorgaben aus den `*.env`-Dateien konfiguriert.

#### Aktualisieren der Umgebungsvariablen für die JBOSS Konfigurationsskripte

In der aktuellen Version liegen die zugehörigen `*.env`-Dateien im Unterordner `./envs`. In älteren Versionen lagen diese direkt im Wurzelverzeichnis des Dockerpaketes. Falls eine solche Version aktualisiert werden soll, müssen zuvor die `*.env`-Dateien in den Unterordner `./envs` verschoben werden:

```
mkdir <epix-old>/envs
mv <epix-old>/*.env <epix-old>/envs/
```

Dieser Ordner sollte auch gesichert werden:

```
cp -R <epix-old>/envs <epix-old>/envs-<old-version>
```

Nun muss die Liste der neuen und umbenannten `ENV`-Variablen in der `README_gICS.md` im Wurzelverzeichnis des Dockerpaketes studiert werden, um gegebenenfalls die `*.env`-Dateien entsprechend anzupassen. **Achtung**: unter Umständen wurden auch die Namen der `*.env`-Dateien geändert. Dies muss auf auf jeden Fall angepasst werden.

**Alternativ** kann man mit etwas höherem Aufwand die Anpassungen der alten `*.env`-Dateien manuell in die neuen übertragen, um größtmögliche Ähnlichkeit zwischen den angepassten und ausgelieferten `*.env`-Dateien zu bewahren. Dazu müssen die alten angepassten Dateien  gesichert und die neuen eingespielt werden:

```
mv <epix-old>/envs <epix-old>/envs-<old-version>
cp -R <epix-new>/envs/ <epix-old>/envs
```

Anschließend müssen alle manuellen Anpassungen der alten in die neuen `*.env`-Dateien übertragen werden. Dabei ist hohe Aufmerksamkeit erforderlich. Wir empfehlen die Nutzung eines grafischen Diff-Werkzeuges (z.B [devart Code Compare Free](https://www.devart.com/codecompare/featurematrix.html)). Für zukünftige Updates ist es hilfreich, die bestehenden Skeletons, Beispiele und Kommentare nicht zu ändern, sondern die eigenen Zeilen zu ergänzen und durch einen leicht wiederauffindbaren Kommentar zu markieren.

#### Aktualisieren der Docker-Compose-Konfiguration

Die alte angepasste Datei muss gesichert und die neue eingespielt werden:

```
mv <epix-old>/docker-compose.yml <epix-old>/docker-compose-<old-version>.yml
cp <epix-new>/docker-compose.yml <epix-old>/docker-compose.yml
```

Wahrscheinlich müssen auch hier die Anpassungen der alten `docker-compose.yml` (inbesondere für Ports und Volumes) in die neue übertragen werden (am besten wieder mit Hilfe eines grafischen Diff-Werkzeuges).

#### Aktualisieren der Dokumentationsdateien

Schließlich empfiehlt es sich, auch die aktualisierten Dokumentationsdateien zu übertragen:

```
rm -f <epix-old>/*.md 
cp <epix-new>/*.md <epix-old>/
rm -f <epix-old>/docs/* 
cp -R <epix-new>/docs/ <epix-old>/docs/
```

#### Anpassen des Eigentümer-Benutzers

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

${ttp.readme.footer}
