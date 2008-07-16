package org.drools.examples;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.rule.Package;
import org.drools.util.PackageLoader;

public class HonestPoliticianExample {

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {

        final Package pkg = PackageLoader.loadPackage(HonestPoliticianExample.class, "HonestPolitician.rules");

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        final StatefulSession session = ruleBase.newStatefulSession();

        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
        logger.setFileName( "log/honest-politician" );

        final Politician blair  = new Politician("blair", true);
        final Politician bush  = new Politician("bush", true);
        final Politician chirac  = new Politician("chirac", true);
        final Politician schroder   = new Politician("schroder", true);
        
        session.insert( blair );
        session.insert( bush );
        session.insert( chirac );
        session.insert( schroder );

        session.fireAllRules();
        
        logger.writeToDisk();
        
        session.dispose();
    }
    
    public static class Politician {
    	private String name;
    	
    	private boolean honest;
    	
    	public Politician() {
    		
    	}
    	
		public Politician(String name, boolean honest) {
			super();
			this.name = name;
			this.honest = honest;
		}
		
		public boolean isHonest() {
			return honest;
		}
		
		public void setHonest(boolean honest) {
			this.honest = honest;
		}

		public String getName() {
			return name;
		}    			    
    }

    public static class Hope {
    	
    	public Hope() {
    		
    	}
    	
    }
    
}
