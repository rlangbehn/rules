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
package net.sourceforge.rules.compiler.drools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.rules.compiler.AbstractRulesCompiler;
import net.sourceforge.rules.compiler.RulesCompilerConfiguration;
import net.sourceforge.rules.compiler.RulesCompilerError;
import net.sourceforge.rules.compiler.RulesCompilerException;
import net.sourceforge.rules.compiler.RulesCompilerOutputStyle;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * TODO
 * 
 * @plexus.component
 *   role="net.sourceforge.rules.compiler.RulesCompiler"
 *   role-hint="drools-compiler"
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class DroolsRulesCompiler extends AbstractRulesCompiler
{
	/**
	 * TODO
	 */
	public static final String[] INPUT_FILE_ENDINGS = new String[] {
		"brl", "csv", "drl", "dsl", "dslr", "rf", "rfm", "xls", "xml"
	};

	/**
	 * TODO
	 */
	public static final String OUTPUT_FILE_ENDING = "pkg";
	
	/**
	 * TODO
	 */
	public DroolsRulesCompiler() {
		super(
				RulesCompilerOutputStyle.ONE_OUTPUT_FILE_FOR_ALL_INPUT_FILES,
				INPUT_FILE_ENDINGS,
				null,
				OUTPUT_FILE_ENDING
		);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.compiler.RulesCompiler#compile(net.sourceforge.rules.compiler.RulesCompilerConfiguration)
	 */
	public List<RulesCompilerError> compile(
			RulesCompilerConfiguration configuration)
	throws RulesCompilerException {
		
        File destinationDir = new File(configuration.getOutputLocation());

        if (!destinationDir.exists()) {
        	destinationDir.mkdirs();
        }
        
        String[] sourceFiles = getSourceFiles(configuration);

        if (sourceFiles.length == 0) {
            return Collections.emptyList();
        }

        if ((getLogger() != null) && getLogger().isInfoEnabled()) {
			getLogger().info("compile(" + configuration + ")");
        	getLogger().info(
        			"Compiling " + sourceFiles.length + " " +
        			"rules file" + (sourceFiles.length == 1 ? "" : "s" ) +
        			" to " + destinationDir.getAbsolutePath()
        	);
        }
        
        String[] args = buildCompilerArguments(configuration, sourceFiles);
		List<RulesCompilerError> messages;
        
        if (configuration.isFork()) {
        	String executable = configuration.getExecutable();
        	
        	if (StringUtils.isEmpty(executable)) {
        		executable = "java"; //$NON-NLS-1$
        	}
        	
        	messages = compileOutOfProcess(
        			configuration,
        			executable,
        			args
        	);
        } else {
        	messages = compileInProcess(args);
        }
		
		return messages;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.compiler.RulesCompiler#createCommandLine(net.sourceforge.rules.compiler.RulesCompilerConfiguration)
	 */
	public String[] createCommandLine(
			RulesCompilerConfiguration configuration)
	throws RulesCompilerException {
		String[] sourceFiles = getSourceFiles(configuration);
		return buildCompilerArguments(configuration, sourceFiles);
	}
	
	/**
	 * TODO
	 *
	 * @param config
	 * @param sourceFiles
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String[] buildCompilerArguments(
			RulesCompilerConfiguration config,
			String[] sourceFiles) {
		
		List<String> args = new ArrayList<String>();

        // -------------------------------------------------------------------
        // Set the class path
        // -------------------------------------------------------------------
        List classpathEntries = config.getClasspathEntries();
        
		if (classpathEntries != null && !classpathEntries.isEmpty()) {
			args.add("-classpath"); //$NON-NLS-1$
			args.add(createPathString(classpathEntries));
		}

        // -------------------------------------------------------------------
        // Set output
        // -------------------------------------------------------------------
        File destinationDir = new File(config.getOutputLocation());

		args.add("-d"); //$NON-NLS-1$
		args.add(destinationDir.getAbsolutePath());

		if (config.getOutputFileName() != null) {
			args.add("-outputfile");
			args.add(config.getOutputFileName());
		}
		
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
            args.add("-verbose");
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

        return args.toArray(new String[args.size()]);
	}
	
	/**
	 * TODO
	 * 
	 * @param args
	 * @return
	 * @throws RulesCompilerException 
	 */
	@SuppressWarnings("unchecked")
	private List<RulesCompilerError> compileInProcess(String[] args)
	throws RulesCompilerException {
		
		StringWriter out = new StringWriter();
		List<RulesCompilerError> messages;
		Integer result;
		
		try {
			Class c = Class.forName("net.sourceforge.rules.compiler.drools.Main");
			Method compile = c.getMethod("compile", new Class[] {String[].class, PrintWriter.class});
			result = (Integer)compile.invoke(null, new Object[] {args, new PrintWriter(out)});
			messages = parseStream(new BufferedReader(new StringReader(out.toString())));
		} catch (ClassNotFoundException e) {
			throw new RulesCompilerException("Error while executing the compiler.", e);
		} catch (SecurityException e) {
			throw new RulesCompilerException("Error while executing the compiler.", e);
		} catch (NoSuchMethodException e) {
			throw new RulesCompilerException("Error while executing the compiler.", e);
		} catch (IllegalArgumentException e) {
			throw new RulesCompilerException("Error while executing the compiler.", e);
		} catch (IllegalAccessException e) {
			throw new RulesCompilerException("Error while executing the compiler.", e);
		} catch (InvocationTargetException e) {
			throw new RulesCompilerException("Error while executing the compiler.", e);
		} catch (IOException e) {
			throw new RulesCompilerException("Error while executing the compiler.", e);
		}

		if (result.intValue() != 0 && messages.isEmpty()) {
            // TODO: exception?
            messages.add(new RulesCompilerError(
            		"Failure executing droolsc, but could not parse the error:" +
            		EOL + out.toString(),
            		true));
		}
		
		return messages;
	}

	/**
	 * TODO
	 * 
	 * @param config
	 * @param executable
	 * @param args
	 * @return
	 * @throws RulesCompilerException
	 */
	private List<RulesCompilerError> compileOutOfProcess(
			RulesCompilerConfiguration config,
			String executable,
			String[] args)
	throws RulesCompilerException {

		Commandline cli = new Commandline();
		cli.setWorkingDirectory(config.getWorkingDirectory().getAbsolutePath());
		cli.setExecutable(executable);

		try {

	        List<?> classpathEntries = config.getClasspathEntries();
	        
			if (classpathEntries != null && !classpathEntries.isEmpty()) {
				cli.addArguments(new String[] {
						"-classpath",
						createPathString(classpathEntries)
				});
			}
			
			if (!StringUtils.isEmpty(config.getMaxmem())) {
				cli.addArguments(new String[] {"-Xmx" + config.getMaxmem()});
			}

			if (!StringUtils.isEmpty(config.getMeminitial())) {
				cli.addArguments(new String[] {"-Xms" + config.getMeminitial()});
			}

			if (config.isDebugRulesCompiler()) {
				cli.addArguments(new String[] {
						"-Xdebug",
						"-Xnoagent",
						"-Djava.compiler=NONE",
						"-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"
				});
			}

			cli.addArguments(new String[] {"net.sourceforge.rules.compiler.drools.Main"});

			File argumentsFile = createArgumentsFile(args);
			cli.addArguments(new String[] {"@" + argumentsFile.getCanonicalPath().replace(File.separatorChar, '/')});
			
		} catch (IOException e) {
			String s = "Error while creating file with droolsc arguments";
			throw new RulesCompilerException(s, e);
		}

		CommandLineUtils.StringStreamConsumer out = new CommandLineUtils.StringStreamConsumer();
		CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();
		List<RulesCompilerError> messages;
		int returnCode;

		try {
			returnCode = CommandLineUtils.executeCommandLine(cli, out, err);
			messages = parseStream(new BufferedReader(new StringReader(err.getOutput())));
		} catch (CommandLineException e) {
			String s = "Error while executing the external compiler"; //$NON-NLS-1$
			throw new RulesCompilerException(s, e);
		} catch (IOException e) {
			String s = "Error while executing the external compiler"; //$NON-NLS-1$
			throw new RulesCompilerException(s, e);
		}

		if (returnCode != 0 && messages.isEmpty()) {

			if (err.getOutput().length() == 0) {
				String s = "Unknown error trying to execute the external compiler: " + EOL + cli.toString();
				throw new RulesCompilerException(s);
			} else {
				messages.add(new RulesCompilerError(
						"Failure executing droolsc, but could not parse the error:" + //$NON-NLS-1$
						EOL + err.getOutput(),
						true
				));
			}
		}

		return messages;
	}

    /**
     * TODO
     * 
     * @param args
     * @return
     * @throws IOException
     */
    private File createArgumentsFile(String[] args) throws IOException {

    	PrintWriter writer = null;

    	try {
    		File tempFile = File.createTempFile(DroolsRulesCompiler.class.getName(), "arguments");
    		tempFile.deleteOnExit();

    		writer = new PrintWriter(new FileWriter(tempFile));

    		for (int i = 0; i < args.length; i++) {
    			String argValue = args[i].replace(File.separatorChar, '/');
    			writer.write("\"" + argValue + "\"");
    			writer.println();
    		}
    		
    		writer.flush();
    		
    		return tempFile;
    		
    	} finally {
    		if (writer != null) {
    			writer.close();
    		}
    	}
	}

	/**
	 * TODO
	 * 
	 * @param reader
	 * @return
	 * @throws IOException 
	 */
	private List<RulesCompilerError> parseStream(BufferedReader reader)
	throws IOException {
		
		List<RulesCompilerError> errors = new ArrayList<RulesCompilerError>();
		StringBuilder sb;
		String line;
		
		while (true) {
			sb = new StringBuilder();
			
			do {
				line = reader.readLine();
				
				if (line == null) {
					return errors;
				}
				
                // TODO: there should be a better way to parse these
                if (sb.length() == 0 && line.startsWith("error: ")) { //$NON-NLS-1$
                    errors.add(new RulesCompilerError(line, true));
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
	private boolean suppressEncoding(RulesCompilerConfiguration config) {
        return "1.3".equals(config.getCompilerVersion()); //$NON-NLS-1$
	}

	/**
	 * TODO
	 * 
	 * @param config
	 * @return
	 */
	private boolean suppressSource(RulesCompilerConfiguration config) {
        return "1.3".equals(config.getCompilerVersion()); //$NON-NLS-1$
	}
}
