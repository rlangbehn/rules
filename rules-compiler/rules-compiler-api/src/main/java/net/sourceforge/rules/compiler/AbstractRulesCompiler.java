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
import java.util.Random;
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
    protected static final String EOL = System.getProperty("line.separator"); //$NON-NLS-1$
    protected static final String PS = System.getProperty("path.separator"); //$NON-NLS-1$

    //get some non-crypto-grade randomness from various places.
    protected static Random rand = new Random(System.currentTimeMillis()
            + Runtime.getRuntime().freeMemory());

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
	 * 
	 * @param inputFileEndings
	 * @param outputFile
	 * @param outputFileEnding
	 */
	protected AbstractRulesCompiler(
			String[] inputFileEndings,
			String outputFile,
			String outputFileEnding) {
		
		this.inputFileEndings = inputFileEndings;
		this.outputFile = outputFile;
		this.outputFileEnding = outputFileEnding;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.compiler.RulesCompiler#canUpdateTarget(net.sourceforge.rules.compiler.RulesCompilerConfiguration)
	 */
	public boolean canUpdateTarget(RulesCompilerConfiguration configuration)
	throws RulesCompilerException {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.compiler.RulesCompiler#getInputFileEndings(net.sourceforge.rules.compiler.RulesCompilerConfiguration)
	 */
	public String[] getInputFileEndings(RulesCompilerConfiguration configuration)
	throws RulesCompilerException {
		return inputFileEndings;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.compiler.RulesCompiler#getOutputFile(net.sourceforge.rules.compiler.RulesCompilerConfiguration)
	 */
	public String getOutputFile(RulesCompilerConfiguration configuration)
	throws RulesCompilerException {
		return outputFile;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.compiler.RulesCompiler#getOutputFileEnding(net.sourceforge.rules.compiler.RulesCompilerConfiguration)
	 */
	public String getOutputFileEnding(RulesCompilerConfiguration configuration)
	throws RulesCompilerException {
		return outputFileEnding;
	}
	
	/**
	 * TODO
	 * 
	 * @param pathElements
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected String createPathString(List pathElements) {
        StringBuilder sb = new StringBuilder();

        for (Iterator it = pathElements.iterator(); it.hasNext(); ) {
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
	@SuppressWarnings("unchecked")
	protected String[] getSourceFiles(RulesCompilerConfiguration config) {
		
        Set<String> sources = new HashSet<String>();
        Set sourceFiles = config.getSourceFiles();

        if (sourceFiles != null && !sourceFiles.isEmpty()) {
            for (Iterator it = sourceFiles.iterator(); it.hasNext(); ) {
                File sourceFile = (File)it.next();
                sources.add(sourceFile.getAbsolutePath());
            }
        } else {
            for (Iterator it = config.getSourceLocations().iterator(); it.hasNext(); ) {
                String sourceLocation = (String)it.next();
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
	@SuppressWarnings("unchecked")
	protected Set getSourceFilesForSourceRoot(
			RulesCompilerConfiguration config,
			String sourceLocation) {
		
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(sourceLocation);

        Set includes = config.getIncludes();

        if (includes != null && !includes.isEmpty()) {
            String[] inclStrs = (String[])includes.toArray(new String[includes.size()]);
            scanner.setIncludes(inclStrs);
        } else {
        	String[] inclStrs = new String[inputFileEndings.length];
        	int i = 0;
        	
        	for (String inputFileEnding : inputFileEndings) {
        		inclStrs[i++] = "**/*" + inputFileEnding;
        	}
        	
            scanner.setIncludes(inclStrs);
        }

        Set excludes = config.getExcludes();

        if (excludes != null && !excludes.isEmpty()) {
            String[] exclStrs = (String[])excludes.toArray(new String[excludes.size()]);
            scanner.setIncludes(exclStrs);
        }

        scanner.scan();

        String[] sourceDirectorySources = scanner.getIncludedFiles();
        Set sources = new HashSet();

        for (int j = 0; j < sourceDirectorySources.length; j++) {
            File f = new File(sourceLocation, sourceDirectorySources[j]);
            sources.add(f.getPath());
        }

        return sources;
	}
}
