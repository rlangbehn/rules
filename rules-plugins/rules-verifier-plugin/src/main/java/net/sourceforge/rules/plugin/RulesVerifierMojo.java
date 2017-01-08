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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Verifies the main rules of your project.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
@Mojo(defaultPhase = LifecyclePhase.VERIFY, name = "verify", requiresDependencyResolution = ResolutionScope.COMPILE)
public class RulesVerifierMojo extends AbstractRulesVerifierMojo
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

    /**
     * A list of exclusion filters for the rules verifier.
     */
	@Parameter
    private Set<String> excludes = new HashSet<>();

    /**
     * A list of inclusion filters for the rules verifier.
     */
	@Parameter
    private Set<String> includes = new HashSet<>();

    /**
     * The directory for generated verifier reports.
     */
	@Parameter(defaultValue = "${project.build.directory}/verifier-reports")
    private File reportsDirectory;

    /**
     * TODO
     */
	@Parameter(defaultValue = "${basedir}/src/main/rules", required = true)
    private File rulesDirectory;
    
    // Static ----------------------------------------------------------------
    
    // Constructors ----------------------------------------------------------
    
    // AbstractRulesVerifierMojo Overrides -----------------------------------
    
	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesVerifierMojo#getExcludes()
	 */
	@Override
	protected Set<String> getExcludes() {
		return Collections.unmodifiableSet(excludes);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesVerifierMojo#getIncludes()
	 */
	@Override
	protected Set<String> getIncludes() {
		return Collections.unmodifiableSet(includes);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesVerifierMojo#getReportsDirectory()
	 */
	@Override
	protected File getReportsDirectory() {
		return reportsDirectory;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesVerifierMojo#getRulesDirectory()
	 */
	@Override
	protected File getRulesDirectory() {
		return rulesDirectory;
	}

    // Package protected -----------------------------------------------------
    
    // Protected -------------------------------------------------------------

    // Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
