package com.watchDog.project.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.watchDog.project.model.SmsDbVo;

public class SmsDbDao {

	private SqlSessionFactory sqlSessionFactory = null;

	public SmsDbDao(SqlSessionFactory sqlSessionFactory){
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	public int dbInsert(SmsDbVo smsDbVo) {
        
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			result = session.insert("smsDb.smsDbInsert",smsDbVo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			session.commit();
			System.out.println("smsDbInsert: "+result);
			session.close();
		}
		
		return result;
	}
	
}
