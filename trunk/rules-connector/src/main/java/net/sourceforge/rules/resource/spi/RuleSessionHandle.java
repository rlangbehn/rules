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

import javax.rules.InvalidRuleSessionException;
import javax.rules.RuleExecutionSetMetadata;
import javax.rules.RuleSession;

/**
 * This class is the JCA implementation of a rule session.
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public abstract class RuleSessionHandle implements RuleSession
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private RuleManagedConnection mc;
	
	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------
	
	/**
	 * TODO
	 * 
	 * @param mc
	 */
	public RuleSessionHandle(RuleManagedConnection mc) {
		this.mc = mc;
	}

	// RuleSession implementation --------------------------------------------

	/* (non-Javadoc)
	 * @see javax.rules.RuleSession#getRuleExecutionSetMetadata()
	 */
	public RuleExecutionSetMetadata getRuleExecutionSetMetadata()
	throws InvalidRuleSessionException, RemoteException {
		return getRuleSession().getRuleExecutionSetMetadata();
	}

	/* (non-Javadoc)
	 * @see javax.rules.RuleSession#getType()
	 */
	public int getType()
	throws InvalidRuleSessionException, RemoteException {
		return getRuleSession().getType();
	}

	/* (non-Javadoc)
	 * @see javax.rules.RuleSession#release()
	 */
	public void release()
	throws InvalidRuleSessionException, RemoteException {
		getRuleSession().release();
	}

	// Public ----------------------------------------------------------------

	/**
	 * @return the associated managed connection
	 */
	public RuleManagedConnection getManagedConnection() {
		return mc;
	}

	/**
	 * @param mc the mc to set
	 */
	public void setManagedConnection(RuleManagedConnection mc) {
		this.mc = mc;
	}

	/**
	 * TODO
	 *
	 * @return
	 */
	public RuleSession getRuleSession() {
		return mc.getRuleSession(this);
	}
	
	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
