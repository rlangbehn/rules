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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.rules.tools.rulesc.util.Context;
import net.sourceforge.rules.tools.rulesc.util.FileUtil;
import net.sourceforge.rules.tools.rulesc.util.IOUtil;
import net.sourceforge.rules.tools.rulesc.util.Log;
import net.sourceforge.rules.tools.rulesc.util.Options;

import org.drools.brms.client.modeldriven.brl.RuleModel;
import org.drools.brms.server.util.BRDRLPersistence;
import org.drools.brms.server.util.BRXMLPersistence;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.FunctionError;
import org.drools.compiler.GlobalError;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.ParserError;
import org.drools.compiler.RuleError;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.lang.Expander;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.lang.dsl.DefaultExpander;
import org.drools.lang.dsl.DefaultExpanderResolver;
import org.drools.lang.dsl.MappingError;
import org.drools.rule.Package;
import org.drools.rule.builder.dialect.java.JavaDialectConfiguration;

import com.sun.org.apache.xalan.internal.xsltc.compiler.CompilerException;

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
	public void compile(List<String> fileNames) {
		PackageBuilderConfiguration config = createPackageBuilderConfiguration();

		for(String fileName : fileNames) {
			PackageBuilder builder = new PackageBuilder(config);

			try {
				if (fileName.endsWith(".brl")) {
					compileBRLFile(builder, fileName);
				} else if (fileName.endsWith(".csv")) { //$NON-NLS-1$
					compileCSVFile(builder, fileName);
				} else if (fileName.endsWith(".drl")) { //$NON-NLS-1$
					compileDRLFile(builder, fileName);
				} else if (fileName.endsWith(".dslr")) { //$NON-NLS-1$
					compileDSLRFile(builder, fileName);
				} else if (fileName.endsWith(".rfm")) { //$NON-NLS-1$
					compileRFMFile(builder, fileName);
				} else if (fileName.endsWith(".xls")) { //$NON-NLS-1$
					compileXLSFile(builder, fileName);
				} else if (fileName.endsWith(".xml")) { //$NON-NLS-1$
					compileXMLFile(builder, fileName);
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
	 * @param builder
	 * @param fileName
     * @throws CompilerException 
     * @throws DroolsParserException 
	 * @throws IOException
	 */
	private void compileBRLFile(PackageBuilder builder, String fileName)
	throws DroolsParserException, IOException {

		File file = new File(fileName);
		String brlSource = FileUtil.readFile(file);
		RuleModel model = BRXMLPersistence.getInstance().unmarshal(brlSource);
		File pkgFile = getPackageFile(file.getParentFile());
		String pkgSource = FileUtil.readFile(pkgFile);
		model.name = FileUtil.getBaseName(file);
		StringBuilder sb = new StringBuilder(pkgSource);
		sb.append(BRDRLPersistence.getInstance().marshal(model));
		InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
		Reader reader = new InputStreamReader(in);
		builder.addPackageFromDrl(reader);
	}

	/**
	 * TODO
	 * 
	 * @param builder
	 * @param fileName
	 * @throws DroolsParserException
	 * @throws IOException
	 */
	private void compileCSVFile(PackageBuilder builder, String fileName)
	throws DroolsParserException, IOException {

		InputStream in = createInputStream(fileName);

		if (in == null) {
			// situation already reported
			return;
		}

		try {
			SpreadsheetCompiler sc = new SpreadsheetCompiler();
			String drlSource = sc.compile(in, InputType.CSV);
			Reader reader = new StringReader(drlSource);

			if (keepRuleSource) {
				writeSource(fileName, ".drl", drlSource); //$NON-NLS-1$
			}

			builder.addPackageFromDrl(reader);
		} finally {
		    IOUtil.close(in);
		}
	}

	/**
	 * TODO
	 * 
	 * @param builder
	 * @param fileName
	 * @throws DroolsParserException
	 * @throws IOException
	 */
	private void compileDRLFile(PackageBuilder builder, String fileName)
	throws DroolsParserException, IOException {

		Reader reader = createReader(fileName);

		if (reader == null) {
			// situation already reported
			return;
		}

		try {
			builder.addPackageFromDrl(reader);
		} finally {
			IOUtil.close(reader);
		}
	}

	/**
	 * TODO
	 * 
	 * @param builder
	 * @param fileName
	 * @throws CompilerException
	 * @throws DroolsParserException
	 * @throws IOException
	 */
	private void compileDSLRFile(PackageBuilder builder, String fileName)
	throws DroolsParserException, IOException {
		
		File file = new File(fileName);
		DrlParser parser = new DrlParser();
		String drlSource = FileUtil.readFile(file);
		DefaultExpanderResolver resolver = createExpanderResolver(file.getParentFile());
		String expandedDRLSource = parser.getExpandedDRL(drlSource, resolver);
		Reader reader = new StringReader(expandedDRLSource);
		builder.addPackageFromDrl(reader);
	}

	/**
	 * TODO
	 * 
	 * @param builder
	 * @param fileName
	 * @throws IOException
	 */
	private void compileRFMFile(PackageBuilder builder, String fileName)
	throws IOException {
		
		Reader reader = createReader(fileName);
		
		if (reader == null) {
			// situation already reported
			return;
		}

		try {
			builder.addRuleFlow(reader);
		} finally {
			IOUtil.close(reader);
		}
	}

	/**
	 * TODO
	 * 
	 * @param builder
	 * @param fileName
	 * @throws DroolsParserException
	 * @throws IOException
	 */
	private void compileXLSFile(PackageBuilder builder, String fileName)
	throws DroolsParserException, IOException {
		
		InputStream in = new FileInputStream(fileName);
		
		if (in == null) {
			// situation already reported
			return;
		}

		try {
	        SpreadsheetCompiler sc = new SpreadsheetCompiler();
	        String drlSource = sc.compile(in, InputType.XLS);
	        Reader reader = new StringReader(drlSource);
	        
			if (keepRuleSource) {
				writeSource(fileName, ".drl", drlSource); //$NON-NLS-1$
			}

	        builder.addPackageFromDrl(reader);
		} finally {
			IOUtil.close(in);
		}
	}

	/**
	 * TODO
	 * 
	 * @param builder
	 * @param fileName
	 * @throws DroolsParserException
	 * @throws IOException
	 */
	private void compileXMLFile(PackageBuilder builder, String fileName)
	throws DroolsParserException, IOException {
		
		Reader reader = createReader(fileName);
		
		if (reader == null) {
			// situation already reported
			return;
		}

		try {
			builder.addPackageFromXml(reader);
		} finally {
			IOUtil.close(reader);
		}
	}

	/**
	 * TODO
	 * 
	 * @param rootDir
	 * @return
	 * @throws IOException 
	 */
	private DefaultExpanderResolver createExpanderResolver(File rootDir)
	throws IOException {
		
		DefaultExpanderResolver resolver = new DefaultExpanderResolver();
		DSLMappingFile dslMappingFile = new DSLMappingFile();
		File[] dslFiles = getDSLFiles(rootDir);
		
		for (int i = 0; i < dslFiles.length; i++) {
			Reader reader = createReader(dslFiles[i]);
			
			if (dslMappingFile.parseAndLoad(reader)) {
				Expander expander = new DefaultExpander();
				expander.addDSLMapping(dslMappingFile.getMapping());
				resolver.addExpander("*", expander);
			} else {
				throw new IOException("Error while parsing and loading DSL file: " + dslFiles[i].getAbsolutePath());
			}
		}
		
		return resolver;
	}

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
		javaConfig.setJavaLanguageLevel("1.5");

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
		return createReader(new File(fileName));
	}

	/**
	 * TODO
	 * 
	 * @param file
	 * @return
	 */
	private Reader createReader(File file) {
		try {
			inputFiles.add(file);
			return new FileReader(file);
		} catch (IOException e) {
			log.error(0, "cant.read.file", file.getAbsolutePath()); //$NON-NLS-1$
			return null;
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param rootDir
	 * @return
	 * @throws IOException 
	 */
	private File[] getDSLFiles(File rootDir) throws IOException {
		File[] files = rootDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".dsl");
			}
		});

		if (files.length == 0) {
			throw new IOException("No DSL files found in: " + rootDir);
		}

		return files;
	}

	/**
	 * TODO
	 * 
	 * @param rootDir
	 * @return
	 * @throws IOException 
	 */
	private File getPackageFile(File rootDir) throws IOException {
		File[] files = rootDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".package");
			}
		});

		if (files.length > 1) {
			throw new IOException("More than one package file found in: " + rootDir);
		}
		
		if (files.length == 0) {
			throw new IOException("No package file found in: " + rootDir);
		}
		
		return files[0];
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
