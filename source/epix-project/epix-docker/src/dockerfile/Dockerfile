FROM ${docker.baseImage}

# ###license-information-start###
# E-PIX - Enterprise Patient Identifier Cross-referencing
# __
# Copyright (C) 2013 - ${build.year} Independent Trusted Third Party of the University Medicine Greifswald
# 							kontakt-ths@uni-greifswald.de
#
# 							concept and implementation
# 							c.schack, l.geidel, d.langner, g.koetzschke
# 							web client
# 							a.blumentritt
# 							docker
# 							r.schuldt
#
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

# tool-defaults
ENV TTP_${project.upperName}_VERSION        ${project.version}
ENV TTP_${project.upperName}_WEB_AUTH_MODE  none

# mysql-defaults
ENV TTP_${project.upperName}_DB_HOST        mysql
ENV TTP_${project.upperName}_DB_PORT        3306
ENV TTP_${project.upperName}_DB_NAME        ${project.shortName}
ENV TTP_${project.upperName}_DB_USER        ${project.shortName}_user
ENV TTP_${project.upperName}_DB_PASS        ${project.shortName}_password

# gras-defaults
ENV TTP_GRAS_DB_HOST        mysql
ENV TTP_GRAS_DB_PORT        3306
ENV TTP_GRAS_DB_NAME        gras
ENV TTP_GRAS_DB_USER        gras_user
ENV TTP_GRAS_DB_PASS        gras_password

# keycloak-defaults
ENV TTP_KEYCLOAK_SSL_REQUIRED               all
ENV TTP_KEYCLOAK_USE_RESOURCE_ROLE_MAPPINGS false
ENV TTP_KEYCLOAK_CONFIDENTIAL_PORT          8443

# annotations
LABEL maintainer                           = "ronny.schuldt@uni-greifswald.de" \
      org.opencontainers.image.authors     = "university-medicine greifswald" \
      org.opencontainers.image.source      = "https://github.com/mosaic-hgw/${project.parent.name}" \
      org.opencontainers.image.vendor      = "uni-greifswald.de" \
      org.opencontainers.image.title       = "${project.parent.name}" \
      org.opencontainers.image.version     = "${project.version}" \
      org.opencontainers.image.description = "E-PIX - Enterprise Patient Identifier Cross-referencing" \
      org.opencontainers.image.license     = "AGPLv3"

# copy files
COPY ["deployments/*ar",    "${ENTRY_WILDFLY_DEPLOYS}/"]
COPY ["jboss/*",            "${ENTRY_WILDFLY_CLI}/"]
COPY ["LICENSE.txt",        "./"]

USER root
RUN chown -R ${USER}:${USER} ${ENTRY_WILDFLY_DEPLOYS} ${ENTRY_WILDFLY_CLI} LICENSE.txt
USER ${USER}
