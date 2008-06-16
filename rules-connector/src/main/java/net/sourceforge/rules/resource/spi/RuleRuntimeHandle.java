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
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.Referenceable;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.RuleSession;
import javax.rules.RuleSessionCreateException;
import javax.rules.RuleSessionTypeUnsupportedException;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RuleRuntimeHandle implements RuleRuntime, Referenceable
{
	// Constants -------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private ConnectionManager cm;
	
	/**
	 * TODO
	 */
	private RuleManagedConnectionFactory mcf;

	/**
	 * TODO
	 */
	private Reference reference;
	
	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------
	
	/**
	 * TODO
	 *
	 * @param mcf
	 * @param cm
	 */
	public RuleRuntimeHandle(
			RuleManagedConnectionFactory mcf,
			ConnectionManager cm) {
		this.mcf = mcf;
		this.cm = cm;
	}

	// Referenceable implementation ------------------------------------------

	/* (non-Javadoc)
	 * @see javax.resource.Referenceable#setReference(javax.naming.Reference)
	 */
	public void setReference(Reference reference) {
		this.reference = reference;
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

		RuleConnectionRequestInfo cri = new RuleConnectionRequestInfo(
				bindUri,
				properties,
				ruleSessionType
		);
		
		return createRuleSession(cri);
	}

	/* (non-Javadoc)
	 * @see javax.rules.RuleRuntime#getRegistrations()
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List getRegistrations() throws RemoteException {
		RuleRuntime ruleRuntime = mcf.getRuleRuntime();
		return ruleRuntime.getRegistrations();
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

	// Inner classes ---------------------------------------------------------
}
