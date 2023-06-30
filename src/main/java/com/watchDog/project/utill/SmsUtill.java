package com.watchDog.project.utill;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.watchDog.project.model.SmsDbVo;

@Component
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
		 

	
	public String sendSMS(String str , String err) throws Exception {
						
		String farmName = "- 팜 : " + Encoding.encodingToUTF(farm) + "\n";
		String serverName = "- 장비 : " + Encoding.encodingToUTF(sid) + "\n";		
		String nowDate = "- " + DateTime.nowDateHour();		
		String result = "";
		
		if(str.contains("프로세스")) {
			String procName = "- 프로세스 : " + err + "\n";
			result = "\n" + str + "\n" + "\n" + farmName + serverName + procName + nowDate ;			
		}else if (str.contains("디스크")){
			String diskName = "- 디스크 : " + err + "\n";			
			result = "\n" + str + "\n" + "\n" + farmName + serverName + diskName + nowDate ;
		}else if (str.contains("데이터 베이스")) {
			String dbName = "- DB : " + err + "\n";
			result = "\n" + str + "\n" + "\n" + farmName + serverName + dbName + nowDate ;
		} else {
			result = "\n" + str + "\n" + "\n" + farmName + serverName + nowDate ;
		}
		
		
		
		return result;
	}
	
 
}