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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the JCA implementation of a rule session.
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public abstract class RuleSessionHandle implements RuleSession
{
	// Constants -------------------------------------------------------------

	/**
	 * The <code>Logger</code> instance for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(
			RuleSessionHandle.class);

	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private static boolean traceEnabled = logger.isTraceEnabled();
	
	/**
	 * The <code>RuleManagedConnection</code> instance we're associated with.
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

		if (traceEnabled) {
			logger.trace("getRuleExecutionSetMetadata()");
		}
		
		try {
			return getRuleSession().getRuleExecutionSetMetadata();
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see javax.rules.RuleSession#getType()
	 */
	public int getType()
	throws InvalidRuleSessionException, RemoteException {

		if(traceEnabled) {
			logger.trace("getType()");
		}
		
		try {
			return getRuleSession().getType();
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see javax.rules.RuleSession#release()
	 */
	public void release()
	throws InvalidRuleSessionException, RemoteException {
		
		if (traceEnabled) {
			logger.trace("release()");
		}
		
		getManagedConnection().releaseHandle(this);
		
		if (traceEnabled) {
			logger.trace("Released rule session (" + this + ")");
		}
	}

	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	/**
	 * @return the associated managed connection
	 */
	RuleManagedConnection getManagedConnection() {
		
		if (traceEnabled) {
			logger.trace("getManagedConnection()");
		}
		
		return mc;
	}

	/**
	 * @param mc the mc to set
	 */
	void setManagedConnection(RuleManagedConnection mc) {
		
		if (traceEnabled) {
			logger.trace("setManagedConnection(" + mc + ")");
		}
		
		this.mc = mc;
	}

	// Protected -------------------------------------------------------------

	/**
	 * TODO
	 *
	 * @return
	 */
	protected RuleSession getRuleSession() {
		
		if (traceEnabled) {
			logger.trace("getRuleSession()");
		}
		
		return mc.getRuleSession(this);
	}
	
	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
