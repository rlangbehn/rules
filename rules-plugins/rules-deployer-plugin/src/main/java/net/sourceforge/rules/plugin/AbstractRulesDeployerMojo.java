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
package net.sourceforge.rules.plugin;

import net.sourceforge.rules.deployer.RulesDeployer;
import net.sourceforge.rules.deployer.RulesDeployerConfiguration;
import net.sourceforge.rules.deployer.RulesDeployerException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Base class for a Maven rules deployer.
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public abstract class AbstractRulesDeployerMojo extends AbstractMojo
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	/**
	 * TODO 
	 */
	@Component
	private BuildContext buildContext;
	
	/**
	 * The actual Plexus rules deployer component used to deploy the rules
	 * of your project.
	 */
	@Component
	private RulesDeployer rulesDeployer;
	
    /**
     * Set to true to show messages about what the rules deployer is doing.
     */
	@Parameter(defaultValue = "false", property = "rules-deployer.verbose")
    private boolean verbose;

    // Static ----------------------------------------------------------------
    
    // Constructors ----------------------------------------------------------
    
    // AbstractMojo Overrides ------------------------------------------------
    
    /* (non-Javadoc)
     * @see org.apache.maven.plugin.AbstractMojo#execute()
     */
	@Override
    public void execute() throws MojoExecutionException {
    	
    	RulesDeployerConfiguration config = new RulesDeployerConfiguration();
    	config.setVerbose(verbose);
    	
    	try {
			rulesDeployer.deploy(config);
		} catch (RulesDeployerException e) {
			String s = "Error while deploying rule execution sets";
			throw new MojoExecutionException(s, e);
		}
    }

    // Package protected -----------------------------------------------------
    
    // Protected -------------------------------------------------------------

    // Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
