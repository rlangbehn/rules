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
package net.sourceforge.rules.plugin;

import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.compiler.CompilerError;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class CompilationFailureException
extends MojoFailureException
{
	private static final long serialVersionUID = 1L;
	private static final String LS = System.getProperty("line.separator"); //$NON-NLS-1$

    @SuppressWarnings("unchecked")
	public CompilationFailureException(List messages) {
        super(null, "Compilation failure", longMessage(messages)); //$NON-NLS-1$
    }

    @SuppressWarnings("unchecked")
	public static String longMessage(List messages) {
        StringBuffer sb = new StringBuffer();

        for (Iterator it = messages.iterator(); it.hasNext(); ) {
            CompilerError compilerError = (CompilerError)it.next();
            sb.append( compilerError ).append( LS );
        }
        
        return sb.toString();
    }
}
