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
package net.sourceforge.rules.compiler;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public final class RulesCompilerOutputStyle
{
	/**
	 * TODO
	 */
	public final static RulesCompilerOutputStyle ONE_OUTPUT_FILE_PER_INPUT_FILE =
		new RulesCompilerOutputStyle("one-output-file-per-input-file");
	
	/**
	 * TODO
	 */
	public final static RulesCompilerOutputStyle ONE_OUTPUT_FILE_FOR_ALL_INPUT_FILES =
		new RulesCompilerOutputStyle("one-output-file");
	
	private String id;
	
	/**
	 * TODO
	 * 
	 * @param id
	 */
	public RulesCompilerOutputStyle(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o == null || !(o instanceof RulesCompilerOutputStyle)) {
			return false;
		}
		
		return id.equals(((RulesCompilerOutputStyle)o).id);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id;
	}
}
