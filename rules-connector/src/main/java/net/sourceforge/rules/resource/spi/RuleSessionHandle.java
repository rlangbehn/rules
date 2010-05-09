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
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is the JCA implementation of a rule session.
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public abstract class RuleSessionHandle implements RuleSession, XAResource
{
	// Constants -------------------------------------------------------------

	/**
	 * The <code>Log</code> instance for this class.
	 */
	private static final Log log = LogFactory.getLog(
			RuleSessionHandle.class);

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
		
		mc.releaseHandle(this);
		
		if (log.isTraceEnabled()) {
			log.trace("Released rule session (" + this + ")");
		}
	}

	// XAResource implementation ---------------------------------------------

	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#commit(javax.transaction.xa.Xid, boolean)
	 */
	public void commit(Xid xid, boolean onePhase) throws XAException {
		getXAResource().commit(xid, onePhase);
	}

	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#end(javax.transaction.xa.Xid, int)
	 */
	public void end(Xid xid, int flags) throws XAException {
		getXAResource().end(xid, flags);
	}

	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#forget(javax.transaction.xa.Xid)
	 */
	public void forget(Xid xid) throws XAException {
		getXAResource().forget(xid);
	}

	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#getTransactionTimeout()
	 */
	public int getTransactionTimeout() throws XAException {
		return getXAResource().getTransactionTimeout();
	}

	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#isSameRM(javax.transaction.xa.XAResource)
	 */
	public boolean isSameRM(XAResource xares) throws XAException {
		
		if (xares instanceof RuleSessionHandle) {
			xares = ((RuleSessionHandle)xares).getXAResource();
		}
		
		return getXAResource().isSameRM(xares);
	}

	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#prepare(javax.transaction.xa.Xid)
	 */
	public int prepare(Xid xid) throws XAException {
		return getXAResource().prepare(xid);
	}

	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#recover(int)
	 */
	public Xid[] recover(int flag) throws XAException {
		return getXAResource().recover(flag);
	}

	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#rollback(javax.transaction.xa.Xid)
	 */
	public void rollback(Xid xid) throws XAException {
		getXAResource().rollback(xid);
	}

	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#setTransactionTimeout(int)
	 */
	public boolean setTransactionTimeout(int seconds) throws XAException {
		return getXAResource().setTransactionTimeout(seconds);
	}

	/* (non-Javadoc)
	 * @see javax.transaction.xa.XAResource#start(javax.transaction.xa.Xid, int)
	 */
	public void start(Xid xid, int flags) throws XAException {
		getXAResource().start(xid, flags);
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

	/**
	 * TODO
	 * 
	 * @return
	 * @throws XAException
	 */
	private XAResource getXAResource() throws XAException {
		
		RuleSession ruleSession = getRuleSession();

		if (ruleSession instanceof XAResource) {
			return (XAResource)ruleSession;
		} else {
			String s = "XA transactions are not supported with " + ruleSession;
			throw new XAException(s);
		}
	}

	// Inner classes ---------------------------------------------------------
}
