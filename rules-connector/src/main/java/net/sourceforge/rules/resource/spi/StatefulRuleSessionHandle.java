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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * The <code>Logger</code> instance for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(
			StatefulRuleSessionHandle.class);

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

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
	public StatefulRuleSessionHandle(RuleManagedConnection mc) {
		super(mc);
	}

	// StatefulRuleSession implementation ------------------------------------

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#addObject(java.lang.Object)
	 */
	public Handle addObject(Object object)
	throws RemoteException, InvalidRuleSessionException {

		if (traceEnabled) {
			logger.trace("addObject(" + object + ")");
		}
		
		StatefulRuleSession sfrs = getRuleSession();
		Handle handle = null;
		
		try {
			handle = sfrs.addObject(object);
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
		
		if (traceEnabled) {
			logger.trace("Added object (" + handle + ")");
		}
		
		return handle;
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#addObjects(java.util.List)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List addObjects(List objectList)
	throws RemoteException, InvalidRuleSessionException {

		if (traceEnabled) {
			logger.trace("addObjects(" + objectList + ")");
		}
		
		StatefulRuleSession sfrs = getRuleSession();
		
		try {
			return sfrs.addObjects(objectList);
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#containsObject(javax.rules.Handle)
	 */
	public boolean containsObject(Handle handle)
	throws RemoteException, InvalidRuleSessionException, InvalidHandleException {

		if (traceEnabled) {
			logger.trace("containsObject(" + handle + ")");
		}
		
		StatefulRuleSession sfrs = getRuleSession();
		
		try {
			return sfrs.containsObject(handle);
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#executeRules()
	 */
	public void executeRules()
	throws RemoteException,	InvalidRuleSessionException {
		
		if (traceEnabled) {
			logger.trace("executeRules()");
		}
		
		StatefulRuleSession sfrs = getRuleSession();
		
		try {
			sfrs.executeRules();
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
		
		if (traceEnabled) {
			logger.trace("Successfully executed rules");
		}
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#getHandles()
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List getHandles()
	throws RemoteException, InvalidRuleSessionException {

		if (traceEnabled) {
			logger.trace("getHandles()");
		}
		
		StatefulRuleSession sfrs = getRuleSession();
		
		try {
			return sfrs.getHandles();
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#getObject(javax.rules.Handle)
	 */
	public Object getObject(Handle handle)
	throws RemoteException, InvalidHandleException, InvalidRuleSessionException {

		if (traceEnabled) {
			logger.trace("getObject(" + handle + ")");
		}
		
		StatefulRuleSession sfrs = getRuleSession();
		
		try {
			return sfrs.getObject(handle);
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#getObjects()
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List getObjects()
	throws RemoteException, InvalidRuleSessionException {

		if (traceEnabled) {
			logger.trace("getObjects()");
		}
		
		StatefulRuleSession sfrs = getRuleSession();
		
		try {
			return sfrs.getObjects();
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#getObjects(javax.rules.ObjectFilter)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List getObjects(ObjectFilter filter)
	throws RemoteException, InvalidRuleSessionException {

		if (traceEnabled) {
			logger.trace("getObjects(" + filter + ")");
		}
		
		StatefulRuleSession sfrs = getRuleSession();
		
		try {
			return sfrs.getObjects(filter);
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#removeObject(javax.rules.Handle)
	 */
	public void removeObject(Handle handle)
	throws RemoteException, InvalidHandleException, InvalidRuleSessionException {

		if (traceEnabled) {
			logger.trace("removeObject(" + handle + ")");
		}
		
		StatefulRuleSession sfrs = getRuleSession();
		
		try {
			sfrs.removeObject(handle);
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#reset()
	 */
	public void reset()
	throws RemoteException, InvalidRuleSessionException {

		if (traceEnabled) {
			logger.trace("reset()");
		}
		
		StatefulRuleSession sfrs = getRuleSession();
		
		try {
			sfrs.reset();
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see javax.rules.StatefulRuleSession#updateObject(javax.rules.Handle, java.lang.Object)
	 */
	public void updateObject(Handle handle, Object object)
	throws RemoteException, InvalidRuleSessionException, InvalidHandleException {

		if (traceEnabled) {
			logger.trace("updateObject(" + handle + ", " + object + ")");
		}
		StatefulRuleSession sfrs = getRuleSession();
		
		try {
			sfrs.updateObject(handle, object);
		} catch (InvalidRuleSessionException e) {
			getManagedConnection().fireConnectionErrorOccurred(this, e);
			throw e;
		}
	}

	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.resource.spi.RuleSessionHandle#getRuleSession()
	 */
	protected StatefulRuleSession getRuleSession() {
		
		if (traceEnabled) {
			logger.trace("getRuleSession()");
		}
		
		return (StatefulRuleSession)super.getRuleSession();
	}
	
	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
