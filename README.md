
![context](https://user-images.githubusercontent.com/12081369/49164561-a4481500-f32f-11e8-9f0d-fa7a730f4b9d.png)

[Latest Docker-Compose Version](https://www.ths-greifswald.de/e-pix/#_download "")

# About #
The Record Linkage and ID Management solution E-PIX (Enterprise Identifier Cross Referencing) applies the propabilistic Fellegi-Sunter-algorithm and the Levenshtein distance to avoid duplicate participant entries. The independent software module facilitates participant management and multisite-aggregation of medical research data. Additionally, the correction of potential synonym errors is supported (i.e. false-negative record linkage).

# Download #

[Latest Docker-compose version of E-PIX](https://www.ths-greifswald.de/e-pix/#_download "")

# Source #

https://github.com/mosaic-hgw/E-PIX/tree/master/source

## Live-Demo and more information ##

Try out E-PIX from https://demo.ths-greifswald.de 

or visit https://ths-greifswald.de/epix for more information.

# API

## SOAP
All functionalities of the E-PIX are provided for external use via SOAP-interfaces. 

Record Linkage and ID administration: ``http://<YOUR IPADDRESS>:8080/epix/epixService?wsdl``

Configuration and domain management: ``http://<YOUR IPADDRESS>:8080/epix/epixManagementService?wsdl``

[Java Documentation of the interfaces](https://www.ths-greifswald.de/spezifikationen/soap/epix)

Use SOAP-UI to create sample requests.

# IT-Security Recommendations #

For the operation of E-PIX at least following IT-security measures are recommended:
* use **integrated authentication and authorization mechanism (gRAS)** or **keycloak-support** to secure access and grant privileges to epix-web (see [supplementary documentation](https://www.ths-greifswald.de/ttp-tools/keycloak) for details)
* operation in a separate network-zone
* use of firewalls and IP-filters
* access restriction to the gPAS-Servers with basic authentication (e.g. with nginx or apache)

# Additional Information #

The E-PIX was developed by the University Medicine Greifswald  and published in 2014 as part of the [MOSAIC-Project](https://ths-greifswald.de/mosaic "")  (funded by the DFG HO 1937/2-1).

## Credits ##
Concept and implementation: L. Geidel

Web-Client: A. Blumentritt

Docker: R. Schuldt

Bloom-Filter: C. Hampf

## License ##
License: AGPLv3, https://www.gnu.org/licenses/agpl-3.0.en.html

Copyright: 2014 - 2021 University Medicine Greifswald

Contact: https://www.ths-greifswald.de/kontakt/

## Publications ##

Hampf et al. 2020 "Assessment of scalability and performance of the record linkage tool E‑PIX® in managing multi‑million patients in research projects at a large university hospital in Germany", https://translational-medicine.biomedcentral.com/articles/10.1186/s12967-020-02257-4

https://dx.doi.org/10.3414/ME14-01-0133

https://dx.doi.org/10.1186/s12967-015-0545-6

# Supported languages #
German, English

# Screenshots #

Record Linkage

![context](https://www.ths-greifswald.de/wp-content/uploads/2019/07/E-PIX-Screenshot-Dublettenaufl%C3%B6sung-600x298.png)

Processing of Lists

![context](https://www.ths-greifswald.de/wp-content/uploads/2019/01/E-PIX-Screenshot-Listenverarbeitung.png)

Adding Patients

![context](https://www.ths-greifswald.de/wp-content/uploads/2019/01/E-PIX-Screenshot-Personen-erfassen.png)
