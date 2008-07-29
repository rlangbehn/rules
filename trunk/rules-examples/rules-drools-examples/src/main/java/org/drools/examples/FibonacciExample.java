package org.drools.examples;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.rule.Package;
import org.drools.util.PackageLoader;

public class FibonacciExample {

    public static void main(final String[] args) throws Exception {

        final Package pkg = PackageLoader.loadPackage(FibonacciExample.class, "Fibonacci.rules");
        
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        final StatefulSession session = ruleBase.newStatefulSession();

        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
        logger.setFileName( "log/fibonacci" );

        session.insert( new Fibonacci( 10 ) );

        session.fireAllRules();

        logger.writeToDisk();
        
        session.dispose(); // Stateful rule session must always be disposed when finished
        
    }

    public static class Fibonacci {
        private int  sequence;

        private long value;
        
        public Fibonacci() {
        	
        }
        
        public Fibonacci(final int sequence) {
            this.sequence = sequence;
            this.value = -1;
        }

        public int getSequence() {
            return this.sequence;
        }

        public void setValue(final long value) {
            this.value = value;
        }

        public long getValue() {
            return this.value;
        }

        public String toString() {
            return "Fibonacci(" + this.sequence + "/" + this.value + ")";
        }
    }

}
