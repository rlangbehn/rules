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
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.compiler.util.scan.SimpleSourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;

/**
 * Compiles the test rules of your project.
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
@Mojo(defaultPhase = LifecyclePhase.TEST_COMPILE, name = "testCompile", requiresDependencyResolution = ResolutionScope.TEST)
public class TestRulesCompilerMojo extends AbstractRulesCompilerMojo
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

    /**
     * Project test classpath.
     */
	@Parameter(defaultValue = "${project.testClasspathElements}", readonly = true, required = true)
    private List<String> classpathElements;

    /**
     * The directory for compiled test rules.
     */
	@Parameter(defaultValue = "${project.build.testOutputDirectory}", readonly = true, required = true)
    private File outputDirectory;

    /**
     * List of artifacts for the plugin.
     */
	@Parameter(defaultValue = "${plugin.artifacts}", readonly = true, required = true)
    private List<String> pluginClasspathElements;

    /**
     * Set this to 'true' to bypass unit tests entirely.
     * Its use is NOT RECOMMENDED, but quite convenient on occasion.
     */
	@Parameter(defaultValue = "${maven.test.skip}")
    private boolean skip;

    /**
     * Specifies the directory containing rules files.
     */
	@Parameter(defaultValue = "${basedir}/src/test/rules", required = true)
    private File sourceDirectory;

    /**
     * A list of exclusion filters for the rules compiler.
     */
	@Parameter
    private Set<String> testExcludes = new HashSet<>();

    /**
     * A list of inclusion filters for the rules compiler.
     */
	@Parameter
    private Set<String> testIncludes = new HashSet<>();

    // Static ----------------------------------------------------------------
    
    // Constructors ----------------------------------------------------------
    
    // AbstractRulesCompilerMojo Overrides -----------------------------------
    
	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#createSourceInclusionScanner(int, java.lang.String[])
	 */
	@Override
	protected SourceInclusionScanner createSourceInclusionScanner(int staleMillis, String[] inputFileEndings) {
		
        SourceInclusionScanner scanner = null;

        if (testIncludes.isEmpty() && testExcludes.isEmpty()) {
            scanner = new StaleSourceScanner(staleMillis);
        } else {
            if (testIncludes.isEmpty()) {
            	for (String inputFileEnding : inputFileEndings) {
            		testIncludes.add("**/*." + inputFileEnding);
            	}
            }
            scanner = new StaleSourceScanner(staleMillis, testIncludes, testExcludes);
        }

        return scanner;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#createSourceInclusionScanner(java.lang.String[])
	 */
	@Override
	protected SourceInclusionScanner createSourceInclusionScanner(String[] inputFileEndings) {
		
        SourceInclusionScanner scanner = null;

        if (testIncludes.isEmpty() && testExcludes.isEmpty()) {
        	for (String inputFileEnding : inputFileEndings) {
        		testIncludes.add("**/*." + inputFileEnding);
        	}
            scanner = new SimpleSourceInclusionScanner(testIncludes, Collections.EMPTY_SET);
        } else {
            if (testIncludes.isEmpty()) {
            	for (String inputFileEnding : inputFileEndings) {
            		testIncludes.add("**/*." + inputFileEnding);
            	}
            }
            scanner = new SimpleSourceInclusionScanner(testIncludes, testExcludes);
        }

        return scanner;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException, CompilationFailureException {
        if (skip) {
            getLog().info("Not compiling test rules");
        } else {
            super.execute();
        }
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#getClasspathElements()
	 */
	@Override
	protected List<String> getClasspathElements() {
		return classpathElements;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#getOutputDirectory()
	 */
	@Override
	protected File getOutputDirectory() {
		return outputDirectory;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#getPluginClasspathElements()
	 */
	@Override
	protected List<String> getPluginClasspathElements() {
		return pluginClasspathElements;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#getSourceDirectory()
	 */
	@Override
	protected File getSourceDirectory() {
		return sourceDirectory;
	}

    // Package protected -----------------------------------------------------
    
    // Protected -------------------------------------------------------------

    // Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
