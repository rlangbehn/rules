/*****************************************************************************
 * $Id$
 *
 * Copyright 2008, The Rules Framework Development Team, and individual
 * contributors as indicated by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ****************************************************************************/
package net.sourceforge.rules.resource.spi;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;

import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public final class SecurityUtil
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param mcf
	 * @param subject
	 * @return
	 */
	public static PasswordCredential getPasswordCredential(
    		final ManagedConnectionFactory mcf,
    		final Subject subject) {
		
    	return AccessController.doPrivileged(
    			new PrivilegedAction<PasswordCredential>() {
    				public PasswordCredential run() {
    					
    					Set<PasswordCredential> pcs = subject.getPrivateCredentials(
    							PasswordCredential.class
    					);
    					
    					for (PasswordCredential pc : pcs) {
    						if (mcf.equals(pc.getManagedConnectionFactory())) {
    							return pc;
    						}
    					}
    					
    			    	return null;
    				}
    			}
    	);
	}
	
	// Constructors ----------------------------------------------------------

	/**
	 * Private default ctor to prevent instantiation. 
	 */
	private SecurityUtil() {
	}
	
	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
