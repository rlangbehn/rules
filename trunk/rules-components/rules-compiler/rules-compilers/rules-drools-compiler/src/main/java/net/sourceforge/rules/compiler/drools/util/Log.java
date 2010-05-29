/*****************************************************************************
 * $Id: Log.java 573 2010-05-27 14:37:22Z rlangbehn $
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
package net.sourceforge.rules.compiler.drools.util;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * TODO
 * 
 * @version $Revision: 573 $ $Date: 2010-05-27 16:37:22 +0200 (Do, 27 Mai 2010) $
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class Log
{
	/**
	 * The context key for the log.
	 */
	protected static final Context.Key<Log> logKey =
		new Context.Key<Log>();

	/**
	 * The context key for the output PrintWriter.
	 */
	public static final Context.Key<PrintWriter> outKey =
		new Context.Key<PrintWriter>();

	/**
	 * TODO
	 *
	 * @param context
	 * @return
	 */
	public static Log instance(Context context) {
		Log instance = context.get(logKey);
		
		if (instance == null) {
			instance = new Log(context);
		}
		
		return instance;
	}

	/**
	 * TODO
	 *
	 * @param context
	 * @return
	 */
	static final PrintWriter defaultWriter(Context context) {
		PrintWriter result = context.get(outKey);
		
		if (result == null) {
			context.put(outKey, result = new PrintWriter(System.err));
		}
		
		return result;
	}

	public final PrintWriter errWriter;
	public final PrintWriter warnWriter;
	public final PrintWriter noticeWriter;

	/**
	 * The number of errors encountered so far.
	 */
	public int nerrors = 0;

	/**
	 * The number of warnings encountered so far.
	 */
	public int nwarnings = 0;

	/**
	 * The maximum number of errors/warnings that are reported,
	 * can be reassigned from outside.
	 */
	final private int maxErrors;
	final private int maxWarnings;

	/**
	 * Switch: emit warning messages
	 */
	private boolean emitWarnings;
	
	/**
	 * TODO
	 *
	 * @param context
	 */
	protected Log(Context context) {
		this(context, defaultWriter(context));
	}

	/**
	 * TODO
	 *
	 * @param context
	 * @param writer
	 */
	protected Log(Context context, PrintWriter defaultWriter) {
		this(context, defaultWriter, defaultWriter, defaultWriter);
	}

	/**
	 * TODO
	 *
	 * @param context
	 * @param errWriter
	 * @param warnWriter
	 * @param noticeWriter
	 */
	protected Log(Context context, PrintWriter errWriter, PrintWriter warnWriter, PrintWriter noticeWriter) {
		context.put(logKey, this);
		
		this.errWriter = errWriter;
		this.warnWriter = warnWriter;
		this.noticeWriter = noticeWriter;
		
		Options options = Options.instance(context);
		this.emitWarnings = options.get("-nowarn") == null; //$NON-NLS-1$
		this.maxErrors = getIntOption(options, "-Xmaxerrs", 100); //$NON-NLS-1$
		this.maxWarnings = getIntOption(options, "-Xmaxwarns", 100); //$NON-NLS-1$
	}

	private int getIntOption(Options options, String optionName, int defaultValue) {
		String s = options.get(optionName);
		
		try {
			if (s != null) return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			// silently ignore ill-formed numbers
		}
		
		return defaultValue;
	}
	
	/**
	 * TODO
	 *
	 * @param out
	 * @param localizedString
	 */
	public static void printLines(PrintWriter out, String msg) {
		int nl;
		
		while ((nl = msg.indexOf('\n')) != -1) {
			out.println(msg.substring(0, nl));
			msg = msg.substring(nl + 1);
		}
		
		if (msg.length() != 0) {
			out.println(msg);
		}
	}

	/**
	 * TODO
	 */
	public void flush() {
		errWriter.flush();
		warnWriter.flush();
		noticeWriter.flush();
	}

	/**
	 * TODO
	 *
	 * @param pos
	 * @param key
	 * @param args
	 */
	public void error(int pos, String key, Object... args) {
		if (nerrors < maxErrors) {
			printLines(
					errWriter,
					getText("compiler.err.error") + //$NON-NLS-1$
					getText("compiler.err." + key, args) //$NON-NLS-1$
			);
			nerrors++;
		}
	}

	/**
	 * TODO
	 *
	 * @param pos
	 * @param key
	 * @param args
	 */
	public void warning(int pos, String key, Object... args) {
		if (nwarnings < maxWarnings && emitWarnings) {
			printLines(
					warnWriter,
					getText("compiler.warn.warning") + //$NON-NLS-1$
					getText("compiler.warn." + key, args) //$NON-NLS-1$
			);
			nwarnings++;
		}
	}
	
	/**
	 * Find a localized string in the resource bundle.
	 *
	 * @param key The key for the localized string.
	 * @param args
	 * @return
	 */
	public static String getLocalizedString(String key, Object... args) {
		return getText("compiler.misc." + key, args); //$NON-NLS-1$
	}
	
	private static final String COMPILER_RB = "net.sourceforge.rules.compiler.drools.compiler"; //$NON-NLS-1$
	
	private static ResourceBundle messageRB;
	
	private static void initResource() {
		try {
			messageRB = ResourceBundle.getBundle(COMPILER_RB);
		} catch (MissingResourceException e) {
			Error x = new FatalError("Fatal Error: Resource for compiler missing"); //$NON-NLS-1$
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
			String msg = "compiler message file broken: key={0} arguments={0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}"; //$NON-NLS-1$
			return MessageFormat.format(msg, (Object[])args);
		}
	}
}
