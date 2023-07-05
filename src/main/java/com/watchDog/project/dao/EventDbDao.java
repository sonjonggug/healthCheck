package com.watchDog.project.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.watchDog.project.model.EventDbVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventDbDao {

	private SqlSessionFactory sqlSessionFactory = null;

	public EventDbDao(SqlSessionFactory sqlSessionFactory){
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	

	
	/**
	 * 이벤트 등록
	 * @param procList
	 * @return
	 * @throws Exception
	 */
	public int insertEvent (EventDbVo eventDbVo) throws Exception{
			
			log.info("이벤트 인서트 -----> " + "Start");
		
			int result = 0;
			
			System.out.println(eventDbVo.getEid());
			System.out.println(eventDbVo.getEventMsg());
			System.out.println(eventDbVo.getEventDate());
			System.out.println(eventDbVo.getEventType());
			System.out.println(eventDbVo.getSid());
			
			SqlSession session = sqlSessionFactory.openSession();
			
			try {																														
			     result = session.insert("eventDb.insertEvent", eventDbVo);				    				 					
				 session.commit();
				 log.info("이벤트 인서트 결과 -----> " + result);				 
			} catch (Exception e) {
				session.rollback();
			    log.error("이벤트 인서트 중 예외 발생", e);
			} finally {				
				 if (session != null) {
				    session.close();
				  }
			}
			
			return result;
	}	
	

}
