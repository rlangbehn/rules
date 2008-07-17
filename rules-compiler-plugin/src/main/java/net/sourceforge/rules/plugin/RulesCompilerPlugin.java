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
 * TODO
 * 
 * @goal compile
 * @description Compiles rules files.
 * @phase compile
 * @requiresDependencyResolution compile
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RulesCompilerPlugin extends AbstractRulesCompilerMojo
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

    /**
     * Project classpath.
     *
     * @parameter expression="${project.compileClasspathElements}"
     * @required
     * @readonly
     */
    private List<String> classpathElements;

    /**
     * A list of exclusion filters for the compiler.
     *
     * @parameter
     */
    private Set<String> excludes = new HashSet<String>();

    /**
     * A list of inclusion filters for the compiler.
     *
     * @parameter
     */
    private Set<String> includes = new HashSet<String>();

    /**
     * The directory for compiled classes.
     *
     * @parameter expression="${project.build.outputDirectory}"
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
     * Specifies the directory containing rules files.
     *
     * @parameter expression="${basedir}/src/main/rules"
     * @required
     */
    private File sourceDirectory;

    // Static ----------------------------------------------------------------
    
    // Constructors ----------------------------------------------------------
    
    // AbstractRulesCompilerMojo implementation ------------------------------
    
	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#createSourceInclusionScanner(int)
	 */
	protected SourceInclusionScanner createSourceInclusionScanner(int staleMillis) {
        SourceInclusionScanner scanner = null;

        if (includes.isEmpty() && excludes.isEmpty()) {
            scanner = new StaleSourceScanner(staleMillis);
        } else {
            if (includes.isEmpty()) {
                includes.add("**/*.brl"); //$NON-NLS-1$
                includes.add("**/*.csv"); //$NON-NLS-1$
                includes.add("**/*.drl"); //$NON-NLS-1$
                includes.add("**/*.dslr"); //$NON-NLS-1$
                includes.add("**/*.rfm"); //$NON-NLS-1$
                includes.add("**/*.xls"); //$NON-NLS-1$
                includes.add("**/*.xml"); //$NON-NLS-1$
            }
            scanner = new StaleSourceScanner(staleMillis, includes, excludes);
        }

        return scanner;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#createSourceInclusionScanner(java.lang.String)
	 */
	protected SourceInclusionScanner createSourceInclusionScanner(String inputFileEnding) {
        SourceInclusionScanner scanner = null;

        if (includes.isEmpty() && excludes.isEmpty()) {
            includes = Collections.singleton("**/*." + inputFileEnding); //$NON-NLS-1$
            scanner = new SimpleSourceInclusionScanner(includes, Collections.EMPTY_SET);
        } else {
            if (includes.isEmpty()) {
                includes.add( "**/*." + inputFileEnding); //$NON-NLS-1$
            }
            scanner = new SimpleSourceInclusionScanner(includes, excludes);
        }

        return scanner;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#createSourceMappings()
	 */
	protected List<SourceMapping> createSourceMappings() {
		List<SourceMapping> sourceMappings = new ArrayList<SourceMapping>();
		sourceMappings.add(new SuffixMapping("brl", "rules")); //$NON-NLS-1$ //$NON-NLS-2$
		sourceMappings.add(new SuffixMapping("csv", "rules")); //$NON-NLS-1$ //$NON-NLS-2$
		sourceMappings.add(new SuffixMapping("drl", "rules")); //$NON-NLS-1$ //$NON-NLS-2$
		sourceMappings.add(new SuffixMapping("dslr", "rules")); //$NON-NLS-1$ //$NON-NLS-2$
		sourceMappings.add(new SuffixMapping("rfm", "rules")); //$NON-NLS-1$ //$NON-NLS-2$
		sourceMappings.add(new SuffixMapping("xls", "rules")); //$NON-NLS-1$ //$NON-NLS-2$
		sourceMappings.add(new SuffixMapping("xml", "rules")); //$NON-NLS-1$ //$NON-NLS-2$
		return sourceMappings;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#getClasspathElements()
	 */
	protected List<String> getClasspathElements() {
		return classpathElements;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#getOutputDirectory()
	 */
	protected File getOutputDirectory() {
		return outputDirectory;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#getPluginClasspathElements()
	 */
	protected List<String> getPluginClasspathElements() {
		return pluginClasspathElements;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#getSourceDirectory()
	 */
	protected File getSourceDirectory() {
		return sourceDirectory;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.plugin.AbstractRulesCompilerMojo#execute()
	 */
	public void execute() throws MojoExecutionException, CompilationFailureException {
		super.execute();
	}

    // Package protected -----------------------------------------------------
    
    // Protected -------------------------------------------------------------

    // Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
