/*****************************************************************************
 * $Id: Main.java 111 2008-07-20 19:03:01Z rlangbehn $
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
package net.sourceforge.rules.compiler.droolsc.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sourceforge.rules.compiler.droolsc.util.Context;
import net.sourceforge.rules.compiler.droolsc.util.FatalError;
import net.sourceforge.rules.compiler.droolsc.util.Log;
import net.sourceforge.rules.compiler.droolsc.util.Options;

/**
 * TODO
 * 
 * @version $Revision: 111 $ $Date: 2008-07-20 21:03:01 +0200 (So, 20 Jul 2008) $
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class Main
{
	/**
	 * Result codes
	 */
	static final int
	EXIT_OK = 0,				// Compilation completed with no errors.
	EXIT_ERROR = 1,			// Completed but reported errors.
	EXIT_CMDERR = 2,		// Bad command-line arguments.
	EXIT_SYSERR = 3,		// System error or resource exhaustion.
	EXIT_ABNORMAL = 4;	// Compiler terminated abnormally.

	/**
	 * The name of the compiler, for use in diagnostics.
	 */
	private String ownName;
	
	/**
	 * The writer to use for diagnostic output.
	 */
	private PrintWriter out;

	/**
	 * This class represents an option recognized by the main program.
	 */
	private class Option {
		/**
		 * Option string
		 */
		String name;
		/**
		 * Documentation key for arguments.
		 */
		String argsNameKey;
		/**
		 * Documentation key for description.
		 */
		String descrKey;
		/**
		 * Suffix option (-foo=bar or -foo:bar)
		 */
		boolean hasSuffix;
		
		Option(String name, String argsNameKey, String descrKey) {
			this.name = name;
			this.argsNameKey = argsNameKey;
			this.descrKey = descrKey;
			char lastChar = name.charAt(name.length() - 1);
			hasSuffix = lastChar == ':' || lastChar == '=';
		}
		
		Option(String name, String descrKey) {
			this(name, null, descrKey);
		}

		public String toString() {
			return name;
		}
		
		/**
		 * Does this option take a (separate) operand?
		 */
		boolean hasArg() {
			return argsNameKey != null && !hasSuffix;
		}
		
		/**
		 * Does argument string match option pattern?
		 *
		 * @param arg The command line argument string.
		 */
		boolean matches(String arg) {
			return hasSuffix ? arg.startsWith(name) : arg.equals(name);
		}
		
		void help() {
			String s = "  " + helpSynopsis(); //$NON-NLS-1$
			out.print(s);
			
			for (int j = s.length(); j < 29; j++) {
				out.print(" "); //$NON-NLS-1$
			}
			
			Log.printLines(out, getLocalizedString(descrKey));
		}
		
		String helpSynopsis() {
			return name +
			(argsNameKey == null ? "" : //$NON-NLS-1$
				((hasSuffix ? "" : " ") + //$NON-NLS-1$ //$NON-NLS-2$
						getLocalizedString(argsNameKey)));
		}
		
		void xhelp() {}
		
		/**
		 * Process the option (with arg). Return true if error detected.
		 *
		 * @param option
		 * @param arg
		 * @return
		 */
		boolean process(String option, String arg) {
			options.put(option, arg);
			return false;
		}
		
		/**
		 * Process the option (without arg). Return true if error detected.
		 *
		 * @param option
		 * @return
		 */
		boolean process(String option) {
			if (hasSuffix) {
				return process(name, option.substring(name.length()));
			} else {
				return process(option, option);
			}
		}
	};

	/**
	 * A hidden (implementor) option.
	 */
	private class HiddenOption extends Option {
		HiddenOption(String name) {
			super(name, null, null);
		}
		
		HiddenOption(String name, String argsNameKey) {
			super(name, argsNameKey);
		}
		
		void help() {}
		void xhelp() {}
	};
	
	/**
	 * 
	 */
	private Option[] recognizedOptions = {
			new Option("-compiler", "opt.arg.compiler", "opt.compiler"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			new Option("-d", "opt.arg.directory", "opt.d"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			new Option("-outputfile", "opt.arg.file", "opt.outputfile"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new Option("-keepRuleSource", "opt.arg.keepRuleSource", "opt.keepRuleSource"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			new Option("-verbose", "opt.verbose"), //$NON-NLS-1$ //$NON-NLS-2$
			
			new Option("-classpath", "opt.arg.path", "opt.classpath"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			new Option("-cp", "opt.arg.path", "opt.classpath") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				boolean process(String option, String arg) {
					return super.process("-classpath", arg); //$NON-NLS-1$
				}
			},

			new Option("-sourcepath", "opt.arg.path", "opt.sourcepath"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			new Option("-help", "opt.help") { //$NON-NLS-1$ //$NON-NLS-2$
				boolean process(String option) {
					Main.this.help();
					return super.process(option);
				}
			},
			
			new Option("-version", "opt.version") { //$NON-NLS-1$ //$NON-NLS-2$
				boolean process(String option) {
					Log.printLines(out, ownName + " " + RulesCompiler.version()); //$NON-NLS-1$
					return super.process(option);
				}
			},
			
			new HiddenOption("sourcefile") { //$NON-NLS-1$
				String s;
				
				boolean matches(String s) {
					this.s = s;
					return s.endsWith(".brl") || //$NON-NLS-1$
				           s.endsWith(".csv") || //$NON-NLS-1$
					       s.endsWith(".drl") || //$NON-NLS-1$
					       s.endsWith(".dslr") || //$NON-NLS-1$
					       s.endsWith(".rfm") || //$NON-NLS-1$
					       s.endsWith(".xls") || //$NON-NLS-1$
					       s.endsWith(".xml"); //$NON-NLS-1$
				}
				
				boolean process(String option) {
					if (!filenames.contains(s)) {
						filenames.add(s);
					}
					return false;
				}
			}
	};

	/**
	 * Construct a compiler instance with the given name.
	 * 
	 * @param name The name of this compiler instance.
	 */
	public Main(String name) {
		this(name, new PrintWriter(System.err, true));
	}

	/**
	 * Construct a compiler instance with the given name.
	 *
	 * @param name The name of this compiler instance.
	 * @param out The writer to use for diagnostic output.
	 */
	public Main(String name, PrintWriter out) {
		this.ownName = name;
		this.out = out;
	}

	/**
	 * A table of all options that's passed to the RulesCompiler constructor.
	 */
	private Options options = null;

	/**
	 * The list of files to process
	 */
	List<String> filenames = null;
	
	/**
	 * Print a string that explains usage.
	 */
	void help() {
		Log.printLines(out, getLocalizedString("msg.usage.header", ownName)); //$NON-NLS-1$
		
		for (int i = 0; i < recognizedOptions.length; i++) {
			recognizedOptions[i].help();
		}
		
		out.println();
	}

	/**
	 * Report a usage error.
	 *
	 * @param key
	 * @param args
	 */
	void error(String key, Object... args) {
		warning(key, args);
		help();
	}

	/**
	 * Report a warning.
	 *
	 * @param key
	 * @param args
	 */
	void warning(String key, Object[] args) {
		Log.printLines(out, ownName + ": " + getLocalizedString(key, args)); //$NON-NLS-1$
	}

	/**
	 * TODO
	 *
	 * @param flags
	 * @return
	 */
	protected List<String> processArgs(String[] flags) {
		int ac = 0;
		
		while (ac < flags.length) {
			String flag = flags[ac];
			ac++;
			
			int j;
			
			for (j = 0; j < recognizedOptions.length; j++) {
				if (recognizedOptions[j].matches(flag)) {
					break;
				}
			}
				
			if (j == recognizedOptions.length) {
				error("err.invalid.flag", flag); //$NON-NLS-1$
				return null;
			}
			
			Option option = recognizedOptions[j];
			
			if (option.hasArg()) {
				if (ac == flags.length) {
					error("err.req.arg", flag); //$NON-NLS-1$
					return null;
				}
				
				String operand = flags[ac];
				ac++;
				
				if (option.process(flag, operand)) {
					return null;
				}
			} else {
				if (option.process(flag)) {
					return null;
				}
			}
		}
		
		return filenames;
	}

	/**
	 * Programmatic interface for main function.
	 *
	 * @param args The command line parameters.
	 * @return
	 */
	public int compile(String[] args) {
		return compile(args, new Context());
	}

	/**
	 * Programmatic interface for main function.
	 *
	 * @param args The command line parameters.
	 * @param context
	 * @return
	 */
	public int compile(String[] args, Context context) {

		options = Options.instance(context);
		filenames = new ArrayList<String>();
		RulesCompiler comp = null;
		
		try {
			if (args.length == 0) {
				help();
				return EXIT_CMDERR;
			}
			
			try {
				filenames = processArgs(CommandLine.parse(args));
				
				if (filenames == null) {
					return EXIT_CMDERR;
				} else if (filenames.isEmpty()) {
					// it is allowed to compile nothing if just asking for help
					if (options.get("-help") != null || options.get("-X") != null) { //$NON-NLS-1$ //$NON-NLS-2$
						return EXIT_OK;
					}
					
					error("err.no.source.files"); //$NON-NLS-1$
					return EXIT_CMDERR;
				}
			} catch (FileNotFoundException e) {
				Log.printLines(out, ownName + ": " + //$NON-NLS-1$
						getLocalizedString("err.file.not.found", e.getMessage())); //$NON-NLS-1$
				return EXIT_SYSERR;
			}
			
			context.put(Log.outKey, out);
			comp = RulesCompiler.instance(context);
			
			if (comp == null) {
				return EXIT_SYSERR;
			}
			
			comp.compile(filenames);
			
			if (comp.errorCount() != 0) {
				return EXIT_ERROR;
			}
			
		} catch (IOException e) {
			ioMessage(e);
			return EXIT_SYSERR;
		} catch (OutOfMemoryError e) {
			resourceMessage(e);
			return EXIT_SYSERR;
		} catch (StackOverflowError e) {
			resourceMessage(e);
			return EXIT_SYSERR;
		} catch (FatalError e) {
			feMessage(e);
			return EXIT_SYSERR;
		} catch (Throwable e) {
			// Nasty. If we've already reported an error, compensate
			// for buggy compiler error recovery by swallowing thrown
			// exceptions.
			if (comp.errorCount() == 0) {
				bugMessage(e);
			}
			return EXIT_ABNORMAL;
		} finally {
			if (comp != null) {
				comp.close();
			}
			filenames = null;
			options = null;
		}
		
		return EXIT_OK;
	}

	/**
	 * Print a message reporting an internal error.
	 *
	 * @param cause
	 */
	void bugMessage(Throwable cause) {
		Log.printLines(out, getLocalizedString("msg.bug", RulesCompiler.version())); //$NON-NLS-1$
		cause.printStackTrace(out);
	}
	
	/**
	 * Print a message reporting a fatal error.
	 *
	 * @param cause
	 */
	void feMessage(Throwable cause) {
		Log.printLines(out, cause.getMessage());
	}
	
	/**
	 * Print a message reporting an input/output error.
	 *
	 * @param cause
	 */
	void ioMessage(Throwable cause) {
		Log.printLines(out, getLocalizedString("msg.io")); //$NON-NLS-1$
		cause.printStackTrace(out);
	}
	
	/**
	 * Print a message reorting an out-of-resources error.
	 *
	 * @param cause
	 */
	void resourceMessage(Throwable cause) {
		Log.printLines(out, getLocalizedString("msg.resource")); //$NON-NLS-1$
		cause.printStackTrace(out);
	}
	
	/**
	 * Find a localized string in the resource bundle.
	 *
	 * @param key The key for the localized string.
	 * @param args
	 * @return
	 */
	private static String getLocalizedString(String key, Object... args) {
		return getText("droolsc." + key, args); //$NON-NLS-1$
	}
	
	private static final String DROOLSC_RB = "net.sourceforge.rules.compiler.droolsc.droolsc"; //$NON-NLS-1$
	
	private static ResourceBundle messageRB;
	
	private static void initResource() {
		try {
			messageRB = ResourceBundle.getBundle(DROOLSC_RB);
		} catch (MissingResourceException e) {
			Error x = new FatalError("Fatal Error: Resource for droolsc missing"); //$NON-NLS-1$
			x.initCause(e);
			throw x;
		}
	}
	
	private static String getText(String key, Object... _args) {
		String[] args = new String[_args.length];
		
		for (int i = 0; i < args.length; i++) {
			args[i] = "" + _args[i]; //$NON-NLS-1$
		}
		
		if (messageRB == null) {
			initResource();
		}
		
		try {
			return MessageFormat.format(messageRB.getString(key), (Object[])args);
		} catch (MissingResourceException e) {
			String msg = "droolsc message file broken: key={0} arguments={1}, {2}"; //$NON-NLS-1$
			return MessageFormat.format(msg, (Object[])args);
		}
	}
}
