package com.watchDog.project.utill;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Init {

	
	public static ArrayList<List<String>> list = getInstance();
	
	public static String START = "N";
	
	public static int errCount = 0;
	
	public static double ramUsed = 0;
	public static double ramMaxUsed = 0;
	
	public static double cpuUsed = 0;
	public static double cpuMaxUsed = 0;
	
	// 카운트 10 마다 자원 체크 후 DB 저장
	public static int usedCount = 1;
	
	
	private Init() {
		
	}
	
	/**
	 * List 초기화
	 * @return
	 */
	 public static ArrayList<List<String>> getInstance() {
		 			 		 
		 	ArrayList<List<String>> csvList = new ArrayList<List<String>>();
		 
	        File csv = new File("C:/ttest/test.csv");
	        BufferedReader br = null;
	        String line = "";

	        try {
	            br = new BufferedReader(new FileReader(csv));
	                        
	            line = br.readLine(); // 첫 번째 행은 헤더로 사용하기 위해 먼저 읽어온다
	            
	            while ((line = br.readLine()) != null) { // readLine()은 파일에서 개행된 한 줄의 데이터를 읽어온다.            	
	                List<String> aLine = new ArrayList<String>();
	                String[] lineArr = line.replace("\uFEFF", "").split(","); // 파일의 한 줄을 ,로 나누어 배열에 저장 후 리스트로 변환한다. / \uFFEF’가 있으면 지우거나 빈 문자열로 교체하는 플로우를 넣는다              
	                aLine = Arrays.asList(lineArr); 	                
	                csvList.add(aLine);
	                
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (br != null) { 
	                    br.close(); // 사용 후 BufferedReader를 닫아준다.
	                }
	            } catch(IOException e) {
	                e.printStackTrace();
	            }
	        }	        
		 			 
	        return csvList;
	    }
	 
	 	/**
		 * 에러 카운트 0 으로 초기화
		 * @return
		 */
		 public static void initCount() {
			 errCount = 0;	 		 			 
		  }
		 
	 	/**
		 * 에러 카운트 0 으로 초기화
		 * @return
		 */
		 public static void addCount() {
			 errCount++;	 		 			 
		  }	
		 
		 
		 
		 /**
		  * 서버 자원 사용량 카운트 +1
		  * @return
		  */
		 public static void addUsedCount() {
			 usedCount++;	 		 			 
		  }		 
		 
		 /**
		  * 서버 자원 사용량 초기화
		  * @return
		  */
		 public static void initResource() {
			    usedCount = 1 ;
		    	cpuUsed = 0;
		    	ramUsed = 0;
		    	cpuMaxUsed = 0;
		    	ramMaxUsed = 0;	 		 			 
		  }	
		 
		 /**
		  * properties 문자 ISO-8859-1 에서 UTF-8로 변경 
		  * @return
		 * @throws Exception 
		  */
		 public static String encoding(String text) throws Exception {
			                                                                      
	            byte[] utf8Bytes = text.getBytes("ISO-8859-1");  // 원래 인코딩
				String result = new String(utf8Bytes, "UTF-8");	
				
				return result;
		  }	
		 
}
