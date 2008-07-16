package org.drools.examples;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.event.DebugAgendaEventListener;
import org.drools.event.DebugWorkingMemoryEventListener;
import org.drools.rule.Package;
import org.drools.util.PackageLoader;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public class HelloWorldExample {

    public static final void main(final String[] args) throws Exception {
    	
        final Package pkg = PackageLoader.loadPackage(HelloWorldExample.class, "HelloWorld.rules");

        //add the package to a rulebase (deploy the rule package).
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        final StatefulSession session = ruleBase.newStatefulSession();
        
        session.addEventListener( new DebugAgendaEventListener() );
        session.addEventListener( new DebugWorkingMemoryEventListener() );
        
        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
        logger.setFileName( "log/helloworld" );        

        final Message message = new Message();
        message.setMessage( "Hello World" );
        message.setStatus( Message.HELLO );
        session.insert( message );
        
        session.fireAllRules();
        
        logger.writeToDisk();
        
        session.dispose();
    }

    public static class Message {
        public static final int HELLO   = 0;
        public static final int GOODBYE = 1;

        private String          message;

        private int             status;

        public Message() {

        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(final String message) {
            this.message = message;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(final int status) {
            this.status = status;
        }
    }

}
