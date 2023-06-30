package com.watchDog.project.utill;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTime {

	
	
	public static String nowDate() {
		
        LocalDateTime now = LocalDateTime.now();

        // 출력 형식을 지정하여 변환합니다.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String result = now.format(formatter);        		

        
        return result;
	}
	
	public static String nowDateHour() {
		
        LocalDateTime now = LocalDateTime.now();

        // 출력 형식을 지정하여 변환합니다.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        String result = now.format(formatter);        		
        
        return result;
	}
}
