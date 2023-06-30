package com.watchDog.project.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.watchDog.project.dao.SaveDbDao;
import com.watchDog.project.model.ProcStatusVo;
import com.watchDog.project.service.CheckDataBase;
import com.watchDog.project.service.CheckDbTableSpace;
import com.watchDog.project.service.CheckResource;
import com.watchDog.project.service.CheckServerProcess;
import com.watchDog.project.service.SendService;
import com.watchDog.project.utill.Constans;
import com.watchDog.project.utill.DbConnectionFactory;
import com.watchDog.project.utill.Init;
import com.watchDog.project.utill.Stat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@PropertySource("classpath:config.properties")
public class Scheduler {
	

	private final CheckServerProcess checkServerProcess ;
	private final CheckResource checkserServerUsed;
	private final CheckDataBase checkDataBase;	
	private final SendService sendService;	
	private final CheckDbTableSpace checkDbTableSpace;
	
	@Value("${sid}")
	 String sid;
		
	@Value("${target.java.use.yn}")
	 String javaUseYn;

	@Value("${target.sys.use.yn}")
	 String sysUseYn;
	
	@Value("${target.db.use.yn}")
	 String dbUseYn;		
	
	@Value("${db.Retry.cnt}")
	int dbRetryCnt;	
	
	@Value("${retry.cnt}")
	 int retryCnt;
				 
			
	//DB check
	//boolean dbHealth = true; //db 이전 상태 값 ( true : ok / false : down or exception 상황 )
	
	//int chkDbCnt = 0;
	
	/**
	 * 자바 프로세스 체크
	 */
	@Scheduled(fixedRate = 10000) // 테스트용 30초 , 600000 -> 10분
//	@Scheduled(cron="0/10 * * * * *") // 10초
    public void checkServerProcess() {
		if(javaUseYn.equals(Constans.USE_YN_Y) && Stat.START.equals(Constans.USE_YN_Y)) {
	
			try {			
				
				SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());								
				
				// 프로세스 체크 전 상태
				List<ProcStatusVo> beforeProcList = saveDbDao.selectProcList(sid);								
				
				// 같은 List<ProcStatusVo>를 사용해서 nowResult 같이 바뀌면 beforeProcList 값도 바뀌기 때문에 상태코드 따로 List 저장 
				List<String> beforeProcStatus = new ArrayList<String>();				
				for(int i = 0; i < beforeProcList.size(); i++) {					
					beforeProcStatus.add(beforeProcList.get(i).getProcStatus());															
				}
										
				// 프로세스 체크 후 상태
				List<ProcStatusVo> nowResult = checkServerProcess.serverConnect(beforeProcList);
																							
				ProcStatusVo changeList = new ProcStatusVo();
												
				for(int i = 0; i < nowResult.size(); i++) { // result 키 값만큼 반복
										
					if(beforeProcStatus.get(i).equals("Y") && nowResult.get(i).getProcStatus().equals("Y")) { // 이전 상태값이 Y 이고 현재 상태값이 Y 면 로그만  			
						 log.info(nowResult.get(i).getProcName() + Constans.SUCCESS);
						 changeList = nowResult.get(i);	
					} else if(beforeProcStatus.get(i).equals("N") && nowResult.get(i).getProcStatus().equals("Y") ) { // 이전 상태값이 N 이고 현재 상태값이 Y 면 로그만
						 log.info(nowResult.get(i).getProcName() + Constans.SUCCESS);
						 changeList = nowResult.get(i);
					} else if(beforeProcStatus.get(i).equals("N") && nowResult.get(i).getProcStatus().equals("N") ) { // 이전 상태값이 N 이고 현재 상태값이 N 면 로그만
						 log.info(nowResult.get(i).getProcName() + Constans.FAIL);						 
						 changeList = nowResult.get(i);						 
					} else if(beforeProcStatus.get(i).equals("Y") && nowResult.get(i).getProcStatus().equals("N") ) { // 이전 상태값이 Y 이고 현재 상태값이 N 면 재시작 및 SMS 발송
						
						changeList = checkServerProcess.restart(nowResult.get(i));			
						
							if(changeList.getProcStatus().equals("Y")) {
								sendService.SendRestartSuccess(changeList);
								log.info(changeList.getProcName()+ Constans.RESTART_SUCESS);
							}else {
								for(; Stat.errCount < retryCnt;) { // 재기동 해야하는 횟수가 에러 카운트보다 클때
//									changeList = checkServerProcess.restart(nowResult.get(i));
									if(Stat.errCount == 0) { // 에러카운트가 0 (재기동 성공) 일때 
										log.info(changeList.getProcName()+ Constans.RESTART_SUCESS);
										break;
									}
								}	
								
								if(Stat.errCount >= retryCnt) { // 에러카운트와 재기동 횟수가 크거나 같을때 즉, 재기동 횟수만큼 돌렸으나 계속 다운일때
									log.info(changeList.getProcName() + Constans.RESTART_FAIL);
									sendService.SendRestartFail(changeList);
								}																									
							}								
									
					}												
						nowResult.set(i, changeList); // 변경된 리스트 값을 set						
				} // for 문 끝																	
						saveDbDao.updateProcList(nowResult);
						saveDbDao.updateServerDate(sid);
					} catch (Exception e) {
					  e.printStackTrace();
					  log.info("Exception Error -----------------> ");
					}
			} else {		
				log.info("자바프로세스 모니터링 사용 안함");
			}
    }
	
	/**
	 * 서버 사용량 체크
	 */
	@Scheduled(fixedRate = 10000) // 테스트용 30초 , 600000 -> 10분 // @Scheduled(cron="0/10 * * * * *") 1분마다 
    public void checkServerUsed() {
      if(sysUseYn.equals(Constans.USE_YN_Y) && Stat.START.equals(Constans.USE_YN_Y)) {
		try {	
				
				SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
			
	   			boolean checkRam = checkserServerUsed.checkRamUsed();
	   			boolean checkCPU = checkserServerUsed.checkCpuUsed();
	   			boolean checkDisk = checkserServerUsed.checkDiskUsed();
			 
				Stat.addUsedCount(); // 스케쥴러 동작 시 +1 카운트
			 
				// 10분마다 누적 카운트 초기화
				if(Stat.usedCount == 10) {    			    	
					Stat.initResource();
				    }    			 											
				
				saveDbDao.updateServerDate(sid);
				
				} catch (Exception e) {
				  e.printStackTrace();
				  log.info("Exception Error -----------------> ");
				}
      	}else {
      		log.info("서버 사용량 모니터링 사용안함");
      	}
    }		
	
	/**
	 * DB 체크
	 */	
//	@Scheduled(fixedDelay = 10000) // 테스트용 30초 , 600000 -> 10분 
	public void checkDataBase() {
		if(dbUseYn.equals(Constans.USE_YN_Y)) {
			try {	
				//서비스 호출
				boolean result = checkDataBase.dataBaseHealthCheck(); //여러개 DB체크해야한다면 로직 변경 필요
				if(result) {
					log.info("성공");
				}else {
					//log.info("실패");
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception Error -----------------> ");
			}
		}else {
      		log.info("DB 체크 모니터링 사용안함");
      	}
	}		
	/**
	 * DB 테이블 스페이스 체크
	 */
//	@Scheduled(fixedRate = 20000) // 테스트용 30초 , 600000 -> 10분 // @Scheduled(cron="0/10 * * * * *") 1분마다 
    public void checkDbTableSp() {
		if(true) {
			try {									
				checkDbTableSpace.chkDbTableSpSelect();
				Stat.addUsedCount();
				// 10분마다 누적 카운트 초기화
				if(Stat.usedCount > 10) {    			    	
					Stat.initResource();
				}
			} catch (Exception e) {
			  e.printStackTrace();
			  log.info("Exception Error -----------------> ");
			}
	  	}else {
	  		log.info("DB 테이블 스페이스 체크 사용안함");
	  	}
    }
	
	
	/**
	 * 서버 사용량 체크
	 * @throws Exception 
	 */
//	@Scheduled(fixedRate = 10000) // 테스트용 30초 , 600000 -> 10분 // @Scheduled(cron="0/10 * * * * *") 1분마다 
    public void test() throws Exception {
        
    		try {
    			 
    			
    			checkserServerUsed.checkRamUsed();
    			checkserServerUsed.checkCpuUsed();
    			checkserServerUsed.checkDiskUsed();
    			 
    			Stat.addUsedCount();
    			 
    			 if(Stat.usedCount == 10) {    			    	
    				 Stat.initResource();
    			    }    			 
    			     			
				} catch (Exception e) {
				  e.printStackTrace();
				  log.info("Exception Error -----------------> ");
				}
          
    }   
		       
	
}
