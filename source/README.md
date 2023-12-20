${ttp.epix.readme.header}

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

${ttp.epix.readme.footer}

# Screenshots #

Record Linkage

![context](https://raw.githubusercontent.com/mosaic-hgw/E-PIX/master/docker/standard/screenshots/E-PIX-Screenshot-Dublettenaufl%C3%B6sung.png)

Processing of Lists

![context](https://raw.githubusercontent.com/mosaic-hgw/E-PIX/master/docker/standard/screenshots/E-PIX-Screenshot-Listenverarbeitung.png)

Adding Patients

![context](https://raw.githubusercontent.com/mosaic-hgw/E-PIX/master/docker/standard/screenshots/E-PIX-Screenshot-Personen-erfassen.png)
