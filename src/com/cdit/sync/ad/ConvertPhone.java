package com.cdit.sync.ad;

import java.util.regex.Pattern;

public class ConvertPhone {
	
	public String convertPhoneNumber(String phoneNum)
	{
		 
		 
		
		 Pattern p = Pattern.compile("[[a-zA-Z]()-.,;\\s]+");
	        // Split input with the pattern
	        String[] mang =  p.split(phoneNum);
	                
	        String myStr="";
	        for (int i=0; i<mang.length; i++)
	        {
	        	 myStr+=mang[i]; 
	        	
	        }   

	       String strFirst = "";
	       boolean is84 =false ;
	       boolean isRezo =false;
	        if(!myStr.equals(""))
	        {
	        	strFirst=myStr.substring(0,1);
	        	if(strFirst.equals("0"))
	        		isRezo=true;
	        	
	        	if(myStr.length()>3)
	        	{
	        		if(myStr.substring(0,2).equals("84"))
	        		{
	        			is84=true;
	        			
	        		}
	        		
	        		
	        	}
	        	
	        	
	        	 if(!isRezo && !is84) 
	 	        {
	 	        	myStr ="84".concat(myStr );
	 	        	
	 	        }
	 	        else if(isRezo){
	 	        	
	 	        	myStr ="84".concat(myStr.substring(1,myStr.length()) );
	 	        	
	 	        }
	        	
	        	
	        }
	       
	        
	       
	        
	 
		
		
		
		return myStr;
	}

}
