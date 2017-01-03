package com.mtit.utils;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class MYOBUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWordReplace() throws Exception {

		// Test when there's () 
		String toReplace="This and that.and this.(GM) that";
		String result = MYOBUtils.transformTitle(toReplace);
		Assert.assertEquals("This and that.and this. that", result);
		
		System.out.println(result);

		// Test when there's []
		toReplace="This and that.and this.[GM]";
		result = MYOBUtils.transformTitle(toReplace);
		Assert.assertEquals("This and that.and this.", result);
		
		System.out.println(result);
		
		// Test when there's '
		toReplace="This and that.and this boy's.";
		result = MYOBUtils.transformTitle(toReplace);
		Assert.assertEquals("This and that.and this Boy test.", result);
		
		System.out.println(result);
		
		// Test when there's just normal word
		toReplace="This small train is too smallish.";
		result = MYOBUtils.transformTitle(toReplace);
		Assert.assertEquals("This train is too smallish.", result);
		
		System.out.println(result);
		
	}
}
