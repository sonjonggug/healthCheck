package com.watchDog.project.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class ChkDbDao {

	private SqlSessionFactory sqlSessionFactory = null;

	public ChkDbDao(SqlSessionFactory sqlSessionFactory){
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	public int dbMergeInto() throws Exception{
		
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();

		try {
			result = session.insert("chkDb.chkDbMergeInto");
		}finally {
			session.commit();
			System.out.println("dbInsert: "+result);
			session.close();
		}

		return result;
	}
	
	public int dbInsert() {
		
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			result = session.insert("chkDb.chkDbInsert");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			session.commit();
			System.out.println("dbInsert: "+result);
			session.close();
		}
		
		return result;
	}
	
	public int dbUpdate() {
		
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();

		try {
			result = session.update("chkDb.chkDbUpdate");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.commit();
			System.out.println("dbUpdate: "+result);
			session.close();
		}

		return result;
	}
	
	public int dbDelete() {
		
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();

		try {
			result = session.delete("chkDb.chkDbDelete");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.commit();
			System.out.println("dbDelete: "+result);
			session.close();
		}

		return result;
	}
}
