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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.compiler.util.scan.SimpleSourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SourceMapping;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;

/**
 * Compiles test rules files.
 * 
 * @goal testCompile
 * @phase test-compile
 * @requiresDependencyResolution test
 * 
 * @version $Revision: 72 $ $Date: 2008-07-17 13:17:01 +0200 (Do, 17 Jul 2008) $
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class TestRulesCompilerPlugin extends AbstractRulesCompilerMojo
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

    /**
     * Project test classpath.
     *
     * @parameter expression="${project.testClasspathElements}"
     * @required
     * @readonly
     */
    private List<String> classpathElements;

    /**
     * The directory for compiled test rules.
     *
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     * @readonly
     */
    private File outputDirectory;

    /**
     * List of artifacts for the plugin.
     *
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @readonly
     */
    private List<String> pluginClasspathElements;

    /**
     * Set this to 'true' to bypass unit tests entirely.
     * Its use is NOT RECOMMENDED, but quite convenient on occasion.
     *
     * @parameter expression="${maven.test.skip}"
     */
    private boolean skip;

    /**
     * Specifies the directory containing rules files.
     *
     * @parameter expression="${basedir}/src/test/rules"
     * @required
     */
    private File sourceDirectory;

    /**
     * A list of exclusion filters for the compiler.
     *
     * @parameter
     */
    private Set<String> testExcludes = new HashSet<String>();

    /**
     * A list of inclusion filters for the compiler.
     *
     * @parameter
     */
    private Set<String> testIncludes = new HashSet<String>();

    // Static ----------------------------------------------------------------
    
    // Constructors ----------------------------------------------------------
    
    // AbstractRulesCompilerMojo implementation ------------------------------
    
	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#createSourceInclusionScanner(int)
	 */
	@Override
	protected SourceInclusionScanner createSourceInclusionScanner(int staleMillis) {
        SourceInclusionScanner scanner = null;

        if (testIncludes.isEmpty() && testExcludes.isEmpty()) {
            scanner = new StaleSourceScanner(staleMillis);
        } else {
            if (testIncludes.isEmpty()) {
            	testIncludes.add("**/*.brl");
            	testIncludes.add("**/*.csv");
            	testIncludes.add("**/*.drl");
            	testIncludes.add("**/*.dslr");
            	testIncludes.add("**/*.rfm");
            	testIncludes.add("**/*.xls");
            	testIncludes.add("**/*.xml");
            }
            scanner = new StaleSourceScanner(staleMillis, testIncludes, testExcludes);
        }

        return scanner;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#createSourceInclusionScanner(java.lang.String)
	 */
	@Override
	protected SourceInclusionScanner createSourceInclusionScanner(String inputFileEnding) {
        SourceInclusionScanner scanner = null;

        if (testIncludes.isEmpty() && testExcludes.isEmpty()) {
            testIncludes = Collections.singleton("**/*." + inputFileEnding);
            scanner = new SimpleSourceInclusionScanner(testIncludes, Collections.EMPTY_SET);
        } else {
            if (testIncludes.isEmpty()) {
                testIncludes.add( "**/*." + inputFileEnding);
            }
            scanner = new SimpleSourceInclusionScanner(testIncludes, testExcludes);
        }

        return scanner;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#createSourceMappings()
	 */
	@Override
	protected List<SourceMapping> createSourceMappings() {
		List<SourceMapping> sourceMappings = new ArrayList<SourceMapping>();
		sourceMappings.add(new SuffixMapping("brl", "rules"));
		sourceMappings.add(new SuffixMapping("csv", "rules"));
		sourceMappings.add(new SuffixMapping("drl", "rules"));
		sourceMappings.add(new SuffixMapping("dslr", "rules"));
		sourceMappings.add(new SuffixMapping("rfm", "rules"));
		sourceMappings.add(new SuffixMapping("xls", "rules"));
		sourceMappings.add(new SuffixMapping("xml", "rules"));
		return sourceMappings;
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
