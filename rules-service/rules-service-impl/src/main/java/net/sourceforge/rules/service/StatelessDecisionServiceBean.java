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
package net.sourceforge.rules.service;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.rules.InvalidRuleSessionException;
import javax.rules.ObjectFilter;
import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.RuleSession;
import javax.rules.RuleSessionCreateException;
import javax.rules.RuleSessionTypeUnsupportedException;
import javax.rules.StatelessRuleSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
@Local({StatelessDecisionService.class})
@Remote({StatelessDecisionServiceRemote.class})
@Stateless(name="StatelessDecisionService")
public class StatelessDecisionServiceBean implements StatelessDecisionServiceRemote
{
	// Constants -------------------------------------------------------------

	/**
	 * The <code>Log</code> instance for this class.
	 */
	private static final Log log = LogFactory.getLog(
			StatelessDecisionServiceBean.class);

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	// Attributes ------------------------------------------------------------

	/**
	 * The <code>RuleRuntime</code> instance we're associated with.
	 */
	private RuleRuntime ruleRuntime;

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// StatelessDecisionService implementation -------------------------------

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.service.StatelessDecisionService#decide(java.lang.String, java.util.Map, java.util.List)
	 */
	public List<?> decide(
			String bindUri,
			Map<?, ?> properties,
			List<?> inputObjects)
	throws DecisionServiceException {

		boolean traceEnabled = log.isTraceEnabled();
		
		if (traceEnabled) {
			StringBuilder sb = new StringBuilder("Executing RuleExecutionSet"); //$NON-NLS-1$
			sb.append("\n\tBindURI     : ").append(bindUri); //$NON-NLS-1$
			sb.append("\n\tProperties  : ").append(properties); //$NON-NLS-1$
			sb.append("\n\tInputObjects: ").append(inputObjects); //$NON-NLS-1$
			log.trace(sb.toString());
		}

		int sessionType = RuleRuntime.STATELESS_SESSION_TYPE;
		StatelessRuleSession ruleSession = null;
		ObjectFilter objectFilter = null;
		List<?> outputObjects = null;

		try {
			ruleSession = (StatelessRuleSession)
			ruleRuntime.createRuleSession(bindUri, properties, sessionType);

			if (traceEnabled) {
				log.trace("Using rule session (" + ruleSession + ")");
			}
			
			objectFilter = createObjectFilter(inputObjects, properties);

			if (traceEnabled) {
				log.trace("Created object filter (" + objectFilter + ")");
			}
			
			outputObjects = ruleSession.executeRules(
					inputObjects,
					objectFilter
			);

			ruleSession.release();
			ruleSession = null;

		} catch (RuleSessionTypeUnsupportedException e) {
			String s = Messages.getError("StatelessDecisionServiceBean.4"); //$NON-NLS-1$
			throw new DecisionServiceException(s, e);
		} catch (RuleSessionCreateException e) {
			String s = Messages.getError("StatelessDecisionServiceBean.5"); //$NON-NLS-1$
			throw new DecisionServiceException(s, e);
		} catch (RuleExecutionSetNotFoundException e) {
			String s = Messages.getError("StatelessDecisionServiceBean.6"); //$NON-NLS-1$
			throw new DecisionServiceException(s, e);
		} catch (RemoteException e) {
			String s = Messages.getError("StatelessDecisionServiceBean.7"); //$NON-NLS-1$
			throw new DecisionServiceException(s, e);
		} catch (InvalidRuleSessionException e) {
			String s = Messages.getError("StatelessDecisionServiceBean.8"); //$NON-NLS-1$
			throw new DecisionServiceException(s, e);
		} finally {
			release(ruleSession);
		}

		return outputObjects;
	}

	// Public ----------------------------------------------------------------

	/**
	 * Setter injection for the <code>RuleRuntime</code> instance.
	 * The dependency is injected at this point.
	 * 
	 * @param ruleRuntime the ruleRuntime to set
	 */
	@Resource(name="RuleSessionFactory")
	public void setRuleRuntime(RuleRuntime ruleRuntime) {
		this.ruleRuntime = ruleRuntime;
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param inputObjects
	 * @param properties
	 * @return
	 */
	protected ObjectFilter createObjectFilter(
			List<?> inputObjects,
			Map<?, ?> properties) {
		return new OutputObjectsOnlyObjectFilter(inputObjects);
	}
	
	// Private ---------------------------------------------------------------

	/**
     * Unconditionally release the given <code>RuleSession</code>.
     * <p>
     * Equivalent to {@link javax.rules.RuleSession#release()},
     * except any exceptions will be ignored. This is typically
     * used in finally blocks.
     *
     * @param ruleSession the <code>RuleSession</code> to be released,
     * 	may be null or already released.
	 */
	private void release(RuleSession ruleSession) {
		if (ruleSession != null) {
			try {
				ruleSession.release();
			} catch (InvalidRuleSessionException e) {
				String s = "Error while releasing rule session"; //$NON-NLS-1$
				log.warn(s, e);
			} catch (RemoteException e) {
				String s = "Error while releasing rule session"; //$NON-NLS-1$
				log.warn(s, e);
			}
		}
	}

	// Inner classes ---------------------------------------------------------
}
