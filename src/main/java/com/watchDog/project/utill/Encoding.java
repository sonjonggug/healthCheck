package com.watchDog.project.utill;

public class Encoding {

	
	 /**
	  * properties 문자 ISO-8859-1 에서 UTF-8로 변경 
	  * @return
	 * @throws Exception 
	  */
	 public static String encodingToUTF(String text) throws Exception {
		                                                                      
           byte[] utf8Bytes = text.getBytes("ISO-8859-1");  // 원래 인코딩
			String result = new String(utf8Bytes, "UTF-8");	
			
			return result;
	  }	
}
