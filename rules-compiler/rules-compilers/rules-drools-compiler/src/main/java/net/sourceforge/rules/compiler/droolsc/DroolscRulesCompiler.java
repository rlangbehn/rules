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
package net.sourceforge.rules.compiler.droolsc;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

import net.sourceforge.rules.compiler.AbstractRulesCompiler;
import net.sourceforge.rules.compiler.RulesCompilerConfiguration;
import net.sourceforge.rules.compiler.RulesCompilerError;
import net.sourceforge.rules.compiler.RulesCompilerException;

/**
 * TODO
 * 
 * @plexus.component
 *   role="net.sourceforge.rules.compiler.RulesCompiler"
 *   role-hint="droolsc"
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class DroolscRulesCompiler extends AbstractRulesCompiler
{
	/**
	 * TODO
	 */
	public static final String[] INPUT_FILE_ENDINGS = new String[] {
		"brl", "csv", "drl", "dslr", "rf", "rfm", "xls", "xml"
	};

	/**
	 * TODO
	 */
	public static final String OUTPUT_FILE_ENDING = "res";
	
	/**
	 * TODO
	 */
	public DroolscRulesCompiler() {
		super(INPUT_FILE_ENDINGS, null, OUTPUT_FILE_ENDING);
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

        System.out.println("Compiling " + sourceFiles.length + " " + //$NON-NLS-1$ //$NON-NLS-2$
                "source file" + (sourceFiles.length == 1 ? "" : "s" ) + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                " to " + destinationDir.getAbsolutePath()); //$NON-NLS-1$
        
        String[] args = buildCompilerArguments(configuration, sourceFiles);
		List<RulesCompilerError> messages;
        
        if (configuration.isFork()) {
        	String executable = configuration.getExecutable();
        	
        	if (StringUtils.isEmpty(executable)) {
        		executable = "java"; //$NON-NLS-1$
        	}
        	
        	messages = compileOutOfProcess(
        			configuration.getWorkingDirectory(),
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
		if (config.isFork() && config.isDebugRulesCompiler()) {
			args.add("-Xdebug"); //$NON-NLS-1$
			args.add("-Xnoagent"); //$NON-NLS-1$
			args.add("-Djava.compiler=NONE"); //$NON-NLS-1$
			args.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"); //$NON-NLS-1$
		}
		
        // -------------------------------------------------------------------
        //
        // -------------------------------------------------------------------
		if (config.isFork()) {
			args.add("net.sourceforge.rules.compiler.droolsc.Main"); //$NON-NLS-1$
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
	 * @param args
	 * @return
	 * @throws RulesCompilerException 
	 */
	@SuppressWarnings("unchecked")
	private List<RulesCompilerError> compileInProcess(String[] args)
	throws RulesCompilerException {
		
		Class c;
		Integer result;
		List<RulesCompilerError> messages;
		StringWriter out = new StringWriter();
		
		try {
			c = Class.forName("net.sourceforge.rules.compiler.droolsc.Main"); //$NON-NLS-1$
			Method compile = c.getMethod("compile", new Class[] {String[].class, PrintWriter.class}); //$NON-NLS-1$
			result = (Integer)compile.invoke(null, new Object[] {args, new PrintWriter(out)});
			messages = parseStream(new BufferedReader(new StringReader(out.toString())));
		} catch (ClassNotFoundException e) {
			throw new RulesCompilerException("Error while executing the compiler.", e); //$NON-NLS-1$
		} catch (SecurityException e) {
			throw new RulesCompilerException("Error while executing the compiler.", e); //$NON-NLS-1$
		} catch (NoSuchMethodException e) {
			throw new RulesCompilerException("Error while executing the compiler.", e); //$NON-NLS-1$
		} catch (IllegalArgumentException e) {
			throw new RulesCompilerException("Error while executing the compiler.", e); //$NON-NLS-1$
		} catch (IllegalAccessException e) {
			throw new RulesCompilerException("Error while executing the compiler.", e); //$NON-NLS-1$
		} catch (InvocationTargetException e) {
			throw new RulesCompilerException("Error while executing the compiler.", e); //$NON-NLS-1$
		} catch (IOException e) {
			throw new RulesCompilerException("Error while executing the compiler.", e); //$NON-NLS-1$
		}

		if (result.intValue() != 0 && messages.isEmpty()) {
            // TODO: exception?
            messages.add(new RulesCompilerError(
            		"Failure executing droolsc, but could not parse the error:" + //$NON-NLS-1$
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
	 * @throws RulesCompilerException
	 */
	private List<RulesCompilerError> compileOutOfProcess(
			File workingDirectory,
			String executable,
			String[] args)
	throws RulesCompilerException {

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
					throw new RulesCompilerException(s, e);
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
	            // TODO: exception?
	            messages.add(new RulesCompilerError(
	            		"Failure executing droolsc, but could not parse the error:" + //$NON-NLS-1$
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
	 * @param reader
	 * @return
	 * @throws IOException 
	 */
	private List<RulesCompilerError> parseStream(BufferedReader reader)
	throws IOException {
		
		List<RulesCompilerError> errors = new ArrayList<RulesCompilerError>();
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
