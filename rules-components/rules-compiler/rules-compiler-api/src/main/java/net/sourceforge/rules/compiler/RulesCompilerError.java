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
public class RulesCompilerError
{
	/**
	 * Is this a severe error or a warning?
	 */
	private boolean error;

	/**
	 * The start line number of the offending program text
	 */
	private int startline;

	/**
	 * The start column number of the offending program text
	 */
	private int startcolumn;

	/**
	 * The end line number of the offending program text
	 */
	private int endline;

	/**
	 * The end column number of the offending program text
	 */
	private int endcolumn;

	/**
	 * The name of the file containing the offending program text
	 */
	private String file;

	/**
	 * The actual error text produced by the language processor
	 */
	private String message;

	/**
	 * The error message constructor.
	 *
	 * @param file The name of the file containing the offending program text
	 * @param error Is this a severe error or a warning?
	 * @param startline The start line number of the offending program text
	 * @param startcolumn The start column number of the offending program text
	 * @param endline The end line number of the offending program text
	 * @param endcolumn The end column number of the offending program text
	 * @param message The actual error text produced by the language processor
	 */
	public RulesCompilerError(
			String file,
			boolean error,
			int startline,
			int startcolumn,
			int endline,
			int endcolumn,
			String message) {

		this.file = file;
		this.error = error;
		this.startline = startline;
		this.startcolumn = startcolumn;
		this.endline = endline;
		this.endcolumn = endcolumn;
		this.message = message;
	}

	/**
	 * The warning message constructor.
	 *
	 * @param message The actual error text produced by the language processor
	 */
	public RulesCompilerError(String message) {
		this.message = message;
	}

	/**
	 * The error message constructor.
	 *
	 * @param message The actual error text produced by the language processor
	 * @param error whether it was an error or informational
	 */
	public RulesCompilerError(String message, boolean error) {
		this.message = message;
		this.error = error;
	}

	/**
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * @return the startline
	 */
	public int getStartline() {
		return startline;
	}

	/**
	 * @return the startcolumn
	 */
	public int getStartcolumn() {
		return startcolumn;
	}

	/**
	 * @return the endline
	 */
	public int getEndline() {
		return endline;
	}

	/**
	 * @return the endcolumn
	 */
	public int getEndcolumn() {
		return endcolumn;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
	@Override
    public String toString() {
        if (file == null) {
            return message;
        } else {
        	StringBuilder sb = new StringBuilder();
        	sb.append(file);
        	sb.append(":[");
        	sb.append(startline);
        	sb.append(",");
        	sb.append(startcolumn);
        	sb.append("] ");
        	sb.append(message);
        	return sb.toString();
        }
    }
}
