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
package net.sourceforge.rules.deployer.drools;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.rules.deployer.AbstractRulesDeployer;
import net.sourceforge.rules.deployer.RulesDeployerConfiguration;
import net.sourceforge.rules.deployer.RulesDeployerException;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
@Named("drools-deployer")
public class DroolsRulesDeployer extends AbstractRulesDeployer
{
	// Constants -------------------------------------------------------------

    private static final Logger LOG = LoggerFactory.getLogger(DroolsRulesDeployer.class);

	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// RulesDeployer Overrides -----------------------------------------------

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.deployer.RulesDeployer#deploy(net.sourceforge.rules.deployer.RulesDeployerConfiguration)
	 */
	@Override
	public void deploy(RulesDeployerConfiguration configuration) throws RulesDeployerException {

		LOG.info("deploy(" + configuration + ")");
	}
	
	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
