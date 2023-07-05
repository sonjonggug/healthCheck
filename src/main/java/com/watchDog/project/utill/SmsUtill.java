package com.watchDog.project.utill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.watchDog.project.dao.EventDbDao;
import com.watchDog.project.dao.SaveDbDao;
import com.watchDog.project.dao.SmsDbDao;
import com.watchDog.project.model.EventDbVo;
import com.watchDog.project.model.SmsDbVo;
import com.watchDog.project.model.SmsUserDbVo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SmsUtill {
					
	@Value("${sid}") // WatchDog 서버 고유 ID
	private String sid ;
	
	@Value("${rid.name}") // 서버 자원 사용량 고유 ID
	private String rid ;	
	
	@Value("${farm}") // 서버가 속한 팜
	private String farm ;
	
	@Value("${name}") // 서버 이름
	private String name ;
	
	@Value("${desc}") // 설명
	private String desc ;	

	@Value("${health.check.use.yn}")
	private String tcpChkYn; 			//L4 TCP 체크
		
	@Value("${process.id}")
	private String pid;
	
	@Value("${proc.count}")
	private int procCount;
	
	@Value("${disk.count}")
	private int diskCount;	
		 

	
	public String sendSMS(Map<String, Object> test , String str , String err) throws Exception {
		
		SmsDbDao smsDbDao = new SmsDbDao(DbConnectionFactory.getDbSmsSqlSessionFactory());
		
		SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
						
		String firstContent = "[클라우드 운영 알림]" + "\n";
		String farmName = "- 팜 : " + Encoding.encodingToUTF(farm) + "\n";
		String serverName = "- 장비 : " + Encoding.encodingToUTF(sid) + "\n";		
		String nowDate = "- " + DateTime.nowDateHour();		
		String result = "";
		
		if(str.contains("프로세스")) {
			String procName = "- 프로세스 : " + err + "\n";
			result = firstContent + "\n" + str + "\n" + "\n" + farmName + serverName + procName + nowDate ;			
		}else if (str.contains("디스크")){
			String diskName = "- 디스크 : " + err + "\n";			
			result = firstContent + "\n" + str + "\n" + "\n" + farmName + serverName + diskName + nowDate ;
		}else if (str.contains("데이터 베이스")) {
			String dbName = "- DB : " + err + "\n";
			result = firstContent + "\n" + str + "\n" + "\n" + farmName + serverName + dbName + nowDate ;
		} else {
			result = firstContent + "\n" + str + "\n" + "\n" + farmName + serverName + nowDate ;
		}
						
		// SMS 발송 할 유저 리스트 조회
		 List<SmsUserDbVo> userList = saveDbDao.selectSmsUserList();
				
		 for(int i = 0; i < userList.size(); i++) {
			 userList.get(i).setSmsContent(result); 
		 }
		
		smsDbDao.dbInsert(userList);
		
		return result;
	}
	
	/**
	 * 프로세스 장애 SMS 발송 ( 인터벌 정해야함 )
	 * @param str
	 * @param proc
	 * @return
	 * @throws Exception
	 */
	public String sendProcSMS(String str , List<String> proc) throws Exception {
		
		log.info("WatchDog SMS 발송 -------> " + "Start");
		
		SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
		
		SmsDbDao smsDbDao = new SmsDbDao(DbConnectionFactory.getDbSmsSqlSessionFactory());		
												
		String firstContent = "[클라우드 운영 알림]" + "\n";
		String farmName = "- 팜 : " + Encoding.encodingToUTF(farm) + "\n";
		String serverName = "- 장비 : " + Encoding.encodingToUTF(sid) + "\n";	
		String procName = "";	
		String nowDate = "- " + DateTime.nowDateHour();		
						
		for(int i = 0; i < proc.size(); i++) {		
			procName += "- 프로세스 : " + Encoding.encodingToUTF(proc.get(i)) + "\n";
		
		}
							
		String result = firstContent + str + "\n" + farmName + serverName + procName +nowDate ;			
											
		// SMS 발송 할 유저 리스트 조회
		 List<SmsUserDbVo> userList = saveDbDao.selectSmsUserList();
		
		 for(int i = 0; i < userList.size(); i++) {
			 userList.get(i).setSmsContent(result); 
		 }
		 
		// SMS DB 저장
		smsDbDao.dbInsert(userList);								
		
		log.info("WatchDog SMS 발송 -------> " + result);
		
		log.info(result);
		
		return result;
		
	}
	
	/**
	 * 리소스 임계치 SMS 발송 ( 하루 한번 )
	 * @param str
	 * @throws Exception
	 */
	public void sendResourceSMS(String str) throws Exception {
	  								
		log.info("WatchDog SMS 발송 -------> " + "Start");
		
		SmsDbDao smsDbDao = new SmsDbDao(DbConnectionFactory.getDbSmsSqlSessionFactory());
		
		SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
		
//		SmsDbVo smsDbVo = new SmsDbVo();
		
		String firstContent = "[클라우드 운영 알림]" + "\n";
		String farmName = "- 팜 : " + Encoding.encodingToUTF(farm) + "\n";
		String serverName = "- 장비 : " + Encoding.encodingToUTF(sid) + "\n";		
		String nowDate = "- " + DateTime.nowDateHour();		
		
						
		String result = firstContent + str + "\n" + farmName + serverName + nowDate ;			
								
		// SMS 발송 할 유저 리스트 조회
		 List<SmsUserDbVo> userList = saveDbDao.selectSmsUserList();
				
		 for(int i = 0; i < userList.size(); i++) {			 
			 userList.get(i).setSmsContent(result); 
		 }
		
		smsDbDao.dbInsert(userList);
						
		log.info("WatchDog SMS 발송 -------> " + result);
				 								
	}
}