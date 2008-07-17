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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.CompilerError;
import org.codehaus.plexus.compiler.CompilerException;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SourceMapping;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public abstract class AbstractRulesCompilerMojo extends AbstractMojo
{
	// Constants -------------------------------------------------------------

    private static final String EOL = System.getProperty("line.separator"); //$NON-NLS-1$
    private static final String PS = System.getProperty("path.separator"); //$NON-NLS-1$

    //get some non-crypto-grade randomness from various places.
    private static Random rand = new Random(System.currentTimeMillis()
            + Runtime.getRuntime().freeMemory());

	// Attributes ------------------------------------------------------------

    /**
     * The directory to run the compiler from if fork is true.
     *
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     */
    private File basedir;

    /**
     * The target directory of the compiler if fork is true.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private File buildDirectory;

    /**
     * Sets the unformatted argument string to be passed to the compiler if fork is set to true.
     * <p>
     * This is because the list of valid arguments passed to a Java compiler
     * varies based on the compiler version.
     * </p>
     *
     * @parameter
     */
    private String compilerArgument;

    /**
     * Sets the arguments to be passed to the compiler (prepending a dash) if fork is set to true.
     * <p>
     * This is because the list of valid arguments passed to a Java compiler
     * varies based on the compiler version.
     * </p>
     *
     * @parameter
     */
    private Map<String, String> compilerArguments;

    /**
     * The compiler id of the compiler to use. See this
     * <a href="non-javac-compilers.html">guide</a> for more information.
     *
     * @parameter expression="${rules.compiler.compilerId}" default-value="rulesc"
     */
    private String compilerId;

    /**
     * Version of the compiler to use, ex. "1.3", "1.5", if fork is set to true.
     *
     * @parameter expression="${rules.compiler.compilerVersion}"
     */
    private String compilerVersion;

    /**
     * Set to true to include debugging information in the compiled class files.
     * The default value is true.
     *
     * @parameter expression="${rules.compiler.debug}" default-value="true"
     */
    private boolean debug;

    /**
     * Set to true to start the rulesc compiler in debugging mode if fork is set
     * to true too.
     * 
     * @parameter default-value="false"
     */
    private boolean debugRulesc;
    
    /**
     * The -encoding argument for the Java compiler.
     *
     * @parameter expression="${rules.compiler.encoding}"
     */
    private String encoding;

    /**
     * Sets the executable of the compiler to use when fork is true.
     *
     * @parameter expression="${rules.compiler.executable}"
     */
    private String executable;

    /**
     * Indicates whether the build will continue even if there
     * are compilation errors; defaults to true.
     *
     * @parameter expression="${drools.compiler.failOnError}" default-value="true"
     */
    private boolean failOnError = true;

    /**
     * Allows running the compiler in a separate process.
     * If "false" it uses the built in compiler, while if "true" it will use an executable.
     *
     * @parameter default-value="false"
     */
    private boolean fork;

    /**
     * Sets the maximum size, in megabytes, of the memory allocation pool, ex. "128", "128m"
     * if fork is set to true.
     *
     * @parameter expression="${rules.compiler.maxmem}"
     */
    private String maxmem;

    /**
     * Initial size, in megabytes, of the memory allocation pool, ex. "64", "64m"
     * if fork is set to true.
     *
     * @parameter expression="${rules.compiler.meminitial}"
     */
    private String meminitial;

    /**
     * Set to true to optimize the compiled code using the compiler's optimization methods.
     *
     * @parameter expression="${rules.compiler.optimize}" default-value="false"
     */
    private boolean optimize;

    /**
     * Sets the name of the output file when compiling a set of
     * sources to a single file.
     *
     * @parameter expression="${project.build.finalName}"
     */
    private String outputFileName;

    /**
     * Sets whether to show source locations where deprecated APIs are used.
     *
     * @parameter expression="${rules.compiler.showDeprecation}" default-value="false"
     */
    private boolean showDeprecation;

    /**
     * Set to true to show compilation warnings.
     *
     * @parameter expression="${rules.compiler.showWarnings}" default-value="false"
     */
    private boolean showWarnings;

    /**
     * The -source argument for the Java compiler.
     *
     * @parameter expression="${rules.compiler.source}"
     */
    private String source;

    /**
     * Sets the granularity in milliseconds of the last modification
     * date for testing whether a source needs recompilation.
     *
     * @parameter expression="${lastModGranularityMs}" default-value="0"
     */
    private int staleMillis;

    /**
     * The -target argument for the Java compiler.
     *
     * @parameter expression="${rules.compiler.target}"
     */
    private String target;

    /**
     * Set to true to show messages about what the compiler is doing.
     *
     * @parameter expression="${rules.compiler.verbose}" default-value="false"
     */
    private boolean verbose;

    // Static ----------------------------------------------------------------
    
    // Constructors ----------------------------------------------------------
    
    // Mojo implementation ---------------------------------------------------
    
	/* (non-Javadoc)
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute()
	throws MojoExecutionException, CompilationFailureException {

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        if (!getSourceDirectory().exists()) {
            getLog().info("No rules files to compile"); //$NON-NLS-1$
            return;
        }

        // -------------------------------------------------------------------
        // Create the compiler configuration
        // -------------------------------------------------------------------

        List compileSourceRoots = new ArrayList();
        compileSourceRoots.add(getSourceDirectory().getAbsolutePath());
        
        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        compilerConfiguration.setOutputLocation(getOutputDirectory().getAbsolutePath());
        compilerConfiguration.setClasspathEntries(getClasspathElements());
        compilerConfiguration.setSourceLocations(compileSourceRoots);
        compilerConfiguration.setOptimize(optimize);
        compilerConfiguration.setDebug(debug);
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
                    
                    if (!key.startsWith("-")) { //$NON-NLS-1$
                        key = "-" + key; //$NON-NLS-1$
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
                    getLog().info("Invalid value for meminitial '" + meminitial + "'. Ignoring this option."); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }

            if (!StringUtils.isEmpty(maxmem)) {
                String value = getMemoryValue(maxmem);

                if (value != null) {
                    compilerConfiguration.setMaxmem(value);
                } else {
                    getLog().info("Invalid value for maxmem '" + maxmem + "'. Ignoring this option."); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }

        compilerConfiguration.setExecutable(executable);
        compilerConfiguration.setWorkingDirectory(basedir);
        compilerConfiguration.setCompilerVersion(compilerVersion);
        compilerConfiguration.setBuildDirectory(buildDirectory);
        compilerConfiguration.setOutputFileName(outputFileName);

		SourceInclusionScanner sourceInclusionScanner = createSourceInclusionScanner(staleMillis);
		Set staleSources = computeStaleSources(compilerConfiguration, sourceInclusionScanner);
		compilerConfiguration.setSourceFiles(staleSources);

        if (staleSources.isEmpty()) {
            getLog().info("Nothing to compile - all rules files are up to date"); //$NON-NLS-1$
            return;
        }
        
		// -------------------------------------------------------------------
		// Dump configuration
		// -------------------------------------------------------------------

		if (getLog().isDebugEnabled()) {
			getLog().debug("Classpath:"); //$NON-NLS-1$

			List classpathEntries = compilerConfiguration.getClasspathEntries();
			
			for (Iterator it = classpathEntries.iterator(); it.hasNext(); ) {
				String classpathEntry = (String)it.next();
				getLog().debug(" " + classpathEntry); //$NON-NLS-1$
			}
			
			if (fork) {
				// TODO
			}
		}

        // -------------------------------------------------------------------
        // Compile!
        // -------------------------------------------------------------------

		List<CompilerError> messages;
		
        try {
			messages = compile(compilerConfiguration);
		} catch (Exception e) {
            // TODO: don't catch Exception
            throw new MojoExecutionException("Fatal error compiling", e); //$NON-NLS-1$
		}
		
		boolean compilationError = false;
		
		for (CompilerError message : messages) {
			if (message.isError()) {
				compilationError = true;
				break;
			}
		}
		
		if (compilationError && failOnError) {
            throw new CompilationFailureException(messages);
		} else {
			for (CompilerError message : messages) {
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
     * @return
     */
    protected abstract SourceInclusionScanner createSourceInclusionScanner(int staleMillis);

    /**
     * TODO
     * 
     * @param inputFileEnding
     * @return
     */
    protected abstract SourceInclusionScanner createSourceInclusionScanner(String inputFileEnding);

    /**
     * TODO
     * 
     * @return
     */
    protected abstract List<SourceMapping> createSourceMappings();
    
    // Private ---------------------------------------------------------------

	/**
	 * TODO
	 *
	 * @param config
	 * @param sourceFiles
	 * @return
	 */
	private String[] buildCompilerArguments(CompilerConfiguration config, String[] sourceFiles) {
		List<String> args = new ArrayList<String>();

        // -------------------------------------------------------------------
        //
        // -------------------------------------------------------------------
        if (!StringUtils.isEmpty(config.getMeminitial())) {
            args.add("-J-Xms" + config.getMeminitial()); //$NON-NLS-1$
        }

        if (!StringUtils.isEmpty(config.getMaxmem())) {
            args.add("-J-Xmx" + config.getMaxmem()); //$NON-NLS-1$
        }

        // -------------------------------------------------------------------
        // Set the class path
        // -------------------------------------------------------------------
        List classpathEntries = config.getClasspathEntries();
        
		if (classpathEntries != null && !classpathEntries.isEmpty()) {
			args.add("-classpath"); //$NON-NLS-1$
			args.add(createPathString(classpathEntries));
		}

        // -------------------------------------------------------------------
        //
        // -------------------------------------------------------------------
		if (fork && debugRulesc) {
			args.add("-Xdebug"); //$NON-NLS-1$
			args.add("-Xnoagent"); //$NON-NLS-1$
			args.add("-Djava.compiler=NONE"); //$NON-NLS-1$
			args.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"); //$NON-NLS-1$
		}
		
        // -------------------------------------------------------------------
        //
        // -------------------------------------------------------------------
		if (fork) {
			args.add("net.sourceforge.rules.tools.rulesc.Main"); //$NON-NLS-1$
		}
		
        // -------------------------------------------------------------------
        // Set output
        // -------------------------------------------------------------------
        File destinationDir = new File(config.getOutputLocation());

		args.add("-d"); //$NON-NLS-1$
		args.add(destinationDir.getAbsolutePath());

        // -------------------------------------------------------------------
        // Set the source paths
        // -------------------------------------------------------------------
        List sourceLocations = config.getSourceLocations();
        if (sourceLocations != null && !sourceLocations.isEmpty() && (sourceFiles.length == 0)) {
            args.add("-sourcepath"); //$NON-NLS-1$
            args.add(createPathString(sourceLocations));
        }

        for (int i = 0; i < sourceFiles.length; i++) {
            args.add(sourceFiles[i]);
        }

        // -------------------------------------------------------------------
        //
        // -------------------------------------------------------------------
        if (config.isOptimize()) {
            //args.add("-O");
        }

        if (config.isDebug()) {
            //args.add("-g");
        }

        if (config.isVerbose()) {
            args.add("-verbose"); //$NON-NLS-1$
        }

        if (config.isShowDeprecation()) {
            //args.add("-deprecation");

            // This is required to actually display the deprecation messages
            config.setShowWarnings(true);
        }

        if (!config.isShowWarnings()) {
            //args.add("-nowarn");
        }

        // TODO: this could be much improved
        if (StringUtils.isEmpty(config.getTargetVersion())) {
            // Required, or it defaults to the target of your JDK (eg 1.5)
            //args.add("-target");
            //args.add("1.1");
        } else {
            //args.add("-target");
            //args.add(config.getTargetVersion());
        }

        if (!suppressSource(config) && StringUtils.isEmpty(config.getSourceVersion())) {
            // If omitted, later JDKs complain about a 1.1 target
            //args.add("-source");
            //args.add("1.3");
        } else if (!suppressSource(config)) {
            //args.add("-source");
            //args.add(config.getSourceVersion());
        }

        if (!suppressEncoding(config) && !StringUtils.isEmpty(config.getSourceEncoding())) {
            //args.add("-encoding");
            //args.add(config.getSourceEncoding());
        }

        for (Iterator it = config.getCustomCompilerArguments().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            String key = (String) entry.getKey();

            if (StringUtils.isEmpty(key)) {
                continue;
            }

            args.add(key);

            String value = (String)entry.getValue();

            if (StringUtils.isEmpty(value)) {
                continue;
            }

            args.add(value);
        }

        return (String[])args.toArray(new String[args.size()]);
	}

	/**
	 * TODO
	 * 
	 * @param config
	 * @return
	 * @throws CompilerException
	 */
	private List<CompilerError> compile(CompilerConfiguration config)
	throws CompilerException {
		
        File destinationDir = new File(config.getOutputLocation());

        if (!destinationDir.exists()) {
        	destinationDir.mkdirs();
        }
        
        String[] sourceFiles = getSourceFiles(config);

        if (sourceFiles.length == 0) {
            return Collections.EMPTY_LIST;
        }

        System.out.println("Compiling " + sourceFiles.length + " " + //$NON-NLS-1$ //$NON-NLS-2$
                "source file" + (sourceFiles.length == 1 ? "" : "s" ) + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                " to " + getOutputDirectory().getAbsolutePath()); //$NON-NLS-1$
        
        String[] args = buildCompilerArguments(config, sourceFiles);
		List<CompilerError> messages;
        
        if (fork) {
        	String executable = config.getExecutable();
        	
        	if (StringUtils.isEmpty(executable)) {
        		executable = "java"; //$NON-NLS-1$
        	}
        	
        	messages = compileOutOfProcess(config.getWorkingDirectory(), executable, args);
        } else {
        	messages = compileInProcess(args);
        }
		
		return messages;
	}

	/**
	 * TODO
	 * 
	 * @param args
	 * @return
	 * @throws CompilerException 
	 */
	private List<CompilerError> compileInProcess(String[] args)
	throws CompilerException {
		
		Class c;
		Integer result;
		List<CompilerError> messages;
		StringWriter out = new StringWriter();
		
		try {
			c = Class.forName("net.sourceforge.rules.tools.rulesc.Main"); //$NON-NLS-1$
			Method compile = c.getMethod("compile", new Class[] {String[].class, PrintWriter.class}); //$NON-NLS-1$
			result = (Integer)compile.invoke(null, new Object[] {args, new PrintWriter(out)});
			messages = parseStream(new BufferedReader(new StringReader(out.toString())));
		} catch (ClassNotFoundException e) {
			throw new CompilerException("Error while executing the compiler.", e); //$NON-NLS-1$
		} catch (SecurityException e) {
			throw new CompilerException("Error while executing the compiler.", e); //$NON-NLS-1$
		} catch (NoSuchMethodException e) {
			throw new CompilerException("Error while executing the compiler.", e); //$NON-NLS-1$
		} catch (IllegalArgumentException e) {
			throw new CompilerException("Error while executing the compiler.", e); //$NON-NLS-1$
		} catch (IllegalAccessException e) {
			throw new CompilerException("Error while executing the compiler.", e); //$NON-NLS-1$
		} catch (InvocationTargetException e) {
			throw new CompilerException("Error while executing the compiler.", e); //$NON-NLS-1$
		} catch (IOException e) {
			throw new CompilerException("Error while executing the compiler.", e); //$NON-NLS-1$
		}

		if (result.intValue() != 0 && messages.isEmpty()) {
            // TODO: exception?
            messages.add(new CompilerError(
            		"Failure executing rulesc, but could not parse the error:" + //$NON-NLS-1$
            		EOL + out.toString(),
            		true));
		}
		
		return messages;
	}

	/**
	 * TODO
	 * 
	 * @param basedir
	 * @param executable
	 * @param args
	 * @return
	 * @throws CompilerException
	 */
	private List<CompilerError> compileOutOfProcess(
			File workingDirectory,
			String executable,
			String[] args)
	throws CompilerException {

        String[] commandArray = null;
        boolean quoteFiles = true;
		int firstFileName = 0;
		File tmpFile = null;

		try {
            /*
             * Many system have been reported to get into trouble with
             * long command lines - no, not only Windows ;-).
             *
             * POSIX seems to define a lower limit of 4k, so use a temporary
             * file if the total length of the command line exceeds this limit.
             */
			if (Commandline.toString(args).length() > 4096) {
				PrintWriter out = null;
				
				try {
					tmpFile = createTempFile("files", "", null);  //$NON-NLS-1$//$NON-NLS-2$
					tmpFile.deleteOnExit();
                    out = new PrintWriter(new FileWriter(tmpFile));
                    
                    for (int i = firstFileName; i < args.length; i++) {
                        if (quoteFiles && args[i].indexOf(" ") > -1) { //$NON-NLS-1$
                            args[i] = args[i].replace(File.separatorChar, '/');
                            out.println("\"" + args[i] + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                        } else {
                            out.println(args[i]);
                        }
                    }
                    
                    out.flush();
                    commandArray = new String[firstFileName + 1];
                    System.arraycopy(args, 0, commandArray, 0, firstFileName);
                    commandArray[firstFileName] = "@" + tmpFile; //$NON-NLS-1$
				} catch (IOException e) {
					String s = "Error while creating argument file"; //$NON-NLS-1$
					throw new CompilerException(s, e);
				} finally {
					if (out != null) {
						out.close();
					}
				}
			} else {
				commandArray = args;
			}
			
			Commandline cli = new Commandline();
			cli.setWorkingDirectory(workingDirectory.getAbsolutePath());
			cli.setExecutable(executable);
			cli.addArguments(args);
			
			CommandLineUtils.StringStreamConsumer out = new CommandLineUtils.StringStreamConsumer();
			CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();
			List<CompilerError> messages;
			int returnCode;

			try {
				returnCode = CommandLineUtils.executeCommandLine(cli, out, err);
				messages = parseStream(new BufferedReader(new StringReader(err.getOutput())));
			} catch (CommandLineException e) {
				String s = "Error while executing the external compiler"; //$NON-NLS-1$
				throw new CompilerException(s, e);
			} catch (IOException e) {
				String s = "Error while executing the external compiler"; //$NON-NLS-1$
				throw new CompilerException(s, e);
			}
			
	        if (returnCode != 0 && messages.isEmpty()) {
	            // TODO: exception?
	            messages.add(new CompilerError(
	            		"Failure executing rulesc, but could not parse the error:" + //$NON-NLS-1$
	            		EOL + err.getOutput(),
	            		true));
	        }

			return messages;
			
		} finally {
			if (tmpFile != null) {
				tmpFile.delete();
			}
		}
	}

	/**
	 * TODO
	 * 
	 * @param config
	 * @param scanner
	 * @return
	 * @throws MojoExecutionException
	 */
    private Set computeStaleSources(CompilerConfiguration config, SourceInclusionScanner scanner)
    throws MojoExecutionException {
    	
        List<SourceMapping> sourceMappings = createSourceMappings();
        File outputDirectory = getOutputDirectory();

        for (SourceMapping sourceMapping : sourceMappings) {
        	scanner.addSourceMapping(sourceMapping);
        }
        
        Set staleSources = new HashSet();
        File sourceDirectory = getSourceDirectory();
        
        try {
			staleSources.addAll(scanner.getIncludedSources(sourceDirectory, outputDirectory));
		} catch (InclusionScanException e) {
            throw new MojoExecutionException(
                    "Error scanning source directory: \'" + //$NON-NLS-1$
                    sourceDirectory + "\' " + //$NON-NLS-1$
                    "for stale rules files to recompile.", e); //$NON-NLS-1$
		}

        return staleSources;
    }

	/**
	 * TODO
	 * 
	 * @param pathElements
	 * @return
	 */
	private String createPathString(List pathElements) {
        StringBuffer sb = new StringBuffer();

        for (Iterator it = pathElements.iterator(); it.hasNext(); ) {
            sb.append(it.next()).append( PS );
        }

        return sb.toString();
	}

    /**
     * Create a temporary file in a given directory.
     *
     * <p>The file denoted by the returned abstract pathname did not
     * exist before this method was invoked, any subsequent invocation
     * of this method will yield a different file name.</p>
     * <p>
     * The filename is prefixNNNNNsuffix where NNNN is a random number.
     * </p>
     * <p>This method is different from File.createTempFile() of JDK 1.2
     * as it doesn't create the file itself.  It uses the location pointed
     * to by java.io.tmpdir when the parentDir attribute is null.</p>
     *
     * @param prefix prefix before the random number.
     * @param suffix file extension; include the '.'.
     * @param parentDir Directory to create the temporary file in;
     * java.io.tmpdir used if not specified.
     *
     * @return a File reference to the new temporary file.
     * @since Ant 1.5
     */
    public File createTempFile(String prefix, String suffix, File parentDir) {
        File result = null;
        String parent = (parentDir == null)
            ? System.getProperty("java.io.tmpdir") //$NON-NLS-1$
            : parentDir.getPath();

        DecimalFormat fmt = new DecimalFormat("#####"); //$NON-NLS-1$
        synchronized (rand) {
            do {
                result = new File(parent,
                                  prefix + fmt.format(Math.abs(rand.nextInt()))
                                  + suffix);
            } while (result.exists());
        }
        return result;
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
            value = setting + "m"; //$NON-NLS-1$
        } else {
            if ((isDigits(setting.substring(0, setting.length() - 1))) &&
                (setting.toLowerCase().endsWith("m"))) { //$NON-NLS-1$
                value = setting;
            }
        }
        
        return value;
	}

	/**
	 * TODO
	 * 
	 * @param config
	 * @return
	 */
	private String[] getSourceFiles(CompilerConfiguration config) {
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
	private Set getSourceFilesForSourceRoot(
			CompilerConfiguration config,
			String sourceLocation) {
		
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(sourceLocation);

        Set includes = config.getIncludes();

        if (includes != null && !includes.isEmpty()) {
            String[] inclStrs = (String[])includes.toArray(new String[includes.size()]);
            scanner.setIncludes(inclStrs);
        } else {
        	String[] inclStrs = new String[] {
        			"**/*.csv", //$NON-NLS-1$
        			"**/*.drl", //$NON-NLS-1$
        			"**/*.xls", //$NON-NLS-1$
        			"**/*.xml" //$NON-NLS-1$
        	};
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

	/**
	 * TODO
	 * 
	 * @param reader
	 * @return
	 * @throws IOException 
	 */
	private List<CompilerError> parseStream(BufferedReader reader)
	throws IOException {
		
		List<CompilerError> errors = new ArrayList<CompilerError>();
		String line;
		StringBuffer sb;
		
		while (true) {
			sb = new StringBuffer();
			
			do {
				line = reader.readLine();
				
				if (line == null) {
					return errors;
				}
				
                // TODO: there should be a better way to parse these
                if (sb.length() == 0 && line.startsWith("error: ")) { //$NON-NLS-1$
                    errors.add(new CompilerError(line, true));
                } else if (sb.length() == 0 && line.startsWith("Note: ")) { //$NON-NLS-1$
                    // skip this one - it is JDK 1.5 telling us that the interface is deprecated.
                } else {
                    sb.append(line);
                    sb.append(EOL);
                }
			} while (!line.endsWith("^")); //$NON-NLS-1$
		}
	}

	/**
	 * TODO
	 * 
	 * @param config
	 * @return
	 */
	private boolean suppressEncoding(CompilerConfiguration config) {
        return "1.3".equals(config.getCompilerVersion()); //$NON-NLS-1$
	}

	/**
	 * TODO
	 * 
	 * @param config
	 * @return
	 */
	private boolean suppressSource(CompilerConfiguration config) {
        return "1.3".equals(config.getCompilerVersion()); //$NON-NLS-1$
	}
	  
	// Inner classes ---------------------------------------------------------
}
