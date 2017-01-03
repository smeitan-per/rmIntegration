package com.mtit.utils;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mtit.utils.RoundUtil;

import static org.junit.Assert.assertEquals;

public class RoundUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testRound() throws Exception {
		double dd = 3.04501d;
		double newd = RoundUtil.round(dd);
		assertEquals(3.05d, newd, 0);
		
	}

}
