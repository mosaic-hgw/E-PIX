ARG TAG=22.0.1.Final-20210309
FROM mosaicgreifswald/wildfly:${TAG}

# ###license-information-start###
# E-PIX - Enterprise Patient Identifier Cross-referencing
# __
# Copyright (C) 2013 - 2021 Independent Trusted Third Party of the University Medicine Greifswald
# 							kontakt-ths@uni-greifswald.de
# 							concept and implementation
# 							c.schack, l.geidel, d.langner, g.koetzschke
# 							web client
# 							a.blumentritt
# 							docker
# 							r.schuldt
# 							please cite our publications
# 							http://dx.doi.org/10.3414/ME14-01-0133
# 							http://dx.doi.org/10.1186/s12967-015-0545-6
# __
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program. If not, see <http://www.gnu.org/licenses/>.
# ###license-information-end###

# default environment variables
ENV EPIX_VERSION     @@EPIX_VERSION@@
ENV EPIX_AUTH_MODE   none

ENV EPIX_DB_HOST     mysql
ENV EPIX_DB_PORT     3306
ENV EPIX_DB_NAME     epix
ENV EPIX_DB_USER     epix_user
ENV EPIX_DB_PASS     epix_password

ENV GRAS_DB_HOST     mysql
ENV GRAS_DB_PORT     3306
ENV GRAS_DB_NAME     gras
ENV GRAS_DB_USER     gras_user
ENV GRAS_DB_PASS     gras_password
	
# annotations
LABEL maintainer                           = "ronny.schuldt@uni-greifswald.de" \
      org.opencontainers.image.authors     = "university-medicine greifswald" \
      org.opencontainers.image.source      = "https://github.com/mosaic-hgw/E-PIX" \
      org.opencontainers.image.vendor      = "uni-greifswald.de" \
      org.opencontainers.image.title       = "E-PIX" \
      org.opencontainers.image.version     = "@@EPIX_VERSION@@" \
      org.opencontainers.image.description = "E-PIX - Enterprise Patient Identifier Cross-referencing" \
      org.opencontainers.image.license     = "AGPLv3"

# copy files
COPY ["compose/deployments/epix-${EPIX_VERSION}.ear",     "${ENTRY_DEPLOYMENTS}/"]
COPY ["compose/deployments/epix-web-${EPIX_VERSION}.war", "${ENTRY_DEPLOYMENTS}/"]
COPY ["compose/jboss/configure_wildfly_epix.cli",         "${ENTRY_JBOSS_BATCH}/"]
COPY ["compose/jboss/configure_wildfly_gras.cli",         "${ENTRY_JBOSS_BATCH}/"]
COPY ["compose/jboss/gras_jboss-web.xml",                 "${ENTRY_JBOSS_BATCH}/"]
COPY ["compose/jboss/gras_web.xml",                       "${ENTRY_JBOSS_BATCH}/"]
COPY ["compose/jboss/keycloak_web.xml",                   "${ENTRY_JBOSS_BATCH}/"]

# manipulate cli for env-injections
RUN cd ${ENTRY_JBOSS_BATCH}/ &&\
	sed -i '1s/^/set _host=\${EPIX_DB_HOST}\nset _port=\${EPIX_DB_PORT}\nset _name=\${EPIX_DB_NAME}\nset _user=\${EPIX_DB_USER}\nset _pass=\${EPIX_DB_PASS}\n\n/' configure_wildfly_epix.cli &&\
	sed -i -r 's/(--connection-url=jdbc:mysql:\/\/)(mysql:3306\/epix)(\?.+)/\1\$_host:\$_port\/\$_name\3/' configure_wildfly_epix.cli &&\
	sed -i -r 's/(--user-name=)([^ ]+)/\1\$_user/;s/(--password=)([^ ]+)/\1\$_pass/' configure_wildfly_epix.cli &&\
	\
	sed -i '1s/^/set _host=\${GRAS_DB_HOST}\nset _port=\${GRAS_DB_PORT}\nset _name=\${GRAS_DB_NAME}\nset _user=\${GRAS_DB_USER}\nset _pass=\${GRAS_DB_PASS}\n\n/' configure_wildfly_gras.cli &&\
	sed -i -r 's/(--connection-url=jdbc:mysql:\/\/)(mysql:3306\/gras)(\?.+)/\1\$_host:\$_port\/\$_name\3/' configure_wildfly_gras.cli &&\
	sed -i -r 's/(--user-name=)([^ ]+)/\1\$_user/;s/(--password=)([^ ]+)/\1\$_pass/' configure_wildfly_gras.cli
