package com.watchDog.project.utill;

import com.watchDog.project.model.SmsDbVo;

public class Constans {
	
	public static final String SUCCESS = " 프로세스가 정상작동 중입니다.";
	public static final String FAIL = " 프로세스가 작동하지 않습니다.";
	public static final String RESTART_SUCESS = " 프로세스가 작동하지 않아 재기동 되었습니다.";
	public static final String RESTART_FAIL = " 프로세스가 재기동에 실패하였습니다.";
	
	public static final String USE_YN_Y = "Y";
	public static final String USE_YN_N = "N";
		
	
	public static final String dbInsert = "dbInsert";
	public static final String dbUpdate = "dbUpdate";
	public static final String dbDelete = "dbDelete";	
	public static final String checkDb = "checkDb";
	public static final long elapsedTime = 10000; //쿼리 작업 시간 ( 임시로 10초로 설정 )	
	
	
//	public static final String MSG_PROC_RESTART = "프로세스가 재 실행되었습니다";
	
	public static final String MSG_SERVER_RESTART = "장비가 재 시작되었습니다.";
	public static final String MSG_PROC_RESTART = "프로세스가 재 실행되었습니다.";
	public static final String MSG_PROC_RESTART_FAIL = "프로세스를 재 실행하려고 했으나 실패하였습니다.";
	public static final String MSG_RAM_OVER = "평균 메모리 사용률이 xx%로 기준을 초과하였습니다.";
	public static final String MSG_CPU_OVER = "평균 CPU 사용률이 xx%로 기준을 초과하였습니다.";
	public static final String MSG_DISK_OVER = "평균 디스크 사용률이 xx% 로 기준을 초과하였습니다.";
	public static final String MSG_DISK_ACCESS_FAIL = "디스크 접속에 실패하였습니다.";
	public static final String MSG_DB_ACCESS_FAIL = "데이터 베이스에 이용에 문제가 발생하였습니다.";
	public static final String MSG_DB_TBS_OVER = "데이터 베이스의 테이블 스페이스가 부족합니다.";
	public static final String MSG_SERVER_STOP = "장비가 동작을 정지했습니다.";
	

	

	 
	
}
