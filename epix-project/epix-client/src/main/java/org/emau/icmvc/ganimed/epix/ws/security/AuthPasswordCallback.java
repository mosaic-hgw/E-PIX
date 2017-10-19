package org.emau.icmvc.ganimed.epix.ws.security;

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


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.emau.icmvc.ganimed.epix.client.EPIXClient;
import org.emau.icmvc.ganimed.epix.client.EPIXClientFactory;
import org.emau.icmvc.ganimed.epix.common.MPIException;
 
public class AuthPasswordCallback implements CallbackHandler
{


 private Map<String, String> passwords = new HashMap<String, String>();
 
   public AuthPasswordCallback() throws MPIException {
	  EPIXClient client = EPIXClientFactory.getInstance().createEPIXClient(null);
	  Account account = client.getAccount();
      passwords.put(account.getUser(), account.getPassword());
   }
 
   public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException
   {
      for (int i = 0; i < callbacks.length; i++)
      {    	 
         WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];
         String pass = passwords.get(pc.getIdentifier());
         if (pass != null)
         {
            pc.setPassword(pass);
            return;
         }
      }
   }
 
   public void setAliasPassword(String alias, String password)
   {
      passwords.put(alias, password);
   }
}
