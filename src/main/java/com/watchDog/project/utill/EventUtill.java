package com.watchDog.project.utill;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.watchDog.project.dao.EventDbDao;
import com.watchDog.project.dao.SaveDbDao;
import com.watchDog.project.model.EventDbVo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EventUtill {
					
	@Value("${sid}") // WatchDog 서버 고유 ID
	private String sid ;
	

		 

	

	
	/**
	 * 이벤트 정보 업데이트
	 * @param str
	 * @param proc
	 * @return
	 * @throws Exception
	 */
	public void updateEvent (String eid , String msg , String type) throws Exception {
		
		SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
		EventDbVo eventDbVo = new EventDbVo();
		
		eventDbVo.setEid(eid);
		eventDbVo.setSid(sid);
		eventDbVo.setEventMsg(msg);
		eventDbVo.setEventDate(DateTime.nowDate());
		eventDbVo.setEventType(type);
				
		
		saveDbDao.updateEvent(eventDbVo);
		
		 
	}

	/**
	 * 이벤트 로그 등록
	 * @param eid
	 * @param msg
	 * @param type
	 * @throws Exception
	 */
	public void insertEventLog (String eid , String msg , String type) throws Exception {
		
		SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
		EventDbVo eventDbVo = new EventDbVo();
		
		eventDbVo.setEid(eid);
		eventDbVo.setSid(sid);
		eventDbVo.setEventMsg(msg);
		eventDbVo.setEventDate(DateTime.nowDate());
		eventDbVo.setEventType(type);
				
		
		saveDbDao.insertEventLog(eventDbVo);
	}

}