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
import java.util.Map;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;
import javax.rules.ConfigurationException;
import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.RuleSession;
import javax.rules.RuleSessionCreateException;
import javax.rules.RuleSessionTypeUnsupportedException;
import javax.security.auth.Subject;

/**
 * Implements the JCA ManagedConnectionFactory system contract.
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RuleManagedConnectionFactory
	implements ManagedConnectionFactory, ResourceAdapterAssociation
{
	// Constants -------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private transient PrintWriter logWriter;

	/**
	 * TODO 
	 */
	private transient RuleResourceAdapter ruleResourceAdapter;
	
	/**
	 * TODO
	 */
	private String ruleServiceProviderClassName;
	
	/**
	 * TODO 
	 */
	private String ruleServiceProviderUri;
	
	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// ManagedConnectionFactory implementation -------------------------------

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnectionFactory#createConnectionFactory()
	 */
	public Object createConnectionFactory() throws ResourceException {
		ConnectionManager cm = new RuleConnectionManager();
		return createConnectionFactory(cm);
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnectionFactory#createConnectionFactory(javax.resource.spi.ConnectionManager)
	 */
	public Object createConnectionFactory(ConnectionManager cm)
	throws ResourceException {
		registerRuleServiceProvider();
		return new RuleRuntimeHandle(this, cm);
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnectionFactory#createManagedConnection(javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
	 */
	public ManagedConnection createManagedConnection(
			Subject subject,
			ConnectionRequestInfo cri)
	throws ResourceException {
		if (!(cri instanceof RuleConnectionRequestInfo)) {
			String s = Messages.getError("RuleManagedConnectionFactory.0"); //$NON-NLS-1$
			throw new ResourceException(s);
		}
		RuleConnectionRequestInfo rcri = (RuleConnectionRequestInfo)cri;
		RuleSession ruleSession = createRuleSession(rcri);
		return new RuleManagedConnection(this, rcri, ruleSession);
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnectionFactory#getLogWriter()
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		return logWriter;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnectionFactory#matchManagedConnections(java.util.Set, javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public ManagedConnection matchManagedConnections(
			Set connectionSet,
			Subject subject,
			ConnectionRequestInfo cri)
	throws ResourceException {

		if (connectionSet == null) {
			return null;
		}

		for (Iterator i = connectionSet.iterator(); i.hasNext(); ) {
			Object next = i.next();
			
			if (next instanceof RuleManagedConnection) {
				RuleManagedConnection mc = (RuleManagedConnection)next;
				if (equals(mc.getManagedConnectionFactory())) {
					RuleConnectionRequestInfo otherCri = mc.getConnectionRequestInfo();
					if (equals(cri, otherCri)) {
						return mc;
					}
				}
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnectionFactory#setLogWriter(java.io.PrintWriter)
	 */
	public void setLogWriter(PrintWriter logWriter)
	throws ResourceException {
		this.logWriter = logWriter;
	}

	// ResourceAdapterAssociation implementation -----------------------------

	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapterAssociation#getResourceAdapter()
	 */
	public ResourceAdapter getResourceAdapter() {
		return ruleResourceAdapter;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapterAssociation#setResourceAdapter(javax.resource.spi.ResourceAdapter)
	 */
	public void setResourceAdapter(ResourceAdapter resourceAdapter)
	throws ResourceException {

		if (ruleResourceAdapter != null) {
			String s = Messages.getError("RuleManagedConnectionFactory.2", "setResourceAdapter"); //$NON-NLS-1$
			throw new IllegalStateException(s);
		}
		
		if (!(resourceAdapter instanceof RuleResourceAdapter)) {
			String s = Messages.getError("RuleManagedConnectionFactory.3"); //$NON-NLS-1$
			throw new ResourceException(s);
		}
		
		this.ruleResourceAdapter = (RuleResourceAdapter)resourceAdapter;
	}

	// Object overrides ------------------------------------------------------

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((ruleServiceProviderClassName == null) ? 0
						: ruleServiceProviderClassName.hashCode());
		result = prime
				* result
				+ ((ruleServiceProviderUri == null) ? 0
						: ruleServiceProviderUri.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof RuleManagedConnectionFactory)) return false;
		
		final RuleManagedConnectionFactory mcf = (RuleManagedConnectionFactory)o;

		return equals(ruleServiceProviderClassName, mcf.ruleServiceProviderClassName)
		    && equals(ruleServiceProviderUri, mcf.ruleServiceProviderUri);
	}

	// Public ----------------------------------------------------------------

	/**
	 * @return the ruleServiceProviderClassName
	 */
	public String getRuleServiceProviderClassName() {
		return ruleServiceProviderClassName;
	}

	/**
	 * @param ruleServiceProviderClassName the ruleServiceProviderClassName to set
	 */
	public void setRuleServiceProviderClassName(String ruleServiceProviderClassName) {
		this.ruleServiceProviderClassName = ruleServiceProviderClassName;
	}

	/**
	 * @return the ruleServiceProviderUri
	 */
	public String getRuleServiceProviderUri() {
		return ruleServiceProviderUri;
	}

	/**
	 * @param ruleServiceProviderUri the ruleServiceProviderUri to set
	 */
	public void setRuleServiceProviderUri(String ruleServiceProviderUri) {
		this.ruleServiceProviderUri = ruleServiceProviderUri;
	}

	// Package protected -----------------------------------------------------

	/**
	 * @return the ruleRuntime
	 */
	RuleRuntime getRuleRuntime() {
		RuleServiceProvider ruleServiceProvider = getRuleServiceProvider();
		try {
			return ruleServiceProvider.getRuleRuntime();
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the ruleServiceProvider
	 */
	RuleServiceProvider getRuleServiceProvider() {
		String uri = getRuleServiceProviderUri();
		try {
			return RuleServiceProviderManager.getRuleServiceProvider(uri);
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	/**
	 * TODO
	 *
	 * @param cri
	 * @return
	 * @throws ResourceException 
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	private RuleSession createRuleSession(RuleConnectionRequestInfo cri)
	throws ResourceException {
		
		String uri = cri.getRuleExecutionSetBindUri();
		Map properties = cri.getRuleSessionProperties();
		int ruleSessionType = cri.getRuleSessionType();
		RuleRuntime ruleRuntime = getRuleRuntime();
		RuleSession ruleSession;
		
		try {
			ruleSession = ruleRuntime.createRuleSession(uri, properties, ruleSessionType);
		} catch (RuleSessionTypeUnsupportedException e) {
            String s = Messages.getError("RuleManagedConnectionFactory.5"); //$NON-NLS-1$
            throw new ResourceException(s, e);
		} catch (RuleSessionCreateException e) {
            String s = Messages.getError("RuleManagedConnectionFactory.6"); //$NON-NLS-1$
            throw new ResourceException(s, e);
		} catch (RuleExecutionSetNotFoundException e) {
            String s = Messages.getError("RuleManagedConnectionFactory.7"); //$NON-NLS-1$
            throw new ResourceException(s, e);
		} catch (RemoteException e) {
            String s = Messages.getError("RuleManagedConnectionFactory.8"); //$NON-NLS-1$
            throw new ResourceException(s, e);
		}
		
		return ruleSession;
	}

    /**
     * TODO
     * 
     * @param o1
     * @param o2
     * @return
     */
    private boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

	/**
	 * TODO
	 *
	 * @param message
	 */
	private void log(String message) {
		log(message, null);
	}

    /**
     * TODO
     *
     * @param message
     * @param exception
     */
    private void log(String message, Throwable exception) {
        if (logWriter != null) {
            logWriter.println(message);

            if (exception != null) {
                exception.printStackTrace(logWriter);
            }
        }
    }

	/**
	 * TODO
	 * 
	 * @return
	 * @throws ResourceException
	 */
	private void registerRuleServiceProvider()
	throws ResourceException {

		String className = getRuleServiceProviderClassName();
		
		if ((className == null) || (className.trim().length() == 0)) {
			String s = Messages.getError("RuleManagedConnectionFactory.9", "ruleServiceProviderClassName"); //$NON-NLS-1$
			throw new ResourceException(s);
		}

		String uri = getRuleServiceProviderUri();
		
		if ((uri == null) || (uri.trim().length() == 0)) {
			String s = Messages.getError("RuleManagedConnectionFactory.10", "ruleServiceProviderUri"); //$NON-NLS-1$
			throw new ResourceException(s);
		}
		
		StringBuilder sb = new StringBuilder("Registering RuleServiceProvider"); //$NON-NLS-1$
		sb.append("\n\tClassName: ").append(className); //$NON-NLS-1$
		sb.append("\n\tUri:       ").append(uri); //$NON-NLS-1$
		log(sb.toString());

		ClassLoader cL = Thread.currentThread().getContextClassLoader();
		Class<?> clazz;

		try {
			clazz = cL.loadClass(className);
		} catch (ClassNotFoundException e) {
			String s = Messages.getError("RuleManagedConnectionFactory.14"); //$NON-NLS-1$
			throw new ResourceException(s, e);
		}

		try {
			RuleServiceProviderManager.registerRuleServiceProvider(uri, clazz, cL);
		} catch (ConfigurationException e) {
			String s = Messages.getError("RuleManagedConnectionFactory.15"); //$NON-NLS-1$
			throw new ResourceException(s, e);
		}
	}

	// Inner classes ---------------------------------------------------------
}
