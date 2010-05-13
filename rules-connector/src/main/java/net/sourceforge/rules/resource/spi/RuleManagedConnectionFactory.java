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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;
import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

	/**
	 * The <code>Log</code> instance for this class.
	 */
	private static final Log log = LogFactory.getLog(
			RuleManagedConnectionFactory.class);

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	// Attributes ------------------------------------------------------------

	/**
	 * The <code>PrintWriter</code> instance where log output goes.
	 */
	private transient PrintWriter logWriter;

	/**
	 * The <code>RuleResourceAdapter</code> instance we're associated with. 
	 */
	private transient RuleResourceAdapter ruleResourceAdapter;
	
	/**
	 * Rule service provider class name.
	 */
	private String ruleServiceProviderClassName;
	
	/**
	 * Rule service provider uri.
	 */
	private String ruleServiceProviderUri;

	/**
	 * TODO
	 */
	private String password;
	
	/**
	 * TODO
	 */
	private String userName;

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// ManagedConnectionFactory implementation -------------------------------

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnectionFactory#createConnectionFactory()
	 */
	public Object createConnectionFactory() throws ResourceException {
		return createConnectionFactory(null);
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnectionFactory#createConnectionFactory(javax.resource.spi.ConnectionManager)
	 */
	public Object createConnectionFactory(ConnectionManager cm)
	throws ResourceException {
		
		Object cf = new RuleRuntimeHandle(this, cm);
		
		if (log.isTraceEnabled()) {
			log.trace("Created connection factory (" + cf + "), using connection manager (" + cm + ")");
		}
		
		return cf;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnectionFactory#createManagedConnection(javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
	 */
	public ManagedConnection createManagedConnection(
			Subject subject,
			ConnectionRequestInfo cri)
	throws ResourceException {

		boolean traceEnabled = log.isTraceEnabled();

		if (traceEnabled) {
			log.trace("Creating managed connection");
		}
		
		RuleConnectionRequestInfo rcri = getRuleConnectionRequestInfo(subject, cri);
		
		if (traceEnabled) {
			log.trace("Using connection request info (" + rcri + ")");
		}
		
		RuleManagedConnection mc = new RuleManagedConnection(this, rcri);
		
		if (traceEnabled) {
			log.trace("Created managed connection (" + mc + ")");
		}
		
		return mc;
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

		boolean traceEnabled = log.isTraceEnabled();

		if (traceEnabled) {
			StringBuilder sb = new StringBuilder();
			sb.append("Matching managed connections ");
			sb.append("(connectionSet=").append(connectionSet);
			sb.append(" subject=").append(subject);
			sb.append(" cri=").append(cri).append(")");
			log.trace(sb.toString());
		}
		
		for (Object connection : connectionSet) {
			if (connection instanceof RuleManagedConnection) {
				RuleManagedConnection mc = (RuleManagedConnection)connection;
				
				if (equals(mc.getManagedConnectionFactory())) {
					RuleConnectionRequestInfo otherCri = mc.getConnectionRequestInfo();
					
					if (equals(cri, otherCri)) {
						
						if (traceEnabled) {
							log.trace("Matched managed connection (" + mc + ")");
						}
						
						return mc;
					}
				}
			}
		}

		if (traceEnabled) {
			log.trace("No matching managed connection found");
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
			String s = "Method 'setResourceAdapter' already called on this instance";
			throw new IllegalStateException(s);
		}
		
		if (!(resourceAdapter instanceof RuleResourceAdapter)) {
			String s = "ResourceAdapter instance is not of expected type";
			throw new ResourceException(s);
		}
		
		this.ruleResourceAdapter = (RuleResourceAdapter)resourceAdapter;
	}

	// Object overrides ------------------------------------------------------

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
		    && equals(ruleServiceProviderUri, mcf.ruleServiceProviderUri)
		    && equals(userName, mcf.userName)
		    && equals(password, mcf.password);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hashCode(ruleServiceProviderClassName);
		result = prime * result + hashCode(ruleServiceProviderUri);
		result = prime * result + hashCode(userName);
		result = prime * result + hashCode(password);
		return result;
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

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

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
     * @param mcf 
     * @param subject
     * @return
     */
    private PasswordCredential getPasswordCredential(
    		final ManagedConnectionFactory mcf,
    		final Subject subject) {
    	
    	return AccessController.doPrivileged(
    			new PrivilegedAction<PasswordCredential>() {
    				public PasswordCredential run() {
    					
    					Set<PasswordCredential> pcs = subject.getPrivateCredentials(
    							PasswordCredential.class
    					);
    					
    					for (PasswordCredential pc : pcs) {
    						if (mcf.equals(pc.getManagedConnectionFactory())) {
    							return pc;
    						}
    					}
    					
    			    	return null;
    				}
    			}
    	);
    }
    
	/**
	 * TODO
	 * 
	 * @param subject 
	 * @param cri
	 * @return
	 * @throws ResourceException
	 */
	@SuppressWarnings("unchecked")
	private RuleConnectionRequestInfo getRuleConnectionRequestInfo(
			Subject subject,
			ConnectionRequestInfo cri)
	throws ResourceException {
		
		if (!(cri instanceof RuleConnectionRequestInfo)) {
			String s = "ConnectionRequestInfo instance is not of expected type";
			throw new ResourceException(s);
		}

		RuleConnectionRequestInfo rcri = (RuleConnectionRequestInfo)cri;

		if (rcri.getRuleSessionProperties() == null) {
			rcri.setRuleSessionProperties(new HashMap());
		}

		if (subject == null) {
			if ((rcri.getUserName() == null) && (userName != null)) {
				rcri.setUserName(userName);
			}
			
			if ((rcri.getPassword() == null) && (password != null)) {
				rcri.setPassword(password.toCharArray());
			}
		} else {
			PasswordCredential pc = getPasswordCredential(this, subject);
			
			if (pc == null) {
				String s = "No PasswordCredential found";
				throw new SecurityException(s);
			}

			rcri.setUserName(pc.getUserName());
			rcri.setPassword(pc.getPassword());
		}
		
		return rcri;
	}

    /**
     * TODO
     * 
     * @param o
     * @return
     */
    private int hashCode(Object o) {
    	return o == null ? 0 : o.hashCode();
    }

	// Inner classes ---------------------------------------------------------
}
