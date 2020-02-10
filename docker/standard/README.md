![context](https://user-images.githubusercontent.com/12081369/49164561-a4481500-f32f-11e8-9f0d-fa7a730f4b9d.png)

Current Version: 2.9.2 (September 2019)

# Installing the standard-version of E-PIX with docker-compose #

Note: your account needs administrative privileges to use docker
change to super user (su) or run the following commands with sudo

Download files

```git clone https://github.com/mosaic-hgw/E-PIX```

grant read/write permissission to contained E-PIX sub-folders

```sudo chmod -R 777 E-PIX/docker```

change to E-PIX folder

```sudo cd E-PIX/docker/standard ```

if applicable: stop runnging mysql services on port 3306 

```sudo service mysql stop```

check docker version (required 1.13.1 or above)

```sudo docker -v```

check docker-compose version (required 1.8.0 or above)

```sudo docker-compose -v```

Note: The default publishing port of the application server is 8080. Modify if necessary in jboss/configure_wildfly_epix.cli

run docker-compose to pull and configure E-PIX

```sudo docker-compose up```

this will start pulling and configuration of mysql and jboss wildfly and automatically deployment of E-PIX in the current version.

installation process takes up to 7 minutes (depending on your internet connection) and succeeded if the following output is shown

open browser and try out the E-PIX from http://YOURIPADDRESS:8080/epix-web


finish and close E-PIX application server with CTRL+C

# Additional Information #

The E-PIX was developed by the University Medicine Greifswald  and published in 2014 as part of the [MOSAIC-Project](https://ths-greifswald.de/mosaic "")  (funded by the DFG HO 1937/2-1).

## Credits ##
Concept and implementation: L. Geidel

Web-Client: A. Blumentritt

## License ##
License: AGPLv3, https://www.gnu.org/licenses/agpl-3.0.en.html

Copyright: 2014 - 2020 University Medicine Greifswald

Contact: https://www.ths-greifswald.de/kontakt/