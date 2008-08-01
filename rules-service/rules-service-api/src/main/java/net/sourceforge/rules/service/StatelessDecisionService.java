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
package net.sourceforge.rules.service;

import java.util.List;
import java.util.Map;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public interface StatelessDecisionService
{
	/**
	 * Executes the <code>javax.rules.RuleExecutionSet</code> registered and
	 * associated with the URI given by the parameter <code>bindUri</code>.
	 * 
	 * @param bindUri
	 * @param properties
	 * @param inputObjects
	 * @return
	 * @throws DecisionServiceException if an error prevented the execution
	 */
	List<?> decide(
			String bindUri,
			Map<?, ?> properties,
			List<?> inputObjects)
	throws DecisionServiceException;
}
