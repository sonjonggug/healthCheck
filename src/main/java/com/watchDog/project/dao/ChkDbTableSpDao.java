package com.watchDog.project.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.watchDog.project.model.ChkDbTableSpVo;

public class ChkDbTableSpDao {

	private SqlSessionFactory sqlSessionFactory = null;

	public ChkDbTableSpDao(SqlSessionFactory sqlSessionFactory){
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	public List<ChkDbTableSpVo> chkDbTableSpSelect() {
		
		List<ChkDbTableSpVo> result = new ArrayList<ChkDbTableSpVo>();
		
		SqlSession session = sqlSessionFactory.openSession();
		//ChkDbTableSpVo chkDbTableSpVo = new ChkDbTableSpVo();
		
		try {
			result = session.selectList("chkDb.chkDbTableSpSelect");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			session.commit();
			for (Object data : result) {
	            System.out.println("selectList: "+data.toString());
	        }
			session.close();
		}
		
		return result;
	}
}
