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

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.Referenceable;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.rules.ConfigurationException;
import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProviderManager;
import javax.rules.RuleSession;
import javax.rules.RuleSessionCreateException;
import javax.rules.RuleSessionTypeUnsupportedException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RuleRuntimeHandle implements RuleRuntime, Referenceable
{
	// Constants -------------------------------------------------------------

	/**
	 * The <code>Log</code> instance for this class.
	 */
	private static final Log log = LogFactory.getLog(
			RuleRuntimeHandle.class);

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	// Attributes ------------------------------------------------------------

	/**
	 * The <code>ConnectionManager</code> instance we're associated with.
	 */
	private ConnectionManager cm;
	
	/**
	 * The <code>ManagedConnectionFactory</code> instance we're associated with.
	 */
	private RuleManagedConnectionFactory mcf;

	/**
	 * TODO
	 */
	private Reference reference;
	
	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------
	
	/**
	 * Creates a new <code>RuleRuntimeHandle</code> instance.
	 *
	 * @param mcf
	 * @param cm
	 * @throws ResourceException 
	 */
	public RuleRuntimeHandle(
			RuleManagedConnectionFactory mcf,
			ConnectionManager cm)
	throws ResourceException {
		
		this.mcf = mcf;
		this.cm = cm;
		
		if (cm == null) {
			this.cm = new RuleConnectionManager();
			
			if (log.isTraceEnabled()) {
				log.trace("Created connection manager (" + this.cm + ")");
			}
		}
		
		registerRuleServiceProvider();
	}

	// Referenceable implementation ------------------------------------------

	/* (non-Javadoc)
	 * @see javax.resource.Referenceable#setReference(javax.naming.Reference)
	 */
	public void setReference(Reference reference) {
		this.reference = reference;
		
		if (log.isTraceEnabled()) {
			log.trace("Using reference (" + reference + ")");
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.Referenceable#getReference()
	 */
	public Reference getReference() throws NamingException {
		return reference;
	}

	// RuleRuntime implementation --------------------------------------------

	/* (non-Javadoc)
	 * @see javax.rules.RuleRuntime#createRuleSession(java.lang.String, java.util.Map, int)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public RuleSession createRuleSession(
			String bindUri,
			Map properties,
			int ruleSessionType)
	throws RuleSessionTypeUnsupportedException,
		   RuleSessionCreateException,
		   RuleExecutionSetNotFoundException,
		   RemoteException {

		boolean traceEnabled = log.isTraceEnabled();

		if (traceEnabled) {
			log.trace("Creating rule session");
		}
		
		RuleConnectionRequestInfo cri = new RuleConnectionRequestInfo(
				bindUri,
				properties,
				ruleSessionType,
				mcf.getUserName(),
				(mcf.getPassword() == null) ? null : mcf.getPassword().toCharArray()
		);

		if (traceEnabled) {
			log.trace("Created connection request info (" + cri + ")");
		}
		
		RuleSession ruleSession = createRuleSession(cri);

		if (traceEnabled) {
			log.trace("Created rule session (" + ruleSession + ")");
		}
		
		return ruleSession;
	}

	/* (non-Javadoc)
	 * @see javax.rules.RuleRuntime#getRegistrations()
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List getRegistrations() throws RemoteException {

		String ruleServiceProviderUri = mcf.getRuleServiceProviderUri();
		RuleRuntime ruleRuntime = null;
		List registrations = null;
		
		try {
			ruleRuntime = JSR94Util.getRuleRuntime(ruleServiceProviderUri);
			registrations = ruleRuntime.getRegistrations();
		} catch (ConfigurationException e) {
			String s = "Error while retrieving rule runtime";
			throw new RemoteException(s, e);
		}
		
		if (log.isTraceEnabled()) {
			log.trace("Registered rule execution sets (" + registrations + ")");
		}
		
		return Collections.unmodifiableList(registrations);
	}

	// Public ----------------------------------------------------------------
	
	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	/**
	 * TODO
	 *
	 * @param cri
	 * @return
	 * @throws RuleSessionTypeUnsupportedException 
	 * @throws RuleSessionCreateException 
	 * @throws RuleExecutionSetNotFoundException 
	 * @throws RemoteException 
	 */
	private RuleSession createRuleSession(RuleConnectionRequestInfo cri)
	throws RuleSessionTypeUnsupportedException,
	       RuleSessionCreateException,
	       RuleExecutionSetNotFoundException,
	       RemoteException {
		
		try {
			return (RuleSession)cm.allocateConnection(mcf, cri);
		} catch (ResourceException e) {
			Throwable cause = e.getCause();
			
			if (cause instanceof RuleSessionTypeUnsupportedException) {
				throw (RuleSessionTypeUnsupportedException)cause;
			} else if (cause instanceof RuleSessionCreateException) {
				throw (RuleSessionCreateException)cause;
			} else if (cause instanceof RuleExecutionSetNotFoundException) {
				throw (RuleExecutionSetNotFoundException)cause;
			} else if (cause instanceof RemoteException) {
				throw (RemoteException)cause;
			} else if (cause != null) {
				throw new RuleSessionCreateException(cause.getMessage());
			} else {
				String s = Messages.getError("RuleRuntimeHandle.2"); //$NON-NLS-1$
				throw new RuleSessionCreateException(s, e);
			}
		}
	}

	/**
	 * TODO
	 * 
	 * @throws ResourceException 
	 */
	private void registerRuleServiceProvider() throws ResourceException {

		String className = mcf.getRuleServiceProviderClassName();
		
		if ((className == null) || (className.trim().length() == 0)) {
			String s = Messages.getError("RuleManagedConnectionFactory.9", "ruleServiceProviderClassName"); //$NON-NLS-1$
			throw new ResourceException(s);
		}

		String uri = mcf.getRuleServiceProviderUri();
		
		if ((uri == null) || (uri.trim().length() == 0)) {
			String s = Messages.getError("RuleManagedConnectionFactory.10", "ruleServiceProviderUri"); //$NON-NLS-1$
			throw new ResourceException(s);
		}

		if (log.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Registering RuleServiceProvider");
			sb.append("\n\tClassName: ").append(className);
			sb.append("\n\tUri:       ").append(uri);
			log.trace(sb.toString());
		}

		ClassLoader cL = Thread.currentThread().getContextClassLoader();
		Class<?> clazz;

		try {
			clazz = cL.loadClass(className);
		} catch (ClassNotFoundException e) {
			String s = Messages.getError("RuleManagedConnectionFactory.14"); //$NON-NLS-1$
			throw new ResourceException(s, e);
		}

		try {
			RuleServiceProviderManager.registerRuleServiceProvider(uri, clazz, cL);
		} catch (ConfigurationException e) {
			String s = Messages.getError("RuleManagedConnectionFactory.15"); //$NON-NLS-1$
			throw new ResourceException(s, e);
		}
	}

	// Inner classes ---------------------------------------------------------
}
