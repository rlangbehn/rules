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
package net.sourceforge.rules.tools.rulesc.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.rules.tools.rulesc.util.Context;
import net.sourceforge.rules.tools.rulesc.util.Log;
import net.sourceforge.rules.tools.rulesc.util.Options;

import org.drools.compiler.DroolsError;
import org.drools.compiler.FunctionError;
import org.drools.compiler.GlobalError;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.ParserError;
import org.drools.compiler.RuleError;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.lang.dsl.MappingError;
import org.drools.rule.Package;
import org.drools.rule.builder.dialect.java.JavaDialectConfiguration;


/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RulesCompiler
{
	// Constants -------------------------------------------------------------

	/**
	 * TODO
	 */
	public static final String DEFAULT_EXTENSION = ".rules"; //$NON-NLS-1$

	/**
	 * TODO
	 */
	public static final String VERSION_INFO = "rulesc version 1.0.0"; //$NON-NLS-1$

	/**
	 * The context key for the compiler.
	 */
	private static final Context.Key<RulesCompiler> compilerKey =
		new Context.Key<RulesCompiler>();

	// Attributes ------------------------------------------------------------

	private Log log;
	private String compiler;
	private File destination;
	private Set<File> inputFiles = new HashSet<File>();
	private boolean keepRuleSource = false;

	// Static ----------------------------------------------------------------

	/**
	 * TODO
	 *
	 * @param context
	 * @return
	 */
	public static RulesCompiler instance(Context context) {
		RulesCompiler instance = context.get(compilerKey);

		if (instance == null) {
			instance = new RulesCompiler(context);
		}

		return instance;
	}

	/**
	 * TODO
	 *
	 * @return
	 */
	public static String version() {
		return RulesCompiler.VERSION_INFO;
	}

	// Constructors ----------------------------------------------------------

	/**
	 * TODO
	 *
	 * @param context
	 */
	public RulesCompiler(Context context) {
		context.put(compilerKey, this);

		log = Log.instance(context);

		Options options = Options.instance(context);
		compiler = options.get("-compiler"); //$NON-NLS-1$
        keepRuleSource = Boolean.parseBoolean(options.get("-keepRuleSource")); //$NON-NLS-1$
		String d = (String)options.get("-d"); //$NON-NLS-1$

		if (d != null) {
			destination = new File(d);
		}
	}

	// Public ----------------------------------------------------------------

	/**
	 * TODO
	 */
	public void close() {
		log.flush();
	}

	/**
	 * TODO
	 *
	 * @param filenames
	 */
	public void compile(List fileNames) {
		PackageBuilderConfiguration config = createPackageBuilderConfiguration();

		for (Iterator i = fileNames.iterator(); i.hasNext(); ) {
			String fileName = (String)i.next();
			PackageBuilder builder = new PackageBuilder(config);

			try {
				if (fileName.endsWith(".drl")) { //$NON-NLS-1$
					Reader reader = createReader(fileName);

					if (reader == null) {
						// situation already reported
						continue;
					}

					builder.addPackageFromDrl(reader);
				} else if (fileName.endsWith(".xml")) { //$NON-NLS-1$
					Reader reader = createReader(fileName);

					if (reader == null) {
						// situation already reported
						continue;
					}

					builder.addPackageFromXml(reader);
				} else if (fileName.endsWith(".xls")) { //$NON-NLS-1$
					InputStream in = createInputStream(fileName);

					if (in == null) {
						// situation already reported
						continue;
					}

					SpreadsheetCompiler sc = new SpreadsheetCompiler();
					String drlSource = sc.compile(in, InputType.XLS);
					Reader reader = new StringReader(drlSource);

					if (keepRuleSource) {
						writeSource(fileName, ".drl", drlSource); //$NON-NLS-1$
					}

					builder.addPackageFromDrl(reader);
				} else if (fileName.endsWith(".csv")) { //$NON-NLS-1$
					InputStream in = createInputStream(fileName);

					if (in == null) {
						// situation already reported
						continue;
					}

					SpreadsheetCompiler sc = new SpreadsheetCompiler();
					String drlSource = sc.compile(in, InputType.CSV);
					Reader reader = new StringReader(drlSource);

					if (keepRuleSource) {
						writeSource(fileName, ".drl", drlSource); //$NON-NLS-1$
					}

					builder.addPackageFromDrl(reader);
				} else {
					// TODO report the situation
					continue;
				}

				if (builder.hasErrors()) {
					log(builder.getErrors().getErrors());
				} else {
					Package pkg = builder.getPackage();
					writePackage(pkg, fileName);
				}
			} catch (Exception e) {
				log.error(0, "error", e.getMessage()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * TODO
	 *
	 * @return
	 */
	public String getCompiler() {
		return compiler;
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param fileName
	 * @return
	 */
	private InputStream createInputStream(String fileName) {
		try {
			File f = new File(fileName);
			inputFiles.add(f);
			return new FileInputStream(f);
		} catch (IOException e) {
			log.error(0, "cant.read.file", fileName); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	protected PackageBuilderConfiguration createPackageBuilderConfiguration() {
		PackageBuilderConfiguration config = new PackageBuilderConfiguration();
		JavaDialectConfiguration javaConfig = (JavaDialectConfiguration)
		config.getDialectConfiguration("java");
//		config.setJavaLanguageLevel("1.5");

		if ("eclipse".equalsIgnoreCase(getCompiler())) { //$NON-NLS-1$
			javaConfig.setCompiler(JavaDialectConfiguration.ECLIPSE);
		}	else if ("extEclipse".equalsIgnoreCase(getCompiler())) { //$NON-NLS-1$
			javaConfig.setCompiler(JavaDialectConfiguration.ECLIPSE);
		}	else if ("janino".equalsIgnoreCase(getCompiler())) { //$NON-NLS-1$
			javaConfig.setCompiler(JavaDialectConfiguration.JANINO);
		}	else if ("extJanino".equalsIgnoreCase(getCompiler())) { //$NON-NLS-1$
			javaConfig.setCompiler(JavaDialectConfiguration.JANINO);
		}

		return config;
	}

	/**
	 * Try to open reader with the given fileName.
	 * Report an error if this fails.
	 *
	 * @param fileName The file name of the reader to be opened.
	 * @return
	 */
	private Reader createReader(String fileName) {
		try {
			File f = new File(fileName);
			inputFiles.add(f);
			return new FileReader(f);
		} catch (IOException e) {
			log.error(0, "cant.read.file", fileName); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * TODO
	 * 
	 * @param errors
	 */
	private void log(DroolsError[] errors) {
		for (int i = 0; i < errors.length; i++) {
			if (errors[i] instanceof FunctionError) {
				log((FunctionError)errors[i]);
			} else if (errors[i] instanceof GlobalError) {
				log((GlobalError)errors[i]);
			} else if (errors[i] instanceof MappingError) {
				log((MappingError)errors[i]);
			} else if (errors[i] instanceof ParserError) {
				log((ParserError)errors[i]);
			} else if (errors[i] instanceof RuleError) {
				log((RuleError)errors[i]);
			} else {
				log.error(0, "rules.error", errors[i].getMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param error
	 */
	private void log(FunctionError error) {
		log.error(0, "rules.function.error", error.getMessage()); //$NON-NLS-1$
	}

	/**
	 * TODO
	 * 
	 * @param error
	 */
	private void log(GlobalError error) {
		log.error(0, "rules.global.error", error.getMessage()); //$NON-NLS-1$
	}

	/**
	 * TODO
	 * 
	 * @param error
	 */
	private void log(MappingError error) {
		log.error(0, "rules.mapping.error", error.getMessage()); //$NON-NLS-1$
	}

	/**
	 * TODO
	 * 
	 * @param error
	 */
	private void log(ParserError error) {
		log.error(0, "rules.parser.error", error.getMessage()); //$NON-NLS-1$
	}

	/**
	 * TODO
	 * 
	 * @param error
	 */
	private void log(RuleError error) {
		log.error(
				0, "rules.rule.error", //$NON-NLS-1$
				error.getRule(),
				error.getMessage()
		);
	}

	/**
	 * Emit a class file for the given Package instance.
	 *
	 * @param pkg
	 * @param srcFileName
	 */
	private void writePackage(Package pkg, String srcFileName) {

		File destFile = null;
		ObjectOutputStream out = null;
		String pkgName = pkg.getName();

		try {
			destFile = outputFile(pkgName, srcFileName, RulesCompiler.DEFAULT_EXTENSION);
			out = new ObjectOutputStream(new FileOutputStream(destFile));
			out.writeObject(pkg);
			out.close();
			out = null;
		} catch (IOException e) {
			log.error(0, "class.cant.write", srcFileName, e.getMessage()); //$NON-NLS-1$
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
				destFile.delete();
				out = null;
			}
		}
	}

	/**
	 * TODO
	 *
	 * @param fileName
	 * @param extension
	 * @param source
	 */
	private void writeSource(String fileName, String extension, String source) {
		File srcFile = null;
		Writer out = null;

		try {
			int index = fileName.lastIndexOf('.');

			if (index != -1) {
				fileName = fileName.substring(0, index);
			}

			srcFile = new File(fileName + extension);
			out = new BufferedWriter(new FileWriter(srcFile));
			out.write(source);
			out.close();
			out = null;
		} catch (IOException e) {
			log.error(0, "class.cant.write", fileName, e.getMessage()); //$NON-NLS-1$
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
				srcFile.delete();
				out = null;
			}
		}
	}

	/**
	 * TODO
	 *
	 * @param pkgName
	 * @param fileName
	 * @param extension
	 * @return
	 */
	private File outputFile(String pkgName, String fileName, String extension)
	throws IOException {

		if (destination != null) {
			fileName = new File(fileName).getName();
			return outputFile(destination, pkgName, fileName, extension);
		} else {
			File srcFile = new File(fileName);
			File srcDir = srcFile.getParentFile();
			fileName = new File(fileName).getName();
			return outputFile(srcDir, pkgName, fileName, extension);
		}
	}

	/**
	 * TODO
	 *
	 * @param destDir
	 * @param pkgName
	 * @param fileName
	 * @param extension
	 * @return
	 */
	private File outputFile(File destDir, String pkgName, String fileName, String extension)
	throws IOException {

		int index = fileName.lastIndexOf('.');

		if (index != -1) {
			fileName = fileName.substring(0, index);
		}

		String className = pkgName + "." + fileName; //$NON-NLS-1$

		int startIndex = 0;
		int endIndex = className.indexOf('.');

		while (endIndex >= startIndex) {
			destDir = new File(destDir, className.substring(startIndex, endIndex));

			if (!destDir.exists()) {
				destDir.mkdir();
			}

			startIndex = endIndex + 1;
			endIndex = className.indexOf('.', startIndex);
		}

		return new File(destDir, fileName + extension);
	}

	/**
	 * The number of errors encountered so far.
	 */
	public int errorCount() {
		return log.nerrors;
	}

	/**
	 * The number of warnings encountered so far.
	 */
	public int warningCount() {
		return log.nwarnings;
	}

	// Inner classes ---------------------------------------------------------
}
