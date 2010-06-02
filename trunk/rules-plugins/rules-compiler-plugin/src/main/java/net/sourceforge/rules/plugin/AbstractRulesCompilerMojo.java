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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.rules.compiler.RulesCompiler;
import net.sourceforge.rules.compiler.RulesCompilerConfiguration;
import net.sourceforge.rules.compiler.RulesCompilerError;
import net.sourceforge.rules.compiler.RulesCompilerException;
import net.sourceforge.rules.compiler.RulesCompilerOutputStyle;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SingleTargetSourceMapping;
import org.codehaus.plexus.compiler.util.scan.mapping.SourceMapping;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.codehaus.plexus.util.StringUtils;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public abstract class AbstractRulesCompilerMojo extends AbstractMojo
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

    /**
     * The directory to run the rules compiler from if fork is true.
     *
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     */
    private File basedir;

    /**
     * The target directory of the rules compiler if fork is true.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private File buildDirectory;

    /**
     * Sets the unformatted argument string to be passed to the rules compiler if fork is set to true.
     * <p>
     * This is because the list of valid arguments passed to a rules compiler
     * varies based on the concrete rules compiler and rules compiler version.
     * </p>
     *
     * @parameter
     */
    private String compilerArgument;

    /**
     * Sets the arguments to be passed to the rules compiler (prepending a dash) if fork is set to true.
     * <p>
     * This is because the list of valid arguments passed to a rules compiler
     * varies based on the concrete rules compiler and rules compiler version.
     * </p>
     *
     * @parameter
     */
    private Map<String, String> compilerArguments;

    /**
     * Set to true to include debugging information in the compiled rules files.
     * The default value is true.
     *
     * @parameter expression="${rules-compiler.debug}" default-value="true"
     */
    private boolean debug;

    /**
     * Set to true to start the rules compiler in debugging mode if fork is set
     * to true too.
     * 
     * @parameter expression="${rules-compiler.debugRulesCompiler}" default-value="false"
     */
    private boolean debugRulesCompiler;
    
    /**
     * The encoding argument for the rules compiler.
     *
     * @parameter expression="${rules-compiler.encoding}" default-value="${project.build.sourceEncoding}"
     */
    private String encoding;

    /**
     * Sets the executable of the rules compiler to use when fork is true.
     *
     * @parameter expression="${rules-compiler.executable}"
     */
    private String executable;

    /**
     * Indicates whether the build will continue even if there
     * are rules compilation errors; defaults to true.
     *
     * @parameter expression="${rules-compiler.failOnError}" default-value="true"
     */
    private boolean failOnError = true;

    /**
     * Allows running the rules compiler in a separate process.
     * If "false" it uses the built in rules compiler, while if "true" it will use an executable.
     *
     * @parameter expression="${rules-compiler.fork}" default-value="false"
     */
    private boolean fork;

    /**
     * Sets the maximum size, in megabytes, of the memory allocation pool, ex. "128", "128m"
     * if fork is set to true.
     *
     * @parameter expression="${rules-compiler.maxmem}"
     */
    private String maxmem;

    /**
     * Initial size, in megabytes, of the memory allocation pool, ex. "64", "64m"
     * if fork is set to true.
     *
     * @parameter expression="${rules-compiler.meminitial}"
     */
    private String meminitial;

    /**
     * Set to true to optimize the compiled rules using the rules compiler's optimization methods.
     *
     * @parameter expression="${rules-compiler.optimize}" default-value="false"
     */
    private boolean optimize;

    /**
     * Sets the name of the output file when compiling a set of
     * rules to a single file.
     *
     * @parameter expression="${project.build.finalName}"
     */
    private String outputFileName;

    /**
     * Rules compiler component.
     * 
     * @component
     */
    private RulesCompiler rulesCompiler;
    
    /**
     * Version of the rules compiler to use.
     *
     * @parameter expression="${rules-compiler.rulesCompilerVersion}"
     */
    private String rulesCompilerVersion;

    /**
     * Sets whether to show source locations where deprecated APIs are used.
     *
     * @parameter expression="${rules-compiler.showDeprecation}" default-value="false"
     */
    private boolean showDeprecation;

    /**
     * Set to true to show rules compilation warnings.
     *
     * @parameter expression="${rules-compiler.showWarnings}" default-value="false"
     */
    private boolean showWarnings;

    /**
     * The source argument for the rules compiler.
     *
     * @parameter expression="${rules-compiler.source}"
     */
    private String source;

    /**
     * Sets the granularity in milliseconds of the last modification
     * date for testing whether a source needs recompilation.
     *
     * @parameter expression="${rules-compiler.lastModGranularityMs}" default-value="0"
     */
    private int staleMillis;

    /**
     * The target argument for the rules compiler.
     *
     * @parameter expression="${rules-compiler.target}"
     */
    private String target;

    /**
     * Set to true to show messages about what the rules compiler is doing.
     *
     * @parameter expression="${rules-compiler.verbose}" default-value="false"
     */
    private boolean verbose;

    // Static ----------------------------------------------------------------
    
    // Constructors ----------------------------------------------------------
    
    // AbstractMojo Overrides ------------------------------------------------
    
	/* (non-Javadoc)
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@SuppressWarnings("unchecked")
	public void execute()
	throws MojoExecutionException, CompilationFailureException {

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        if (!getSourceDirectory().exists()) {
            getLog().info("No rules files to compile");
            return;
        }

        // -------------------------------------------------------------------
        // Create the compiler configuration
        // -------------------------------------------------------------------

        List compileSourceRoots = new ArrayList();
        compileSourceRoots.add(getSourceDirectory().getAbsolutePath());
        
        RulesCompilerConfiguration compilerConfiguration = new RulesCompilerConfiguration();
        compilerConfiguration.setOutputLocation(getOutputDirectory().getAbsolutePath());
        compilerConfiguration.setClasspathEntries(getClasspathElements());
        compilerConfiguration.setSourceLocations(compileSourceRoots);
        compilerConfiguration.setOptimize(optimize);
        compilerConfiguration.setDebug(debug);
        compilerConfiguration.setDebugRulesCompiler(debugRulesCompiler);
        compilerConfiguration.setVerbose(verbose);
        compilerConfiguration.setShowWarnings(showWarnings);
        compilerConfiguration.setShowDeprecation(showDeprecation);
        compilerConfiguration.setSourceVersion(source);
        compilerConfiguration.setTargetVersion(target);
        compilerConfiguration.setSourceEncoding(encoding);

        List pluginClasspathEntries = getPluginClasspathElements();
        
        for (Iterator it = pluginClasspathEntries.iterator(); it.hasNext(); ) {
        	Artifact artifact = (Artifact)it.next();
        	File artifactFile = artifact.getFile();
        	compilerConfiguration.addClasspathEntry(artifactFile.getAbsolutePath());
        }
        
        if ((compilerArguments != null) || (compilerArgument != null)) {
            LinkedHashMap<String, String> cplrArgsCopy = new LinkedHashMap<String, String>();
            
            if (compilerArguments != null) {
                for (Iterator i = compilerArguments.entrySet().iterator(); i.hasNext(); ) {
                    Map.Entry me = (Map.Entry)i.next();
                    String key = (String)me.getKey();
                    String value = (String)me.getValue();
                    
                    if (!key.startsWith("-")) {
                        key = "-" + key;
                    }
                    
                    cplrArgsCopy.put(key, value);
                }
            }
            
            if (!StringUtils.isEmpty(compilerArgument)) {
                cplrArgsCopy.put(compilerArgument, null);
            }
            
            compilerConfiguration.setCustomCompilerArguments(cplrArgsCopy);
        }

        compilerConfiguration.setFork(fork);

        if (fork) {
            if (!StringUtils.isEmpty(meminitial)) {
                String value = getMemoryValue(meminitial);

                if (value != null) {
                    compilerConfiguration.setMeminitial(value);
                } else {
                    getLog().info("Invalid value for meminitial '" + meminitial + "'. Ignoring this option.");
                }
            }

            if (!StringUtils.isEmpty(maxmem)) {
                String value = getMemoryValue(maxmem);

                if (value != null) {
                    compilerConfiguration.setMaxmem(value);
                } else {
                    getLog().info("Invalid value for maxmem '" + maxmem + "'. Ignoring this option.");
                }
            }
        }

        compilerConfiguration.setExecutable(executable);
        compilerConfiguration.setWorkingDirectory(basedir);
        compilerConfiguration.setCompilerVersion(rulesCompilerVersion);
        compilerConfiguration.setBuildDirectory(buildDirectory);
        compilerConfiguration.setOutputFileName(outputFileName);

        String[] inputFileEndings;
        
		try {
			inputFileEndings = rulesCompiler.getInputFileEndings(compilerConfiguration);
		} catch (RulesCompilerException e) {
			String s = "Error while retrieving input file endings";
            throw new MojoExecutionException(s, e);
		}
		
		SourceInclusionScanner sourceInclusionScanner = createSourceInclusionScanner(staleMillis, inputFileEndings);
		Set staleSources = computeStaleSources(compilerConfiguration, rulesCompiler, sourceInclusionScanner);
		compilerConfiguration.setSourceFiles(staleSources);

        if (staleSources.isEmpty()) {
            getLog().info("Nothing to compile - all rules files are up to date");
            return;
        }
        
		// -------------------------------------------------------------------
		// Dump configuration
		// -------------------------------------------------------------------

		if (getLog().isDebugEnabled()) {
			getLog().debug("Classpath:");

			List classpathEntries = compilerConfiguration.getClasspathEntries();
			
			for (Iterator it = classpathEntries.iterator(); it.hasNext(); ) {
				String classpathEntry = (String)it.next();
				getLog().debug(" " + classpathEntry);
			}
			
			if (fork) {
				// TODO
			}
		}

        // -------------------------------------------------------------------
        // Compile!
        // -------------------------------------------------------------------

		List<RulesCompilerError> messages;
		
        try {
			messages = rulesCompiler.compile(compilerConfiguration);
		} catch (Exception e) {
            // TODO: don't catch Exception
            throw new MojoExecutionException("Fatal error compiling", e);
		}
		
		boolean compilationError = false;
		
		for (RulesCompilerError message : messages) {
			if (message.isError()) {
				compilationError = true;
				break;
			}
		}
		
		if (compilationError && failOnError) {
            getLog().info("-------------------------------------------------------------");
            getLog().error("RULES COMPILATION ERROR : ");
            getLog().info("-------------------------------------------------------------");

            if (messages != null) {
                for (RulesCompilerError message : messages) {
                    getLog().error(message.toString());
                }
                getLog().info(messages.size() + ((messages.size() > 1) ? " errors " : "error"));
                getLog().info("-------------------------------------------------------------");
            }
            
            throw new CompilationFailureException(messages);
		} else {
			for (RulesCompilerError message : messages) {
				getLog().warn(message.toString());
			}
		}
	}

    // Package protected -----------------------------------------------------
    
    // Protected -------------------------------------------------------------

	/**
     * TODO
     * 
     * @return
     */
    protected abstract List<String> getClasspathElements();

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
    protected abstract List<String> getPluginClasspathElements();
    
    /**
     * TODO
     * 
     * @return
     */
    protected abstract File getSourceDirectory();

    /**
     * TODO
     * 
     * @param staleMillis
     * @param inputFileEndings
     * @return
     */
    protected abstract SourceInclusionScanner createSourceInclusionScanner(
    		int staleMillis, String[] inputFileEndings);

    /**
     * TODO
     * 
     * @param inputFileEndings
     * @return
     */
    protected abstract SourceInclusionScanner createSourceInclusionScanner(
    		String[] inputFileEndings);

    // Private ---------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param config
	 * @param rulesCompiler
	 * @param sourceInclusionScanner
	 * @return
	 * @throws MojoExecutionException
	 */
    @SuppressWarnings("unchecked")
	private Set<File> computeStaleSources(
    		RulesCompilerConfiguration config,
    		RulesCompiler rulesCompiler,
    		SourceInclusionScanner sourceInclusionScanner)
    throws MojoExecutionException {

    	RulesCompilerOutputStyle outputStyle = rulesCompiler.getRulesCompilerOutputStyle();
        List<SourceMapping> sourceMappings = new ArrayList<SourceMapping>();
        String[] inputFileEndings;
        File outputDirectory;
        
		try {
			inputFileEndings = rulesCompiler.getInputFileEndings(config);
		} catch (RulesCompilerException e) {
			String s = "Error while retrieving input file endings";
            throw new MojoExecutionException(s, e);
		}
		
        if (outputStyle == RulesCompilerOutputStyle.ONE_OUTPUT_FILE_FOR_ALL_INPUT_FILES) {
        	
			String outputFile;
			
        	try {
				outputFile = rulesCompiler.getOutputFile(config);
			} catch (RulesCompilerException e) {
				String s = "Error while retrieving output file";
	            throw new MojoExecutionException(s, e);
			}
        	
    		for (String inputFileEnding : inputFileEndings) {
    			sourceMappings.add(new SingleTargetSourceMapping(
    					inputFileEnding, outputFile
    			));
    		}

        	outputDirectory = buildDirectory;
        	
        } else if (outputStyle == RulesCompilerOutputStyle.ONE_OUTPUT_FILE_PER_INPUT_FILE) {
        	
            String outputFileEnding;
            
			try {
				outputFileEnding = rulesCompiler.getOutputFileEnding(config);
			} catch (RulesCompilerException e) {
				String s = "Error while retrieving output file ending";
	            throw new MojoExecutionException(s, e);
			}
            
    		for (String inputFileEnding : inputFileEndings) {
    			sourceMappings.add(new SuffixMapping(
    					inputFileEnding, outputFileEnding
    			));
    		}
    		
        	outputDirectory = getOutputDirectory();
        	
        } else {
        	String s = "Unknown compiler output style: '" + outputStyle + "'.";
        	throw new MojoExecutionException(s);
        }
        
        for (SourceMapping sourceMapping : sourceMappings) {
        	sourceInclusionScanner.addSourceMapping(sourceMapping);
        }
        
        Set<File> staleSources = new HashSet<File>();
        File sourceDirectory = getSourceDirectory();
        
        try {
			staleSources.addAll(sourceInclusionScanner.getIncludedSources(
					sourceDirectory,
					outputDirectory
			));
		} catch (InclusionScanException e) {
            throw new MojoExecutionException(
                    "Error scanning source directory: \'" +
                    sourceDirectory + "\' " +
                    "for stale rules files to recompile.", e);
		}

        return staleSources;
    }

	/**
	 * TODO
	 * 
	 * @param setting
	 */
	private String getMemoryValue(String setting) {
        String value = null;

        // Allow '128' or '128m'
        if (isDigits(setting)) {
            value = setting + "m";
        } else {
            if ((isDigits(setting.substring(0, setting.length() - 1))) &&
                (setting.toLowerCase().endsWith("m"))) {
                value = setting;
            }
        }
        
        return value;
	}

	/**
	 * TODO
	 * 
	 * @param string
	 * @return
	 */
	private boolean isDigits(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (!Character.isDigit(string.charAt(i))) {
                return false;
            }
        }
        
        return true;
	}

	// Inner classes ---------------------------------------------------------
}
