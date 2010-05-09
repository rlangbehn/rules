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

import java.util.Arrays;
import java.util.Map;

import javax.resource.spi.ConnectionRequestInfo;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RuleConnectionRequestInfo implements ConnectionRequestInfo
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private String ruleExecutionSetBindUri;

	/**
	 * TODO
	 */
	@SuppressWarnings("unchecked")
	private Map ruleSessionProperties;
	
	/**
	 * TODO
	 */
	private int ruleSessionType;

	/**
	 * TODO
	 */
	private char[] password;
	
	/**
	 * TODO
	 */
	private String userName;
	
	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------
	
	/**
	 * Copy ctor.
	 *
	 * @param cri
	 */
	public RuleConnectionRequestInfo(RuleConnectionRequestInfo cri) {
		this(
			cri.ruleExecutionSetBindUri,
			cri.ruleSessionProperties,
			cri.ruleSessionType,
			cri.userName,
			cri.password
		);
	}

	/**
	 * TODO
	 *
	 * @param ruleExecutionSetBindUri
	 * @param ruleSessionProperties
	 * @param ruleSessionType
	 * @param userName
	 * @param password
	 */
	@SuppressWarnings("unchecked")
	public RuleConnectionRequestInfo(
			String ruleExecutionSetBindUri,
			Map ruleSessionProperties,
			int ruleSessionType,
			String userName,
			char[] password) {
		
		this.ruleExecutionSetBindUri = ruleExecutionSetBindUri;
		this.ruleSessionProperties = ruleSessionProperties;
		this.ruleSessionType = ruleSessionType;
		this.userName = userName;
		
		if (password != null) {
			this.password = password.clone();
		}
	}

	// Object overrides ------------------------------------------------------

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof RuleConnectionRequestInfo)) return false;
		
		final RuleConnectionRequestInfo cri = (RuleConnectionRequestInfo)o;

		return equals(ruleExecutionSetBindUri, cri.ruleExecutionSetBindUri)
		    && equals(ruleSessionProperties, cri.ruleSessionProperties)
		    && equals(userName, cri.userName)
		    && Arrays.equals(password, cri.password)
		    && ruleSessionType == cri.ruleSessionType;
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
		result = prime * result + hashCode(userName);
		result = prime * result + Arrays.hashCode(password);
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
		sb.append(" ruleSessionType=").append(ruleSessionType);
		sb.append(" userName=").append(userName);
		sb.append(" password=").append("*****").append("]");
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
	
    /**
	 * @return the password
	 */
	public char[] getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(char[] password) {
		if (password == null) {
			this.password = null;
		} else {
			this.password = password.clone();
		}
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
     * @param o
     * @return
     */
    private int hashCode(Object o) {
    	return o == null ? 0 : o.hashCode();
    }
    
	// Inner classes ---------------------------------------------------------
}
