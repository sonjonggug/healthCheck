package com.watchDog.project.utill;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DbConnectionFactory {
	private static SqlSessionFactory chkDbSqlSessionFactory;
	private static SqlSessionFactory saveDbSqlSessionFactory;
	private static SqlSessionFactory smsDbSqlSessionFactory;
	
	public static SqlSessionFactory getChkDbSqlSessionFactory() {
		
		if (chkDbSqlSessionFactory == null) {
			chkDbSqlSessionFactory = new DbConnectionFactory().createSession("config/chkDbConfig.xml");
		}
		
        return chkDbSqlSessionFactory;
    }
	
	public static SqlSessionFactory getDbSaveSqlSessionFactory() {
		
		if (saveDbSqlSessionFactory == null) {
			saveDbSqlSessionFactory = new DbConnectionFactory().createSession("config/saveDbConfig.xml");
		}
		
		return saveDbSqlSessionFactory;
	}
	
	public static SqlSessionFactory getDbSmsSqlSessionFactory() {
		
		if (smsDbSqlSessionFactory == null) {
			smsDbSqlSessionFactory = new DbConnectionFactory().createSession("config/smsDbConfig.xml");
		}
		
		return smsDbSqlSessionFactory;
	}
	

	private SqlSessionFactory createSession(String path) {
		
		SqlSessionFactory sqlSessionObject = null;
		
		try {
			String resource = path;
			Reader reader = Resources.getResourceAsReader(resource);

			sqlSessionObject = new SqlSessionFactoryBuilder().build(reader);
			
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
		} catch (IOException iOException) {
			iOException.printStackTrace();
		}
		
		return sqlSessionObject;
		
	}
    
}
