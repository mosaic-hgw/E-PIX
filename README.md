![context](https://user-images.githubusercontent.com/12081369/49164561-a4481500-f32f-11e8-9f0d-fa7a730f4b9d.png)

Current Version: 2.4.1

The ID Management solution E-PIX (Enterprise Identifier Cross Referencing) applies the Fellegi-Sunter-algorithm and the Levenshtein distance to avoid duplicate participant entries. The independent software module facilitates participant management and multisite-aggregation of medical research data. Additionally, the correction of potential synonym errors is supported (i.e. false-negative record linkage).

# License
This Software was developed by the Institute for Community Medicine of the University Medicine Greifswald. It it licensed under AGPLv3 and provided by the DFG-funded MOSAIC-Project (grant number HO 1937/2-1).

# Build
To build gICS with maven use the goals "clean install".

# Docker
Use the Docker-Image to easily try out E-PIX visit https://hub.docker.com/r/tmfev/epix/

# Web-based Interface
All functionalities of the E-PIX are provided for external use via a SOAP-Interface.

[E-PIX Service Interface-Description (JavaDoc)](https://www.ths-greifswald.de/wp-content/uploads/tools/e-pix/doc/2-4-0/org/emau/icmvc/ganimed/epix/service/EPIXService.html "E-PIX Service Interface Description")

Use SOAP-UI to create sample requests. The WSDL URL is ``http://<YOUR IPADDRESS>:8080/epix/EPIXServiceBean?wsdl``

(Please modify IP Address and Port accordingly).

# More Information
Visit https://www.ths-greifswald.de/forscher/e-pix/
