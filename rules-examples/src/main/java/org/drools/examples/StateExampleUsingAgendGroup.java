package org.drools.examples;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.DefaultAgendaEventListener;
import org.drools.rule.Package;
import org.drools.util.PackageLoader;

public class StateExampleUsingAgendGroup {

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {
        final Package pkg = PackageLoader.loadPackage(
        	StateExampleUsingAgendGroup.class,
        	"StateExampleUsingAgendGroup.rules"
        );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        final StatefulSession session = ruleBase.newStatefulSession();

        session.addEventListener( new DefaultAgendaEventListener() {
            public void afterActivationFired(final AfterActivationFiredEvent arg0) {
                super.afterActivationFired( arg0, session );
            }
        } );

        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
        logger.setFileName( "log/state" );

        final State a = new State( "A" );
        final State b = new State( "B" );
        final State c = new State( "C" );
        final State d = new State( "D" );

        // By setting dynamic to TRUE, Drools will use JavaBean
        // PropertyChangeListeners so you don't have to call update().
        final boolean dynamic = true;

        session.insert( a,
                        dynamic );
        session.insert( b,
                        dynamic );
        session.insert( c,
                        dynamic );
        session.insert( d,
                        dynamic );

        session.fireAllRules();
        session.dispose();

        logger.writeToDisk();
    }
}
