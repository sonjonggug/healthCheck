package com.watchDog.project.utill;

public class Stat {
	
	public static String START = "N";
	public static boolean dbHealth = true;
	public static int chkDbCnt = 0;
	
	public static int errCount = 0;
	
	public static double ramUsed = 0;
	public static double ramMaxUsed = 0;
	
	public static double cpuUsed = 0;
	public static double cpuMaxUsed = 0;
	
	// 카운트 10 마다 자원 체크 후 DB 저장
	public static int usedCount = 1;
	
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
}
