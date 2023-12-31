package com.watchDog.project.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.watchDog.project.WatchDogApplication;
import com.watchDog.project.dao.ChkDbDao;
import com.watchDog.project.dao.ChkDbTableSpDao;
import com.watchDog.project.dao.SaveDbDao;
import com.watchDog.project.dao.SmsDbDao;
import com.watchDog.project.model.ChkDbTableSpVo;
import com.watchDog.project.model.ProcStatusVo;
import com.watchDog.project.model.ResourceStatusVo;
import com.watchDog.project.model.SmsDbVo;
import com.watchDog.project.utill.Constans;
import com.watchDog.project.utill.DbConnectionFactory;
import com.watchDog.project.utill.Encoding;
import com.watchDog.project.utill.Init;
import com.watchDog.project.utill.Stat;

@Slf4j
@RequiredArgsConstructor
@Service
public class CheckDbTableSpace {
		
	
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
	
	@Value("${rid.name}")
	 String rid;
	
	@Value("${sid}")
	 String sid;
	
	@Value("${db.tb.space.check.count}")
	 int tspChkCount;
	
	//@Value("${db.tb.space.check.info1}")
	//List<String> chkInfo1;
	
	
//	@Value("${db.tb.space.check.info1}")
//	String chkInfo1;
		
	/**
	 * DB 테이블 스페이스 검사
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> chkDbTableSpSelect() throws Exception{

		Map<String, Object> result = new HashMap<String, Object>();
		boolean thresholdCheckResult = true;
		ChkDbTableSpDao chkDbTableSpDao = new ChkDbTableSpDao(DbConnectionFactory.getChkDbSqlSessionFactory());
                
		try {
			// 프로세스 리스트 저장
    	    Properties properties = new Properties();
            InputStream input = null;
            
            // Properties 파일을 로드합니다.	    	 
            input = WatchDogApplication.class.getClassLoader().getResourceAsStream("config.properties");
            properties.load(input);

			List<ChkDbTableSpVo> dbTableSpList = new ArrayList<ChkDbTableSpVo>();
			dbTableSpList = chkDbTableSpDao.chkDbTableSpSelect();	

	        for(int i = 1 ; i <= tspChkCount; i++) {
		    	List<String> list = Arrays.asList(Encoding.encodingToUTF(properties.getProperty("db.tb.space.check.info"+i)).split(",")); //  , 기준으로 잘라서 리스트에 넣기
		    	thresholdCheckResult = Thresholdcheck(dbTableSpList, list, i);
		    	if(thresholdCheckResult == false) {
		    		break;
		    	}
		    }
			
			if(!thresholdCheckResult) {
        		result.put("status", "N");
			}
			
		}	catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private boolean Thresholdcheck(List<ChkDbTableSpVo> dbTableSpList, List<String> chkInfo, int i) {
		boolean result = true;
		double tableSpUsed = 0;
	    
		if(chkInfo.size() > 0) {
			for(int j=0; j<dbTableSpList.size(); j++) {
				if(dbTableSpList.get(j).getTablespace().equals(chkInfo.get(0))) {
					tableSpUsed = dbTableSpList.get(j).getUsepercent();
				    // 1분마다 CPU 사용량 누적 저장 
//				    Init.tableSpUsed += tableSpUsed;	        
					
				    // 현재 CPU 사용량이 cpuMaxUsed보다 클 시 값 저장 
//				    if(tableSpUsed > Init.tableSpMaxUsed) {
//				    	Init.tableSpMaxUsed = tableSpUsed;
//				    }
				    
					if(dbTableSpList.get(j).getUsepercent() > Float.parseFloat(chkInfo.get(1)) ) {
						//임계치 이상
						System.out.println("임계치 이상 입니다.");
						result = false;
					}else {
						
					}
				}				
			}
            
			// 10분 마다 각 update 
		    if(Stat.usedCount == 10) {
			    saveResourceStatus(chkInfo, tableSpUsed, i);
	        }
		    
		}
		return result;
	}
	
	// 10분 마다  update 
	private void saveResourceStatus(List<String> chkInfo, double tableSpUsed, int i) {
		SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
		
		// 프로세스 정보 properties에서 받아와 DB 저장
        //List<ResourceStatusVo> reStatusVoList = new ArrayList<ResourceStatusVo>();
    	ResourceStatusVo reStatusVo = new ResourceStatusVo();
    	reStatusVo.setRid(rid+"TS"+i);  
    	reStatusVo.setSid(sid); 
    	reStatusVo.setResType("TABLESPACE");
    	reStatusVo.setResAvg(tableSpUsed); 
    	reStatusVo.setResPeak(tableSpUsed);
    	reStatusVo.setResPath(chkInfo.get(0));
		reStatusVo.setResState("Y");
    	//reStatusVoList.add(reStatusVo);      	
        
        try {
			saveDbDao.updateResource(reStatusVo);
		} catch (Exception e) {
	    	System.out.println(e);
	    	e.printStackTrace();
		}
	    
	}
}




	

