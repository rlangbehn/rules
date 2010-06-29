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

import java.io.File;
import java.util.Set;

import net.sourceforge.rules.verifier.RulesVerifier;
import net.sourceforge.rules.verifier.RulesVerifierConfiguration;
import net.sourceforge.rules.verifier.RulesVerifierException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public abstract class AbstractRulesVerifierMojo extends AbstractMojo
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 * 
     * @component
	 */
	private RulesVerifier rulesVerifier;
	
    /**
     * Set to true to show messages about what the rules verifier is doing.
     *
     * @parameter expression="${rules-verifier.verbose}" default-value="false"
     */
    private boolean verbose;

    // Static ----------------------------------------------------------------
    
    // Constructors ----------------------------------------------------------
    
    // AbstractMojo Overrides ------------------------------------------------
    
    /* (non-Javadoc)
     * @see org.apache.maven.plugin.AbstractMojo#execute()
     */
    public void execute()
    throws MojoExecutionException {

    	RulesVerifierConfiguration config = new RulesVerifierConfiguration();
    	config.setOutputDirectory(getOutputDirectory());
    	config.setRulesDirectory(getRulesDirectory());
    	config.setVerbose(verbose);

    	Set<String> includes = getIncludes();

    	for (String include : includes) {
    		config.addInclude(include);
    	}
    	
    	Set<String> excludes = getExcludes();

    	for (String exclude : excludes) {
    		config.addExclude(exclude);
    	}
    	
    	try {
			rulesVerifier.verify(config);
		} catch (RulesVerifierException e) {
			String s = "Error while verifying rule execution sets";
			throw new MojoExecutionException(s, e);
		}
    }

    // Package protected -----------------------------------------------------
    
    // Protected -------------------------------------------------------------

    /**
     * TODO
     * 
     * @return
     */
    protected abstract Set<String> getExcludes();
    
    /**
     * TODO
     * 
     * @return
     */
    protected abstract Set<String> getIncludes();
    
    /**
     * TODO
     * 
     * @return
     */
    protected abstract File getOutputDirectory();

    /**
     * TODO
     * 
     * @return
     */
    protected abstract File getRulesDirectory();

    // Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
