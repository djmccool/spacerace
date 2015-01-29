package test;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestNumberFormatException {

	@Test (expected = NumberFormatException.class) 
	public void testNullInput() {
		Integer.parseInt(null);
	}
	
	@Test (expected = NumberFormatException.class)
	public void testBadInput(){
		Integer.parseInt("poop");
	}

}
