/*****************************************************************************
 * $Id: FileUtil.java 111 2008-07-20 19:03:01Z rlangbehn $
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
package net.sourceforge.rules.compiler.droolsc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * TODO
 * 
 * @version $Revision: 111 $ $Date: 2008-07-20 21:03:01 +0200 (So, 20 Jul 2008) $
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public final class FileUtil
{
    // Constants -------------------------------------------------------------

    // Attributes ------------------------------------------------------------

    // Static ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getBaseName(String fileName) {
		return getBaseName(new File(fileName));
	}
	
	/**
	 * TODO
	 * 
	 * @param file
	 * @return
	 */
	public static String getBaseName(File file) {
		String name = file.getName();
		int lastDot = name.lastIndexOf('.');

		if (lastDot >= 0) {
			return name.substring(0, lastDot);
		} else {
			return "";
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String fileName) throws IOException {
		return readFile(fileName, null);
	}
	
	/**
	 * TODO
	 * 
	 * @param fileName
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String fileName, String encoding)
	throws IOException {
		return readFile(new File(fileName), encoding);
	}
	
	/**
	 * TODO
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readFile(File file) throws IOException {
		return readFile(file, null);
	}
	
	/**
	 * TODO
	 * 
	 * @param file
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public static String readFile(File file, String encoding)
	throws IOException {
		
		StringBuilder sb = new StringBuilder();
		Reader reader = null;

		try {
			if (encoding == null) {
				reader = new InputStreamReader(new FileInputStream(file));
			} else {
				reader = new InputStreamReader(new FileInputStream(file), encoding);
			}

            int count;
            char[] ac = new char[512];
            
            while ((count = reader.read(ac)) > 0) {
                sb.append(ac, 0, count);
            }
			
		} finally {
			IOUtil.close(reader);
		}
		
		return sb.toString();
	}
	
    // Constructors ----------------------------------------------------------

    /**
     * Private default ctor to prevent instantiation. 
     */
    private FileUtil() {
    }

    // Public ----------------------------------------------------------------

    // Package protected -----------------------------------------------------

    // Protected -------------------------------------------------------------

    // Private ---------------------------------------------------------------

    // Inner classes ---------------------------------------------------------
}
