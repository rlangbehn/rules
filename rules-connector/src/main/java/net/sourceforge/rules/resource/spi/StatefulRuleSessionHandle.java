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

import javax.rules.Handle;
import javax.rules.InvalidHandleException;
import javax.rules.InvalidRuleSessionException;
import javax.rules.ObjectFilter;
import javax.rules.StatefulRuleSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class StatefulRuleSessionHandle extends RuleSessionHandle
	implements StatefulRuleSession
{
	// Constants -------------------------------------------------------------

	/**
	 * The <code>Log</code> instance for this class.
	 */
	private static final Log log = LogFactory.getLog(
			StatefulRuleSessionHandle.class);

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------
	
	/**
	 * TODO
	 * 
	 * @param mc
	 */
	public StatefulRuleSessionHandle(RuleManagedConnection mc) {
		super(mc);
	}

	// StatefulRuleSession implementation ------------------------------------

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#addObject(java.lang.Object)
	 */
	public Handle addObject(Object object)
	throws RemoteException, InvalidRuleSessionException {
		
		StatefulRuleSession sfrs = (StatefulRuleSession)getRuleSession();
		Handle handle = sfrs.addObject(object);
		
		if (log.isTraceEnabled()) {
			log.trace("Added object (" + handle + ")");
		}
		
		return handle;
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#addObjects(java.util.List)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List addObjects(List objectList)
	throws RemoteException, InvalidRuleSessionException {
		
		StatefulRuleSession sfrs = (StatefulRuleSession)getRuleSession();
		return sfrs.addObjects(objectList);
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#containsObject(javax.rules.Handle)
	 */
	public boolean containsObject(Handle handle)
	throws RemoteException, InvalidRuleSessionException, InvalidHandleException {
		
		StatefulRuleSession sfrs = (StatefulRuleSession)getRuleSession();
		return sfrs.containsObject(handle);
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#executeRules()
	 */
	public void executeRules()
	throws RemoteException,	InvalidRuleSessionException {
		
		StatefulRuleSession sfrs = (StatefulRuleSession)getRuleSession();
		sfrs.executeRules();
		
		if (log.isTraceEnabled()) {
			log.trace("Successfully executed rules");
		}
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#getHandles()
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List getHandles()
	throws RemoteException, InvalidRuleSessionException {
		
		StatefulRuleSession sfrs = (StatefulRuleSession)getRuleSession();
		return sfrs.getHandles();
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#getObject(javax.rules.Handle)
	 */
	public Object getObject(Handle handle)
	throws RemoteException, InvalidHandleException, InvalidRuleSessionException {
		
		StatefulRuleSession sfrs = (StatefulRuleSession)getRuleSession();
		return sfrs.getObject(handle);
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#getObjects()
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List getObjects()
	throws RemoteException, InvalidRuleSessionException {
		
		StatefulRuleSession sfrs = (StatefulRuleSession)getRuleSession();
		return sfrs.getObjects();
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#getObjects(javax.rules.ObjectFilter)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List getObjects(ObjectFilter filter)
	throws RemoteException, InvalidRuleSessionException {
		
		StatefulRuleSession sfrs = (StatefulRuleSession)getRuleSession();
		return sfrs.getObjects(filter);
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#removeObject(javax.rules.Handle)
	 */
	public void removeObject(Handle handle)
	throws RemoteException, InvalidHandleException, InvalidRuleSessionException {
		
		StatefulRuleSession sfrs = (StatefulRuleSession)getRuleSession();
		sfrs.removeObject(handle);
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#reset()
	 */
	public void reset() throws RemoteException, InvalidRuleSessionException {
		StatefulRuleSession sfrs = (StatefulRuleSession)getRuleSession();
		sfrs.reset();
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#updateObject(javax.rules.Handle, java.lang.Object)
	 */
	public void updateObject(Handle handle, Object object)
	throws RemoteException, InvalidRuleSessionException, InvalidHandleException {
		
		StatefulRuleSession sfrs = (StatefulRuleSession)getRuleSession();
		sfrs.updateObject(handle, object);
	}

	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
