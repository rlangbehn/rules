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

import java.security.AccessControlContext;
import java.security.AccessController;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class implements the default connection manager for non-managed
 * application scenarios. No quality of services (QoS) will be provided.
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RuleConnectionManager implements ConnectionManager
{
	// Constants -------------------------------------------------------------

	/**
	 * The <code>Log</code> instance for this class.
	 */
	private static final Log log = LogFactory.getLog(
			RuleConnectionManager.class);

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// ConnectionManager implementation --------------------------------------

	/* (non-Javadoc)
	 * @see javax.resource.spi.ConnectionManager#allocateConnection(javax.resource.spi.ManagedConnectionFactory, javax.resource.spi.ConnectionRequestInfo)
	 */
	public Object allocateConnection(
			ManagedConnectionFactory mcf,
			ConnectionRequestInfo cri)
	throws ResourceException {

		boolean traceEnabled = log.isTraceEnabled();
		
		if (traceEnabled) {
			StringBuilder sb = new StringBuilder("Allocating connection using");
			sb.append("\n\tManaged connection factory: ").append(mcf);
			sb.append("\n\tConnection request info:    ").append(cri);
			log.trace(sb.toString());
		}
		
		Subject subject = getSubject();
		
		if (traceEnabled) {
			log.trace("Using subject (" + subject + ")");
		}
		
		ManagedConnection mc = mcf.createManagedConnection(subject, cri);
		
		if (traceEnabled) {
			log.trace("Using managed connection (" + mc + ")");
		}
		
		Object connection = mc.getConnection(subject, cri);

		if (traceEnabled) {
			log.trace("Allocated connection (" + connection + ")");
		}
		
		return connection;
	}

	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @return
	 */
	private Subject getSubject() {
		
		// obtain the identity of the already-authenticated
		// subject from access control context
		AccessControlContext acc = AccessController.getContext();
		Subject subject = Subject.getSubject(acc);
		
		return subject;
	}
	
	// Inner classes ---------------------------------------------------------
}
