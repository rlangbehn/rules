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
package org.drools.jsr94.rules.repository;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class BindUriParser
{
	// Constants -------------------------------------------------------------

	/**
	 * TODO
	 */
	public static final String BIND_URI_REGEX = "(.*)/(.*)/(.*)";
	
	/**
	 * TODO
	 */
	private static final Pattern PATTERN = Pattern.compile(BIND_URI_REGEX);
	
	// Attributes ------------------------------------------------------------

	private String packageName;
	private String packageVersion;
	private String ruleExecutionSetName;
	private String ruleExecutionSetVersion;
	
	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param bindUri
	 * @throws UnsupportedEncodingException 
	 */
	public BindUriParser(String bindUri) throws UnsupportedEncodingException {
		
        String url = URLDecoder.decode(bindUri, "UTF-8");
        Matcher matcher = PATTERN.matcher(url);
        
        if (matcher.matches()) {
            this.packageName = matcher.group(1);
            this.ruleExecutionSetName = matcher.group(2);
            this.ruleExecutionSetVersion = matcher.group(3);
        }
	}

	// Public ----------------------------------------------------------------

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @return the packageVersion
	 */
	public String getPackageVersion() {
		return packageVersion;
	}

	/**
	 * @return the ruleExecutionSetName
	 */
	public String getRuleExecutionSetName() {
		return ruleExecutionSetName;
	}

	/**
	 * @return the ruleExecutionSetVersion
	 */
	public String getRuleExecutionSetVersion() {
		return ruleExecutionSetVersion;
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
