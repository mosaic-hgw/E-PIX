
![context](https://user-images.githubusercontent.com/12081369/49164561-a4481500-f32f-11e8-9f0d-fa7a730f4b9d.png)

# About #
The Record Linkage and ID Management solution E-PIX (Enterprise Identifier Cross Referencing) applies the propabilistic Fellegi-Sunter-algorithm and the Levenshtein distance to avoid duplicate participant entries. The independent software module facilitates participant management and multisite-aggregation of medical research data. Additionally, the correction of potential synonym errors is supported (i.e. false-negative record linkage).

## Live-Demo and more information ##

Try out E-PIX from https://demo.ths-greifswald.de 

or visit https://ths-greifswald.de/epix for more information.

# Versions and documentation #

[Source-Code](https://github.com/mosaic-hgw/E-PIX/source "")

[Docker-compose version of E-PIX (Standard)](https://github.com/mosaic-hgw/E-PIX/docker/standard "")

[Docker-compose version of E-PIX (Web-Auth)](https://github.com/mosaic-hgw/E-PIX/docker/web-auth "") <strong>(Announced for Feb. 2020)</strong> 

# Web-based interfaces
All functionalities of the E-PIX are provided for external use via SOAP-interfaces. 

Record Linkage and ID administration: ``http://<YOUR IPADDRESS>:8080/epix/epixService?wsdl``

Configuration and domain management: ``http://<YOUR IPADDRESS>:8080/epix/epixManagementService?wsdl``

[Java Documentation of the interfaces](https://www.ths-greifswald.de/spezifikationen/soap/epix)

Use SOAP-UI to create sample requests.

# IT-Security Recommendations #
For the operation of E-PIX at least following IT-security measures are recommended:
* operation in a separate network-zone
* use of firewalls and IP-filters
* access restriction to the E-PIX-Servers with basic authentication (e.g. with nginx or apache)

# Additional Information #

The E-PIX was developed by the University Medicine Greifswald  and published in 2014 as part of the [MOSAIC-Project](https://ths-greifswald.de/mosaic "")  (funded by the DFG HO 1937/2-1).

## Credits ##
Concept and implementation: L. Geidel

Web-Client: A. Blumentritt

## License ##
License: AGPLv3, https://www.gnu.org/licenses/agpl-3.0.en.html

Copyright: 2014 - 2020 University Medicine Greifswald

Contact: https://www.ths-greifswald.de/kontakt/

## Publications ##
https://dx.doi.org/10.3414/ME14-01-0133

https://dx.doi.org/10.1186/s12967-015-0545-6

# Screenshots #

Record Linkage

![context](https://raw.githubusercontent.com/mosaic-hgw/E-PIX/master/docker/standard/screenshots/E-PIX-Screenshot-Dublettenaufl%C3%B6sung.png)

Processing of Lists

![context](https://raw.githubusercontent.com/mosaic-hgw/E-PIX/master/docker/standard/screenshots/E-PIX-Screenshot-Listenverarbeitung.png)

Adding Patients

![context](https://raw.githubusercontent.com/mosaic-hgw/E-PIX/master/docker/standard/screenshots/E-PIX-Screenshot-Personen-erfassen.png)
