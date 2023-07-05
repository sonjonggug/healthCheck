package com.watchDog.project.utill;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTime {

	private static LocalDate lastExecutionDate; // 마지막 실행 날짜를 저장하는 변수
	
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
	
	/**
	 * 하루 한번만
	 * 현재 날짜가 마지막 실행 날짜와 다른 경우에만 실행
	 * @return
	 */
	public static boolean shouldExecute() {
    	
        LocalDate currentDate = LocalDate.now();

        // 현재 날짜가 마지막 실행 날짜와 다른 경우에만 실행
        if (lastExecutionDate == null || !lastExecutionDate.equals(currentDate)) {
            lastExecutionDate = currentDate; // 마지막 실행 날짜를 갱신
            return true;
        }

        return false;
    }
}
