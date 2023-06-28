package com.watchDog.project.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.watchDog.project.dao.ChkDbDao;
import com.watchDog.project.dao.ChkDbTableSpDao;
import com.watchDog.project.dao.SmsDbDao;
import com.watchDog.project.model.ChkDbTableSpVo;
import com.watchDog.project.model.SmsDbVo;
import com.watchDog.project.utill.Constans;
import com.watchDog.project.utill.DbConnectionFactory;

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
	
	@Value("${db.tb.space.check.count}")
	 int chkCount;
	
	@Value("${db.tb.space.check.info1}")
	List<String> chkInfo1;
	
	
//	@Value("${db.tb.space.check.info1}")
//	String chkInfo1;
		
	/**
	 * DB 테이블 스페이스 검사
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public void chkDbTableSpSelect() throws Exception{
		
		// DB 연결
		SmsDbDao smsDbDao = new SmsDbDao(DbConnectionFactory.getDbSmsSqlSessionFactory());
		ChkDbTableSpDao chkDbTableSpDao = new ChkDbTableSpDao(DbConnectionFactory.getChkDbSqlSessionFactory());
		SmsDbVo smsDbVo = new SmsDbVo();
		// 포맷 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd a h시 mm분");
        
		try {
			List<ChkDbTableSpVo> dbTableSpList = new ArrayList<ChkDbTableSpVo>();
			dbTableSpList = chkDbTableSpDao.chkDbTableSpSelect();	

			boolean result = Thresholdcheck(dbTableSpList, chkInfo1);
			System.out.println("result: "+result);
			if(!result) {

	            String formattedDateTime = LocalDateTime.now().format(formatter);
				smsDbVo.setSmsContent("[클라우드 운영 알림] \n 데이터 베이스의 테이블 스페이스가 부족합니다. \n - 팜 : 팜 이름 \n - 장비 : 장비이름 \n - DB : 장비이름 \n"+formattedDateTime); 
				smsDbDao.dbInsert(smsDbVo);
			}
			
		}	catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean Thresholdcheck(List<ChkDbTableSpVo> dbTableSpList, List<String> chkInfo) {
		boolean temp = true;
		if(chkInfo.size() > 0) {
			for(int j=0; j<dbTableSpList.size(); j++) {
				if(dbTableSpList.get(j).getTablespace().equals(chkInfo.get(0)) && dbTableSpList.get(j).getUsepercent() > Float.parseFloat(chkInfo.get(1)) ) {
					//임계치 이상
					System.out.println("임계치 이상 입니다.");
					temp = false;
				}
			}
		}
		return temp;
	}
}




	

