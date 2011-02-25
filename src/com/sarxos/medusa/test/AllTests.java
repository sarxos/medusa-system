package com.sarxos.medusa.test;

import junit.framework.Test;
import junit.framework.TestSuite;


public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for default package");
		// $JUnit-BEGIN$
		suite.addTestSuite(DBDAOTraderTest.class);
		suite.addTestSuite(PaperTest.class);
		suite.addTestSuite(QuoteTest.class);
		suite.addTestSuite(SymbolTest.class);
		suite.addTestSuite(TraderTest.class);
		// $JUnit-END$
		return suite;
	}

}
