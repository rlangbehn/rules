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

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class TestConnectionManager implements ConnectionManager
{
	private static final long serialVersionUID = 1L;

	/**
	 * TODO 
	 */
	public TestConnectionManager() {
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ConnectionManager#allocateConnection(javax.resource.spi.ManagedConnectionFactory, javax.resource.spi.ConnectionRequestInfo)
	 */
	public Object allocateConnection(
			ManagedConnectionFactory mcf,
			ConnectionRequestInfo cri)
	throws ResourceException {

		Principal principal = new TestPrincipal("anonymous");
		Set<Principal> principals = new HashSet<Principal>();
		principals.add(principal);
		
		Set<PasswordCredential> privCredentials = new HashSet<PasswordCredential>();
		PasswordCredential pc = new PasswordCredential(principal.getName(), new char[0]);
		pc.setManagedConnectionFactory(mcf);
		privCredentials.add(pc);
		
		Subject subject = new Subject(
				true,
				principals,
				Collections.EMPTY_SET,
				privCredentials
		);
		
		ManagedConnection mc = mcf.createManagedConnection(subject, cri);
		return mc.getConnection(subject, cri);
	}
}
