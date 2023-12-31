package com.watchDog.project.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.watchDog.project.dao.ChkDbDao;
import com.watchDog.project.dao.SmsDbDao;
import com.watchDog.project.model.SmsDbVo;
import com.watchDog.project.utill.Constans;
import com.watchDog.project.utill.DbConnectionFactory;
import com.watchDog.project.utill.Stat;

@Slf4j
@RequiredArgsConstructor
@Service
public class CheckDataBase {
		
	
	@Value("${dev.server.id}")
	 String id;
	
	@Value("${dev.server.pw}")
	 String pw;
	
	@Value("${dev.server.ip}")
	 String ip;
	
	@Value("${dev.server.port}")
	 int port;
	
	@Value("${interval}")
	 int interval;
	
	@Value("${db.Retry.cnt}")
	int dbRetryCnt;	
	
	/**
	 * db health 체크
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> dataBaseHealthCheck() throws Exception{
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		// DB 연결
		ChkDbDao chkDbDao = new ChkDbDao(DbConnectionFactory.getChkDbSqlSessionFactory());
		SmsDbDao smsDbDao = new SmsDbDao(DbConnectionFactory.getDbSmsSqlSessionFactory());
		SmsDbVo smsDbVo = new SmsDbVo();
		// 포맷 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd a h시 mm분");
        
		try {
	        // 쿼리 실행 시간 측정 시작
	        long startTime = System.currentTimeMillis();
	        
			// DB에  INSERT/UPDATE ( MERGE INTO )
			chkDbDao.dbMergeInto();
	
	        //smsDbDao.dbInsert(Stat.dbHealth);
			// 쿼리 실행 시간 측정 종료
	        long endTime = System.currentTimeMillis();
	        
	        // 실행 시간 계산
	        long elapsedTime = endTime - startTime;
	        
	        if (elapsedTime >= Constans.elapsedTime) {
	        	
	        	if(Stat.chkDbCnt >= dbRetryCnt && Stat.dbHealth) { 
//		            System.out.println("쿼리 실행시간 10초 이상입니다. (" + elapsedTime + " milliseconds)");
//		            Stat.dbHealth = false;
//		            Stat.chkDbCnt = 0;
//		            // 현재 시간을 포맷 적용하여 문자열로 변환
//		            String formattedDateTime = LocalDateTime.now().format(formatter);
//					smsDbVo.setSmsContent("[클라우드 운영 알림] \n 장비가 동작을 정지했습니다. \n - 팜 : 팜 이름 \n - 장비 : 장비이름 \n"+formattedDateTime); 
//		            smsDbDao.dbInsert(smsDbVo);
	        		result.put("status", "N");
	        	}else if(Stat.chkDbCnt < dbRetryCnt && Stat.dbHealth) {
		            //System.out.println("쿼리 실행시간 10초 이상입니다. Stat.chkDbCnt: "+ Stat.chkDbCnt +" (" + elapsedTime + " milliseconds)");
	        		Stat.chkDbCnt++;
	        	}
	        }else {
	        	if(Stat.dbHealth == false) {
//		            System.out.println("db 가 복구되었습니다. ");
//		            Stat.dbHealth = true;
//		            Stat.chkDbCnt = 0;
//		            String formattedDateTime = LocalDateTime.now().format(formatter);
//					smsDbVo.setSmsContent("[클라우드 운영 알림] \n 데이터 베이스가 복구 되었습니다. \n - 팜 : 팜 이름 \n - 장비 : 장비이름 \n"+formattedDateTime); 
//		            smsDbDao.dbInsert(smsDbVo);
	        		result.put("status", "Y");
	        	}else {
		            Stat.chkDbCnt = 0;
	        	}
	        }
			
		} catch (Exception e) {
			
			if(Stat.chkDbCnt >= dbRetryCnt && Stat.dbHealth) { 
	            System.out.println("sqlException. 3번 이상 동작");
	            Stat.dbHealth = false;
	            Stat.chkDbCnt = 0;
//	            // 현재 시간을 포맷 적용하여 문자열로 변환
//	            String formattedDateTime = LocalDateTime.now().format(formatter);
//				smsDbVo.setSmsContent("[클라우드 운영 알림] \n 장비가 동작을 정지했습니다. \n - 팜 : 팜 이름 \n - 장비 : 장비이름 \n"+formattedDateTime); 
//	            smsDbDao.dbInsert(smsDbVo);
        		result.put("status", "N");
	    	}else if(Stat.chkDbCnt < dbRetryCnt && Stat.dbHealth) {
	            System.out.println("sqlException. Stat.chkDbCnt: "+ Stat.chkDbCnt);
	    		Stat.chkDbCnt++;
	    	}
	    }
		return result;
	}
}




	

