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

import javax.rules.InvalidRuleSessionException;
import javax.rules.ObjectFilter;
import javax.rules.StatelessRuleSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class StatelessRuleSessionHandle extends RuleSessionHandle
	implements StatelessRuleSession
{
	// Constants -------------------------------------------------------------

	/**
	 * The <code>Logger</code> instance for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(
			StatelessRuleSessionHandle.class);

	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private static boolean traceEnabled = logger.isTraceEnabled();
	
	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------
	
	/**
	 * TODO
	 * 
	 * @param mc
	 */
	public StatelessRuleSessionHandle(RuleManagedConnection mc) {
		super(mc);
	}

	// StatelessRuleSession implementation -----------------------------------

	/* (non-Javadoc)
	 * @see javax.rules.StatelessRuleSession#executeRules(java.util.List)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List executeRules(List inputs)
	throws InvalidRuleSessionException,	RemoteException {

		if (traceEnabled) {
			logger.trace("executeRules(" + inputs + ")");
		}
		
		StatelessRuleSession slrs = getRuleSession();
		List outputs = null;

		try {
			outputs = slrs.executeRules(inputs);
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
		
		if (traceEnabled) {
			logger.trace("Successfully executed rules, outputs (" + outputs + ")");
		}
		
		return Collections.unmodifiableList(outputs);
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatelessRuleSession#executeRules(java.util.List, javax.rules.ObjectFilter)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List executeRules(List inputs, ObjectFilter filter)
	throws InvalidRuleSessionException, RemoteException {
		
		if (traceEnabled) {
			logger.trace("executeRules(" + inputs + ", " + filter + ")");
		}
		
		StatelessRuleSession slrs = getRuleSession();
		List outputs = null;
		
		try {
			outputs = slrs.executeRules(inputs, filter);
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
		
		if (traceEnabled) {
			logger.trace("Successfully executed rules, outputs (" + outputs + ")");
		}
		
		return Collections.unmodifiableList(outputs);
	}

	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.resource.spi.RuleSessionHandle#getRuleSession()
	 */
	protected StatelessRuleSession getRuleSession() {
		
		if (traceEnabled) {
			logger.trace("getRuleSession()");
		}
		
		return (StatelessRuleSession)super.getRuleSession();
	}
	
	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
