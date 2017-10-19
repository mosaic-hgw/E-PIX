package org.emau.icmvc.ganimed.epix.client;

/*
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2017 The MOSAIC Project - Institut fuer Community Medicine der
 * 							Universitaetsmedizin Greifswald - mosaic-projekt@uni-greifswald.de
 * 							concept and implementation
 * 							c. schack, d.langner, l. geidel
 * 							web client
 * 							a. blumentritt
 * 							g. weiher
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
 * __
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ###license-information-end###
 */

import javax.xml.ws.BindingProvider;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.service.EPIXService;
import org.emau.icmvc.ganimed.epix.ws.security.Account;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EPIXClientFactory {

	private volatile static EPIXClientFactory factory = null;
	private static final Object factoryCreationLock = new Object();

	private EPIXClient client = null;

	private final Logger logger = Logger.getLogger(EPIXClientFactory.class);

	private EPIXClientFactory() {
	}

	/**
	 * The path to the Spring <code>configFile</code> must be available relativ
	 * to classpath.
	 * 
	 * @param configFile
	 * @return PIXManClientFactory as Singleton
	 */
	public static EPIXClientFactory getInstance() {
		synchronized (factoryCreationLock) {
			if (factory == null) {
				factory = new EPIXClientFactory();
			}
			return factory;
		}
	}

	/**
	 * The method returns a PIXManClient object.
	 * 
	 * @return
	 */
	public synchronized EPIXClient createEPIXClient(String configFile) throws MPIException {
		try {
			if (client == null) {
				String value = "" + configFile;
				ApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { value });
				BeanFactory factory = appContext;
				IdentifierDomain identifierDomain = (IdentifierDomain) factory.getBean("identifierDomain");
				Account account = (Account) factory.getBean("account");
				EPIXService epixService = (EPIXService) factory.getBean("client");

				// XmlBeanFactory beanFactory = new XmlBeanFactory(new
				// ClassPathResource(configFile));
				// IdentifierDomain identifierDomain =
				// (IdentifierDomain)beanFactory.getBean("identifierDomain");
				// EPIXService epixService =
				// (EPIXService)beanFactory.getBean("client");
				// Account account = (Account) beanFactory.getBean("account");

				Client clientProxy = ClientProxy.getClient(epixService);
				if (clientProxy != null) {
					HTTPConduit conduit = (HTTPConduit) clientProxy.getConduit();
					HTTPClientPolicy policy = new HTTPClientPolicy();

					policy.setConnectionTimeout(120000);
					policy.setReceiveTimeout(120000);
					policy.setAllowChunking(false);
					conduit.setClient(policy);
				}
				// FIXME Until Server side Authenification Interceptor is
				// implemented

				((BindingProvider) epixService).getRequestContext().put(BindingProvider.USERNAME_PROPERTY,
						account.getUser());
				((BindingProvider) epixService).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,
						account.getPassword());
				client = new EPIXClient(epixService, identifierDomain, account);
				logger.info("EPIXClient successfully initialized");
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new MPIException(ErrorCode.EPIXCLIENT_INSTANTIATION_ERROR, e);
		}
		return client;
	}
}
