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
package net.sourceforge.rules.provider.drools;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetDeregistrationException;
import javax.rules.admin.RuleExecutionSetRegisterException;

import org.drools.jsr94.rules.repository.RuleExecutionSetRepository;
import org.drools.jsr94.rules.repository.RuleExecutionSetRepositoryException;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RuleExecutionSetRepositoryLoadedByPropertiesResource
	implements RuleExecutionSetRepository
{
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#getRegistrations()
	 */
	@SuppressWarnings("unchecked")
	public List getRegistrations()
	throws RuleExecutionSetRepositoryException {
		return Collections.EMPTY_LIST;
	}

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#getRuleExecutionSet(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public RuleExecutionSet getRuleExecutionSet(
			String bindUri,
			Map properties)
	throws RuleExecutionSetRepositoryException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#registerRuleExecutionSet(java.lang.String, javax.rules.admin.RuleExecutionSet, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public void registerRuleExecutionSet(
			String bindUri,
			RuleExecutionSet ruleSet,
			Map properties)
	throws RuleExecutionSetRegisterException {
		// empty on purpose
	}

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#unregisterRuleExecutionSet(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public void unregisterRuleExecutionSet(
			String bindUri,
			Map properties)
	throws RuleExecutionSetDeregistrationException {
		// empty on purpose
	}
}
