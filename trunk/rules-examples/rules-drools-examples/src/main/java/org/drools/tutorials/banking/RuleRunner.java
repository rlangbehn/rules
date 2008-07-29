package org.drools.tutorials.banking;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.rule.Package;
import org.drools.util.PackageLoader;

public class RuleRunner {

    public RuleRunner() {
    }

    public void runRules(String[] rules,
                         Object[] facts) throws Exception {

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        for ( int i = 0; i < rules.length; i++ ) {
            String ruleFile = rules[i];
            System.out.println( "Loading file: " + ruleFile );
            Package pkg = PackageLoader.loadPackage(RuleRunner.class, ruleFile);
           	ruleBase.addPackage(pkg);
        }

        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        for ( int i = 0; i < facts.length; i++ ) {
            Object fact = facts[i];
            System.out.println( "Inserting fact: " + fact );
            workingMemory.insert( fact );
        }

        workingMemory.fireAllRules();
    }
}
