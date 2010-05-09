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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	 * The <code>Log</code> instance for this class.
	 */
	private static final Log log = LogFactory.getLog(
			StatelessRuleSessionHandle.class);

	// Attributes ------------------------------------------------------------

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
	public List executeRules(List objects)
	throws InvalidRuleSessionException,	RemoteException {
		
		StatelessRuleSession slrs = (StatelessRuleSession)getRuleSession();
		List outputs = slrs.executeRules(objects);
		
		if (log.isTraceEnabled()) {
			log.trace("Successfully executed rules");
		}
		
		return Collections.unmodifiableList(outputs);
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatelessRuleSession#executeRules(java.util.List, javax.rules.ObjectFilter)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List executeRules(List objects, ObjectFilter filter)
	throws InvalidRuleSessionException, RemoteException {
		
		StatelessRuleSession slrs = (StatelessRuleSession)getRuleSession();
		List outputs = slrs.executeRules(objects, filter);
		
		if (log.isTraceEnabled()) {
			log.trace("Successfully executed rules");
		}
		
		return Collections.unmodifiableList(outputs);
	}

	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
