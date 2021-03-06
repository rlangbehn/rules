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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.rules.ConfigurationException;
import javax.rules.InvalidRuleSessionException;
import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.RuleSession;
import javax.rules.RuleSessionCreateException;
import javax.rules.RuleSessionTypeUnsupportedException;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the JCA ManagedConnection contract.
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RuleManagedConnection implements ManagedConnection
{
	// Constants -------------------------------------------------------------

	/**
	 * The <code>Logger</code> instance for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(
			RuleManagedConnection.class);

	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private static boolean traceEnabled = logger.isTraceEnabled();
	
	/**
	 * TODO
	 */
	private final RuleConnectionRequestInfo cri;

	/**
	 * TODO
	 */
	private final LinkedList<RuleSessionHandle> handles =
		new LinkedList<RuleSessionHandle>();
	
	/**
	 * TODO 
	 */
	private final List<ConnectionEventListener> listeners =
		new CopyOnWriteArrayList<ConnectionEventListener>();
	
	/**
	 * TODO 
	 */
	private final RuleManagedConnectionFactory mcf;

	/**
	 * The underlying physical rules session. 
	 */
	private final RuleSession ruleSession;
	
	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	/**
	 * TODO
	 *
	 * @param mcf
	 * @param cri
	 * @throws ResourceException 
	 */
	public RuleManagedConnection(
			RuleManagedConnectionFactory mcf,
			RuleConnectionRequestInfo cri)
	throws ResourceException {
		
		this.mcf = mcf;
		this.cri = cri;
		
		ruleSession = createRuleSession();
	}

	// ManagedConnection implementation --------------------------------------

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#addConnectionEventListener(javax.resource.spi.ConnectionEventListener)
	 */
	public void addConnectionEventListener(ConnectionEventListener listener) {

		if (traceEnabled) {
			logger.trace("addConnectionEventListener(" + listener + ")");
		}
		
		if (!listeners.contains(listener)) {
			listeners.add(listener);

			if (traceEnabled) {
				logger.trace("Added connection event listener (" + listener + ")");
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#associateConnection(java.lang.Object)
	 */
	public void associateConnection(Object connection)
	throws ResourceException {

		if (traceEnabled) {
			logger.trace("associateConnection(" + connection + ")");
		}
		
		if (!(connection instanceof RuleSessionHandle)) {
			String s = "Connection instance is not of expected type";
			throw new ResourceException(s);
		}

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
		
		if (traceEnabled) {
			logger.trace("cleanup()");
		}
		
		synchronized (handles) {
			handles.clear();
		}
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#destroy()
	 */
	public void destroy() throws ResourceException {
		
		if (traceEnabled) {
			logger.trace("destroy()");
		}
		
		cleanup();
		
		try {
			
			if (traceEnabled) {
				logger.trace("Releasing rule session (" + ruleSession + ")");
			}
			
			ruleSession.release();
			
			if (traceEnabled) {
				logger.trace("Released rule session (" + ruleSession + ")");
			}
			
		} catch (InvalidRuleSessionException e) {
			String s = "Error while releasing rule session";
			throw new ResourceException(s, e);
		} catch (RemoteException e) {
			String s = "Error while releasing rule session";
			throw new ResourceException(s, e);
		}
		
		if (traceEnabled) {
			logger.trace("Destroyed managed connection (" + this + ")");
		}
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#getConnection(javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
	 */
	public Object getConnection(Subject subject, ConnectionRequestInfo cri)
	throws ResourceException {

		if (traceEnabled) {
			logger.trace("getConnection(" + subject + ", " + cri + ")");
		}
		
		if (!(cri instanceof RuleConnectionRequestInfo)) {
			String s = "ConnectionRequestInfo instance is not of expected type";
			throw new ResourceException(s);
		}

		RuleConnectionRequestInfo rcri = (RuleConnectionRequestInfo)cri;
		int ruleSessionType = rcri.getRuleSessionType();
		RuleSessionHandle handle = null;
		
		if (ruleSessionType == RuleRuntime.STATEFUL_SESSION_TYPE) {
			handle = new StatefulRuleSessionHandle(this);
		} else if (ruleSessionType == RuleRuntime.STATELESS_SESSION_TYPE) {
			handle = new StatelessRuleSessionHandle(this);
		} else {
			String s = "Unsupported rule session type: " + ruleSessionType;
			throw new IllegalStateException(s);
		}

		if (traceEnabled) {
			logger.trace("Created connection (" + handle + ")");
		}
		
		addHandle(handle);
		return handle;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#getLocalTransaction()
	 */
	public LocalTransaction getLocalTransaction() throws ResourceException {
		
		if (traceEnabled) {
			logger.trace("getLocalTransaction()");
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#getLogWriter()
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		
		if (traceEnabled) {
			logger.trace("getLogWriter()");
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#getMetaData()
	 */
	public ManagedConnectionMetaData getMetaData() throws ResourceException {
		
		if (traceEnabled) {
			logger.trace("getMetaData()");
		}
		
		return new RuleManagedConnectionMetaData(this);
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#getXAResource()
	 */
	public XAResource getXAResource() throws ResourceException {
		
		if (traceEnabled) {
			logger.trace("getXAResource()");
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#removeConnectionEventListener(javax.resource.spi.ConnectionEventListener)
	 */
	public void removeConnectionEventListener(ConnectionEventListener listener) {
		
		if (traceEnabled) {
			logger.trace("removeConnectionEventListener(" + listener + ")");
		}
		
		listeners.remove(listener);

		if (traceEnabled) {
			logger.trace("Removed connection event listener (" + listener + ")");
		}
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnection#setLogWriter(java.io.PrintWriter)
	 */
	public void setLogWriter(PrintWriter logWriter) throws ResourceException {
		
		if (traceEnabled) {
			logger.trace("setLogWriter(" + logWriter + ")");
		}
	}
	
	// Public ----------------------------------------------------------------

	/**
	 * @return the cri
	 */
	public RuleConnectionRequestInfo getConnectionRequestInfo() {
		
		if (traceEnabled) {
			logger.trace("getConnectionRequestInfo()");
		}
		
		return cri;
	}

	/**
	 * @return the mcf
	 */
	public RuleManagedConnectionFactory getManagedConnectionFactory() {
		
		if (traceEnabled) {
			logger.trace("getManagedConnectionFactory()");
		}
		
		return mcf;
	}

	/**
	 * TODO
	 *
	 * @param handle
	 * @return
	 */
	public RuleSession getRuleSession(RuleSessionHandle handle) {
		
		if (traceEnabled) {
			logger.trace("getRuleSession(" + handle + ")");
		}
		
		synchronized (handles) {
			if ((handles.size() > 0) && (handles.get(0) == handle)) {
				return ruleSession;
			} else {
				String s = "Inactive logical rule session handle called";
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

		if (traceEnabled) {
			logger.trace("releaseHandle(" + handle + ")");
		}
		
		if (handle != null) {
			removeHandle(handle);
			
			if (traceEnabled) {
				logger.trace("Released rule session (" + handle + ")");
			}
			
			fireConnectionClosed(handle);
		}
	}
	
	// Package protected -----------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param handle
	 */
	void fireConnectionClosed(RuleSessionHandle handle) {
		
		if (traceEnabled) {
			logger.trace("fireConnectionClosed(" + handle + ")");
		}
		
		sendEvent(ConnectionEvent.CONNECTION_CLOSED, handle, null);
	}
	
	/**
	 * TODO
	 * 
	 * @param handle
	 * @param cause
	 */
	void fireConnectionErrorOccurred(
			RuleSessionHandle handle,
			Exception cause) {
		
		if (traceEnabled) {
			logger.trace("fireConnectionErrorOccurred(" + handle + ", " + cause + ")");
		}
		
		sendEvent(ConnectionEvent.CONNECTION_ERROR_OCCURRED, handle, cause);
	}
	
	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	/**
	 * TODO
	 *
	 * @param handle
	 */
	private void addHandle(RuleSessionHandle handle) {
		
		if (traceEnabled) {
			logger.trace("addHandle(" + handle + ")");
		}
		
		synchronized (handles) {
			handles.addFirst(handle);
		}
	}

	/**
	 * TODO
	 * 
	 * @return
	 * @throws ResourceException
	 */
	@SuppressWarnings("unchecked")
	private RuleSession createRuleSession() throws ResourceException {
		
		if (traceEnabled) {
			logger.trace("createRuleSession()");
		}
		
		RuleSession ruleSession = null;
		
		try {
			String ruleServiceProviderUri = mcf.getRuleServiceProviderUri();
			RuleRuntime ruleRuntime = JSR94Util.getRuleRuntime(ruleServiceProviderUri);

			Map properties = new HashMap(cri.getRuleSessionProperties());
			properties.put(RuleConstants.USERNAME_PROPERTY_KEY, cri.getUserName());
			properties.put(RuleConstants.PASSWORD_PROPERTY_KEY, cri.getPassword());
			
			ruleSession = ruleRuntime.createRuleSession(
					cri.getRuleExecutionSetBindUri(),
					properties,
					cri.getRuleSessionType()
			);

			if (traceEnabled) {
				logger.trace("Created rule session (" + ruleSession + ")");
			}
			
		} catch (ConfigurationException e) {
			String s = "Error while retrieving rule runtime";
			throw new ResourceException(s, e);
		} catch (RuleSessionTypeUnsupportedException e) {
            String s = "Failed to create rule session";
            throw new ResourceException(s, e);
		} catch (RuleSessionCreateException e) {
            String s = "Failed to create rule session";
            throw new ResourceException(s, e);
		} catch (RuleExecutionSetNotFoundException e) {
            String s = "Failed to create rule session";
            throw new ResourceException(s, e);
		} catch (RemoteException e) {
            String s = "Failed to create rule session";
            throw new ResourceException(s, e);
		}
		
		return ruleSession;
	}

	/**
	 * TODO
	 *
	 * @param handle
	 */
	private void removeHandle(RuleSessionHandle handle) {
		
		if (traceEnabled) {
			logger.trace("removeHandle(" + handle + ")");
		}
		
		synchronized (handles) {
			handles.remove(handle);
		}
	}

	/**
	 * TODO
	 * 
	 * @param event
	 */
	private void sendEvent(ConnectionEvent event) {
		
		if (traceEnabled) {
			logger.trace("sendEvent(" + event + ")");
		}
		
		for (ConnectionEventListener listener : listeners) {
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
	
	/**
	 * TODO
	 * 
	 * @param id
	 * @param handle
	 * @param cause
	 */
	private void sendEvent(int id, Object handle, Exception cause) {

		if (traceEnabled) {
			logger.trace("sendEvent(" + id + ", " + handle + ", " + cause + ")");
		}
		
		ConnectionEvent event = new ConnectionEvent(this, id, cause);
		
		if (handle != null) {
			event.setConnectionHandle(handle);
		}
		
		sendEvent(event);
	}

	// Inner classes ---------------------------------------------------------
}
