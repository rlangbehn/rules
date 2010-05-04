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

import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.rules.InvalidRuleSessionException;
import javax.rules.RuleRuntime;
import javax.rules.RuleSession;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

/**
 * Implements the JCA ManagedConnection contract.
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RuleManagedConnection implements ManagedConnection
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private final RuleConnectionRequestInfo cri;

	/**
	 * TODO
	 */
	private final LinkedList<RuleSessionHandle> handles;
	
	/**
	 * TODO 
	 */
	private final LinkedList<ConnectionEventListener> listeners;
	
	/**
	 * TODO 
	 */
	private PrintWriter logWriter;
	
	/**
	 * TODO 
	 */
	private final RuleManagedConnectionFactory mcf;
	
	/**
	 * TODO 
	 */
	private final RuleSession ruleSession;
	
	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	/**
	 * TODO
	 *
	 * @param mcf
	 * @param cri
	 * @param ruleSession
	 */
	public RuleManagedConnection(
			RuleManagedConnectionFactory mcf,
			RuleConnectionRequestInfo cri,
			RuleSession ruleSession) {
		
		this.mcf = mcf;
		this.cri = cri;
		this.ruleSession = ruleSession;
		
		listeners = new LinkedList<ConnectionEventListener>();
		handles = new LinkedList<RuleSessionHandle>();
	}

	// ManagedConnection implementation --------------------------------------

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#addConnectionEventListener(javax.resource.spi.ConnectionEventListener)
	 */
	public void addConnectionEventListener(ConnectionEventListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#associateConnection(java.lang.Object)
	 */
	public void associateConnection(Object connection)
	throws ResourceException {
		
        RuleSessionHandle handle = (RuleSessionHandle)connection;
        
        if (handle.getManagedConnection() != this) {
            handle.getManagedConnection().removeHandle(handle);
            handle.setManagedConnection(this);
            addHandle(handle);
        }
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#cleanup()
	 */
	public void cleanup() throws ResourceException {
		synchronized (handles) {
			handles.clear();
		}
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#destroy()
	 */
	public void destroy() throws ResourceException {
		cleanup();
		
		try {
			ruleSession.release();
		} catch (InvalidRuleSessionException e) {
			String s = Messages.getError("RuleManagedConnection.0"); //$NON-NLS-1$
			throw new ResourceException(s, e);
		} catch (RemoteException e) {
			String s = Messages.getError("RuleManagedConnection.1"); //$NON-NLS-1$
			throw new ResourceException(s, e);
		}
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#getConnection(javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
	 */
	public Object getConnection(Subject subject, ConnectionRequestInfo cri)
	throws ResourceException {
		
		int ruleSessionType = ((RuleConnectionRequestInfo)cri).getRuleSessionType();
		RuleSessionHandle handle = null;
		
		if (ruleSessionType == RuleRuntime.STATEFUL_SESSION_TYPE) {
			handle = new StatefulRuleSessionHandle(this);
		} else if (ruleSessionType == RuleRuntime.STATELESS_SESSION_TYPE) {
			handle = new StatelessRuleSessionHandle(this);
		} else {
			String s = Messages.getError("RuleManagedConnection.2", ruleSessionType); //$NON-NLS-1$
			throw new IllegalStateException(s);
		}
		
		addHandle(handle);
		return handle;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#getLocalTransaction()
	 */
	public LocalTransaction getLocalTransaction() throws ResourceException {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#getLogWriter()
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		return logWriter;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#getMetaData()
	 */
	public ManagedConnectionMetaData getMetaData() throws ResourceException {
		return new RuleManagedConnectionMetaData(this);
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#getXAResource()
	 */
	public XAResource getXAResource() throws ResourceException {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#removeConnectionEventListener(javax.resource.spi.ConnectionEventListener)
	 */
	public void removeConnectionEventListener(ConnectionEventListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#setLogWriter(java.io.PrintWriter)
	 */
	public void setLogWriter(PrintWriter logWriter) throws ResourceException {
		this.logWriter = logWriter;
	}
	
	// Public ----------------------------------------------------------------

	/**
	 * @return the cri
	 */
	public RuleConnectionRequestInfo getConnectionRequestInfo() {
		return cri;
	}

	/**
	 * @return the mcf
	 */
	public RuleManagedConnectionFactory getManagedConnectionFactory() {
		return mcf;
	}

	/**
	 * TODO
	 *
	 * @param handle
	 * @return
	 */
	public RuleSession getRuleSession(RuleSessionHandle handle) {
		synchronized (handles) {
			if ((handles.size() > 0) && (handles.get(0) == handle)) {
				return ruleSession;
			} else {
				String s = Messages.getError("RuleManagedConnection.3"); //$NON-NLS-1$
				throw new IllegalStateException(s);
			}
		}
	}

	/**
	 * TODO
	 * 
	 * @param ruleSessionHandle
	 */
	public void releaseHandle(RuleSessionHandle handle) {
		if (handle != null) {
			removeHandle(handle);
			sendConnectionClosedEvent(handle);
		}
	}
	
	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	/**
	 * TODO
	 *
	 * @param handle
	 */
	private void addHandle(RuleSessionHandle handle) {
		synchronized (handles) {
			handles.addFirst(handle);
		}
	}

	/**
	 * TODO
	 *
	 * @param handle
	 */
	private void removeHandle(RuleSessionHandle handle) {
		synchronized (handles) {
			handles.remove(handle);
		}
	}

	/**
	 * TODO
	 * 
	 * @param handle
	 */
	private void sendConnectionClosedEvent(RuleSessionHandle handle) {
		sendEvent(ConnectionEvent.CONNECTION_CLOSED, handle, null);
	}
	
	/**
	 * TODO
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	private void sendEvent(ConnectionEvent event) {
		synchronized (listeners) {
			for (Iterator i = listeners.iterator(); i.hasNext(); ) {
				ConnectionEventListener listener = (ConnectionEventListener)i.next();
				
				switch (event.getId()) {
				case ConnectionEvent.CONNECTION_CLOSED:
					listener.connectionClosed(event);
					break;
				case ConnectionEvent.CONNECTION_ERROR_OCCURRED:
					listener.connectionErrorOccurred(event);
					break;
				case ConnectionEvent.LOCAL_TRANSACTION_COMMITTED:
					listener.localTransactionCommitted(event);
					break;
				case ConnectionEvent.LOCAL_TRANSACTION_ROLLEDBACK:
					listener.localTransactionRolledback(event);
					break;
				case ConnectionEvent.LOCAL_TRANSACTION_STARTED:
					listener.localTransactionStarted(event);
					break;
				default:
					// Unknown event, skip
				}
			}
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param id
	 * @param handle
	 * @param cause
	 */
	private void sendEvent(int id, Object handle, Exception cause) {
		ConnectionEvent event = new ConnectionEvent(this, id, cause);
		
		if (handle != null) {
			event.setConnectionHandle(handle);
		}
		
		sendEvent(event);
	}

	// Inner classes ---------------------------------------------------------
}
