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

import java.io.Serializable;
import java.util.Map;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RuleActivationSpec implements ActivationSpec, Serializable
{
	// Constants -------------------------------------------------------------

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private String ruleExecutionSetBindUri;

	/**
	 * The <code>RuleResourceAdapter</code> instance we're associated with. 
	 */
	private transient RuleResourceAdapter ruleResourceAdapter;
	
	/**
	 * TODO
	 */
	@SuppressWarnings("unchecked")
	private Map ruleSessionProperties;
	
	/**
	 * TODO
	 */
	private int ruleSessionType;
	
	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// ActivationSpec implementation -----------------------------------------

	/* (non-Javadoc)
	 * @see javax.resource.spi.ActivationSpec#validate()
	 */
	public void validate() throws InvalidPropertyException {
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
		if (!(o instanceof RuleActivationSpec)) return false;
		
		final RuleActivationSpec as = (RuleActivationSpec)o;

		return equals(ruleExecutionSetBindUri, as.ruleExecutionSetBindUri)
		    && equals(ruleSessionProperties, as.ruleSessionProperties)
		    && ruleSessionType == as.ruleSessionType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hashCode(ruleExecutionSetBindUri);
		result = prime * result + hashCode(ruleSessionProperties);
		result = prime * result + ruleSessionType;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("[ruleExecutionSetBindUri=").append(ruleExecutionSetBindUri);
		sb.append(" ruleSessionProperties=").append(ruleSessionProperties);
		sb.append(" ruleSessionType=").append(ruleSessionType).append("]");
		return sb.toString();
	}

	// Public ----------------------------------------------------------------

	/**
	 * @return the ruleExecutionSetBindUri
	 */
	public String getRuleExecutionSetBindUri() {
		return ruleExecutionSetBindUri;
	}

	/**
	 * @param ruleExecutionSetBindUri the ruleExecutionSetBindUri to set
	 */
	public void setRuleExecutionSetBindUri(String ruleExecutionSetBindUri) {
		this.ruleExecutionSetBindUri = ruleExecutionSetBindUri;
	}

	/**
	 * @return the ruleSessionProperties
	 */
	@SuppressWarnings("unchecked")
	public Map getRuleSessionProperties() {
		return ruleSessionProperties;
	}

	/**
	 * @param ruleSessionProperties the ruleSessionProperties to set
	 */
	@SuppressWarnings("unchecked")
	public void setRuleSessionProperties(Map ruleSessionProperties) {
		this.ruleSessionProperties = ruleSessionProperties;
	}

	/**
	 * @return the ruleSessionType
	 */
	public int getRuleSessionType() {
		return ruleSessionType;
	}

	/**
	 * @param ruleSessionType the ruleSessionType to set
	 */
	public void setRuleSessionType(int ruleSessionType) {
		this.ruleSessionType = ruleSessionType;
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
     * @param o
     * @return
     */
    private int hashCode(Object o) {
    	return o == null ? 0 : o.hashCode();
    }
    
	// Inner classes ---------------------------------------------------------
}
