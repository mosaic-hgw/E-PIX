![context](https://user-images.githubusercontent.com/12081369/49164561-a4481500-f32f-11e8-9f0d-fa7a730f4b9d.png)

Current Docker-Version of E-PIX: 2023.1.2 (Okt. 2023)       <br/>
Current Docker-Version of TTP-FHIR-Gateway: 2023.1.2 (October 2023), Details from [ReleaseNotes](https://www.ths-greifswald.de/ttpfhirgw/releasenotes/2023-1-2)

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
The [JavaDoc specs for the Services](https://www.ths-greifswald.de/epix/doc "")
are available online (see package `org.emau.icmvc.ttp.epix.service`).

Use SOAP-UI to create sample requests based on the WSDL files.

### Service-Interface for Record Linkage and ID Administration

The WSDL URL is [http://&lt;YOUR IPADDRESS&gt;:8080/epix/epixService?wsdl](https://demo.ths-greifswald.de/epix/epixService?wsdl)

### Service-Interface for Record Linkage and ID Administration with Notifications

The WSDL URL is [http://&lt;YOUR IPADDRESS&gt;:8080/epix/epixServiceWithNotification?wsdl](https://demo.ths-greifswald.de/epix/epixServiceWithNotification?wsdl)

### Service-Interface for Configuration and Domain Management

The WSDL URL is [http://&lt;YOUR IPADDRESS&gt;:8080/epix/epixManagementService?wsdl](https://demo.ths-greifswald.de/epix/epixManagementService?wsdl)

## FHIR

More details from https://www.ths-greifswald.de/e-pix/fhir

# IT-Security Recommendations #

Access to relevant application and database servers of the Trusted Third Party tools should only be possible for authorised personnel and via authorised end devices. We therefore recommend additionally implementing the following IT security measures:

* Operation of the relevant servers in separate network zones (separate from the research and supply network).
* Use of firewalls and IP filters
* Access restriction at URL level with Basic Authentication (e.g. with NGINX or Apache)
* use of Keycloak to restrict access to Web-Frontends and technical interfaces

# Additional Information #
Selected functionalities of E-PIX were developed as part of the following research projects:
- MIRACUM (funded by the German Federal Ministry of Education and Research 01ZZ1801M)
- NUM-CODEX (funded by the German Federal Ministry of Education and Research 01KX2021)

## Credits ##
**Concept and implementation:** L. Geidel <br/>
**Web-Client:** A. Blumentritt, F.M. Moser <br/>
**Keycloak:** Peter Penndorf, R. Schuldt, F.M. Moser <br/>
**Docker:** R. Schuldt <br/>
**Bloom-Filter:** C. Hampf <br/>
**TTP-FHIR Gateway for E-PIX:** M. Bialke, F.M. Moser, S. Lang <br/>

## License ##
**License:** AGPLv3, https://www.gnu.org/licenses/agpl-3.0.en.html <br/>
**Copyright:** 2009 - 2023 University Medicine Greifswald <br/>
**Contact:** https://www.ths-greifswald.de/kontakt/

## Publications ##
- Hampf et al. 2020 "Assessment of scalability and performance of the record linkage tool E‑PIX® in managing multi‑million patients in research projects at a large university hospital in Germany", https://translational-medicine.biomedcentral.com/articles/10.1186/s12967-020-02257-4
- Gött et al. 2022 "3LGM2IHE: Requirements for data-protection-compliant research infrastructures. A systematic comparison of theory and practice-oriented implementation", http://dx.doi.org/10.1055/a-1950-2791
- http://dx.doi.org/10.3414/ME14-01-0133
- http://dx.doi.org/10.1186/s12967-015-0545-6

## Supported languages ##
German, English

# Screenshots #

Record Linkage

![context](https://raw.githubusercontent.com/mosaic-hgw/E-PIX/master/docker/standard/screenshots/E-PIX-Screenshot-Dublettenaufl%C3%B6sung.png)

Processing of Lists

![context](https://raw.githubusercontent.com/mosaic-hgw/E-PIX/master/docker/standard/screenshots/E-PIX-Screenshot-Listenverarbeitung.png)

Adding Patients

![context](https://raw.githubusercontent.com/mosaic-hgw/E-PIX/master/docker/standard/screenshots/E-PIX-Screenshot-Personen-erfassen.png)
