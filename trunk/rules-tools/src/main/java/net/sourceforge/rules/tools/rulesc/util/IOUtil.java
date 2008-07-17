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
package net.sourceforge.rules.tools.rulesc.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * IOUtil takes care of some of the more annoying IO tasks.
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public final class IOUtil
{
    // Constants -------------------------------------------------------------

    // Attributes ------------------------------------------------------------

    // Static ----------------------------------------------------------------

    /**
     * Unconditionally close the given <code>Closeable</code>.
     * <p>
     * Equivalent to {@link Closeable#close()}, except any exceptions will
     * be ignored. This is typically used in finally blocks.
     *
     * @param closeable the Closeable to be closed, may be null or already closed
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            	// ignored
            }
        }
    }

    // Constructors ----------------------------------------------------------

    /**
     * Private default ctor to prevent instantiation. 
     */
    private IOUtil() {
    }

    // Public ----------------------------------------------------------------

    // Package protected -----------------------------------------------------

    // Protected -------------------------------------------------------------

    // Private ---------------------------------------------------------------

    // Inner classes ---------------------------------------------------------
}
