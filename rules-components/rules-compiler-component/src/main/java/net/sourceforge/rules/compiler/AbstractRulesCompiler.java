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
package net.sourceforge.rules.compiler;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.util.DirectoryScanner;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public abstract class AbstractRulesCompiler implements RulesCompiler
{
	// Constants -------------------------------------------------------------

    protected static final String EOL = System.getProperty("line.separator");
    protected static final String PS = System.getProperty("path.separator");

	// Attributes ------------------------------------------------------------

	/**
	 * TODO 
	 */
	private String[] inputFileEndings;
	
	/**
	 * TODO
	 */
	private String outputFile;
	
	/**
	 * TODO
	 */
	private String outputFileEnding;

	/**
	 * TODO
	 */
	private RulesCompilerOutputStyle rulesCompilerOutputStyle;
	
	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param rulesCompilerOutputStyle
	 * @param inputFileEndings
	 * @param outputFile
	 * @param outputFileEnding
	 */
	protected AbstractRulesCompiler(
			RulesCompilerOutputStyle rulesCompilerOutputStyle,
			String[] inputFileEndings,
			String outputFile,
			String outputFileEnding) {
		
		this.rulesCompilerOutputStyle = rulesCompilerOutputStyle;
		this.inputFileEndings = inputFileEndings;
		this.outputFile = outputFile;
		this.outputFileEnding = outputFileEnding;
	}

	// RulesCompiler Implementation ------------------------------------------

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.compiler.RulesCompiler#canUpdateTarget(net.sourceforge.rules.compiler.RulesCompilerConfiguration)
	 */
	@Override
	public boolean canUpdateTarget(RulesCompilerConfiguration configuration)
	throws RulesCompilerException {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.compiler.RulesCompiler#getInputFileEndings(net.sourceforge.rules.compiler.RulesCompilerConfiguration)
	 */
	@Override
	public String[] getInputFileEndings(RulesCompilerConfiguration configuration)
	throws RulesCompilerException {
		return inputFileEndings;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.compiler.RulesCompiler#getOutputFile(net.sourceforge.rules.compiler.RulesCompilerConfiguration)
	 */
	@Override
	public String getOutputFile(RulesCompilerConfiguration configuration)
	throws RulesCompilerException {
		
		if (outputFile != null) {
			return outputFile;
		} else if (configuration.getOutputFileName() != null) {
			return configuration.getOutputFileName() + "." + getOutputFileEnding(configuration);
		} else {
			// TODO should we throw an exception here
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.compiler.RulesCompiler#getOutputFileEnding(net.sourceforge.rules.compiler.RulesCompilerConfiguration)
	 */
	@Override
	public String getOutputFileEnding(RulesCompilerConfiguration configuration)
	throws RulesCompilerException {
		return outputFileEnding;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.rules.compiler.RulesCompiler#getRulesCompilerOutputStyle()
	 */
	@Override
	public RulesCompilerOutputStyle getRulesCompilerOutputStyle() {
		return rulesCompilerOutputStyle;
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param pathElements
	 * @return
	 */
	protected String createPathString(List<String> pathElements) {
		
        StringBuilder sb = new StringBuilder();

        for (Iterator<String> it = pathElements.iterator(); it.hasNext(); ) {
            sb.append(it.next()).append(PS);
        }

        return sb.toString();
	}

	/**
	 * TODO
	 * 
	 * @param config
	 * @return
	 */
	protected String[] getSourceFiles(RulesCompilerConfiguration config) {
		
        Set<String> sources = new HashSet<String>();
        Set<String> sourceFiles = config.getSourceFiles();

        if (sourceFiles != null && !sourceFiles.isEmpty()) {
            for (Iterator<String> it = sourceFiles.iterator(); it.hasNext(); ) {
                File sourceFile = new File(it.next());
                sources.add(sourceFile.getAbsolutePath());
            }
        } else {
            for (Iterator<String> it = config.getSourceLocations().iterator(); it.hasNext(); ) {
                String sourceLocation = it.next();
                sources.addAll(getSourceFilesForSourceRoot(config, sourceLocation));
            }
        }

        String[] result;

        if (sources.isEmpty()) {
            result = new String[0];
        } else {
            result = (String[])sources.toArray(new String[sources.size()]);
        }

        return result;
	}

	/**
	 * TODO
	 * 
	 * @param config
	 * @param sourceLocation
	 * @return
	 */
	protected Set<String> getSourceFilesForSourceRoot(
			RulesCompilerConfiguration config,
			String sourceLocation) {
		
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(sourceLocation);

        Set<String> includes = config.getIncludes();

        if (includes != null && !includes.isEmpty()) {
            String[] inclStrs = includes.toArray(new String[includes.size()]);
            scanner.setIncludes(inclStrs);
        } else {
        	String[] inclStrs = new String[inputFileEndings.length];
        	int i = 0;
        	
        	for (String inputFileEnding : inputFileEndings) {
        		inclStrs[i++] = "**/*." + inputFileEnding;
        	}
        	
            scanner.setIncludes(inclStrs);
        }

        Set<String> excludes = config.getExcludes();

        if (excludes != null && !excludes.isEmpty()) {
            String[] exclStrs = excludes.toArray(new String[excludes.size()]);
            scanner.setIncludes(exclStrs);
        }

        scanner.scan();

        String[] sourceDirectorySources = scanner.getIncludedFiles();
        Set<String> sources = new HashSet<>();

        for (int j = 0; j < sourceDirectorySources.length; j++) {
            File f = new File(sourceLocation, sourceDirectorySources[j]);
            sources.add(f.getPath());
        }

        return sources;
	}

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
