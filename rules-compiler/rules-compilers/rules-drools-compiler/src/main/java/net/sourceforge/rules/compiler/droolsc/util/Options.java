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
package net.sourceforge.rules.compiler.droolsc.util;

import java.util.HashMap;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class Options extends HashMap<String, String>
{
	/**
	 * TODO
	 */
	private static final long serialVersionUID = 0L;
	
	/**
	 * The context key for the options.
	 */
	protected static final Context.Key<Options> optionsKey =
		new Context.Key<Options>();

	/**
	 * TODO
	 *
	 * @param context
	 * @return
	 */
	public static Options instance(Context context) {
		Options instance = context.get(optionsKey);
		
		if (instance == null) {
			instance = new Options(context);
		}
		
		return instance;
	}

	/**
	 * TODO
	 *
	 * @param context
	 */
	protected Options(Context context) {
		super();
		context.put(optionsKey, this);
	}
}
