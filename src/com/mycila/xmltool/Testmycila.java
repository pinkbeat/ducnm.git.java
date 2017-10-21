package com.mycila.xmltool;

import java.io.File; 
 
/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com) 
 */ 
final class Testmycila { 
    public static void main(String[] args) { 
    	XMLDocValidatorTest test = new XMLDocValidatorTest();
    	try {
			test.validate_URL();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    } 
    
}
