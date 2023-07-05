package com.watchDog.project.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.watchDog.project.model.SmsUserDbVo;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class SmsDbDao {

	private SqlSessionFactory sqlSessionFactory = null;

	public SmsDbDao(SqlSessionFactory sqlSessionFactory){
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	/**
	 * SMS 전송 
	 * @param smsUserDbVo
	 * @return
	 */
	public int dbInsert(List<SmsUserDbVo> smsUserDbVo) {
        
		log.info("SMS 전송 시작 ------> " + "Start");
		
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			
			for(SmsUserDbVo smsVo : smsUserDbVo) {
				result += session.insert("smsDb.smsDbInsert",smsVo);
				Thread.sleep(1000); // pk가 TO_CHAR(SYSDATE, 'yyyymmddHH24MISS') 이기 떄문에 1초 텀 두고 인서트
			 }
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			session.commit();
			log.info("SMS 전송 결과 ------> " + result);
			session.close();
		}
		
		return result;
	}
	
}
