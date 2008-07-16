package org.drools.tutorials.banking;

public class Example1 {
    public static void main(String[] args) throws Exception {
        new RuleRunner().runRules( new String[] { "banking.rules" },
                                   new Object[0] );
    }
}
