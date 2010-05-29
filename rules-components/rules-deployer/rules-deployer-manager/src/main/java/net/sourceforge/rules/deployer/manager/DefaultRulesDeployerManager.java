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
package net.sourceforge.rules.deployer.manager;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.rules.deployer.RulesDeployer;

import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * This is the default <code>RulesDeployerManager</code> implementation.
 *
 * @plexus.component
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class DefaultRulesDeployerManager
	extends AbstractLogEnabled
	implements RulesDeployerManager
{
	/**
	 * TODO
	 * 
	 * @plexus.requirement role="net.sourceforge.rules.deployer.RulesDeployer"
	 */
	private Map<String, RulesDeployer> rulesDeployers =
		new HashMap<String, RulesDeployer>();
	
	/* (non-Javadoc)
	 * @see net.sourceforge.rules.deployer.manager.RulesDeployerManager#getRulesDeployer(java.lang.String)
	 */
	public RulesDeployer getRulesDeployer(String rulesDeployerId)
	throws NoSuchRulesDeployerException {

		RulesDeployer rulesDeployer = rulesDeployers.get(rulesDeployerId);

		if (rulesDeployer == null) {
			throw new NoSuchRulesDeployerException(rulesDeployerId);
		}
		
		return rulesDeployer;
	}
}