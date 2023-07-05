package com.watchDog.project.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.watchDog.project.model.EventDbVo;
import com.watchDog.project.model.ProcStatusVo;
import com.watchDog.project.model.ResourceStatusVo;
import com.watchDog.project.model.ServerStatusVo;
import com.watchDog.project.model.SmsUserDbVo;
import com.watchDog.project.utill.DateTime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaveDbDao {

	private SqlSessionFactory sqlSessionFactory = null;

	public SaveDbDao(SqlSessionFactory sqlSessionFactory){
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	
	public int serverUpdate (ServerStatusVo serverStatusVo) throws Exception{
		
		log.info("서버 정보 업데이트 -----> " + "Start");
		
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();

		try {
			result = session.insert("saveDb.saveDbMergeInto",serverStatusVo);
		}finally {
			session.commit();			
			session.close();
			log.info("서버 정보 업데이트 결과 -----> " + result);
		}

		return result;
	}

	public int updateServerDate (String sid) throws Exception{
		
		log.info("와치독 동작 시간 업데이트 -----> " + "Start");
		
		ServerStatusVo serStatusVo = new ServerStatusVo();
		
		serStatusVo.setSid(sid);
		serStatusVo.setConnDate(DateTime.nowDate());
		
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();

		try {
			result = session.insert("saveDb.updateServerDate",serStatusVo);
		}finally {
			session.commit();			
			session.close();
			log.info("와치독 동작 시간 업데이트 -----> " + result);
		}

		return result;
	}	
	
	/**
	 * 프로세스 목록 확인
	 * @param sid
	 * @return
	 * @throws Exception
	 */
	public List<ProcStatusVo> selectProcList(String sid) throws Exception{
									
			SqlSession session = sqlSessionFactory.openSession();
			
			List<ProcStatusVo> result = new ArrayList<ProcStatusVo>();
			
			try {
				result = session.selectList("saveDb.selectProcList",sid);												
				
			}finally {								
				session.close();
				log.info("프로세스 감시 목록 ----> " + result.size());
			}
	
			return result;
		}
	
	
	/**
	 * 디스크 목록 확인
	 * @param sid
	 * @return
	 * @throws Exception
	 */
	public List<ResourceStatusVo> selectDiskList() throws Exception{
			
			log.info("디스크 목록 확인 ----> " + "Start");
		
			SqlSession session = sqlSessionFactory.openSession();
			
			List<ResourceStatusVo> result = new ArrayList<ResourceStatusVo>();
			
			try {
				result = session.selectList("saveDb.selectDiskList");												
				
			}finally {								
				session.close();
				log.info("디스크 목록 확인 ----> " + result.size());
			}
	
			return result;
		}	

	/**
	 * 첫 애플리케케이션 동작 시 
	 * 프로세스 목록 읽은 후 저장
	 * @param procList
	 * @return
	 * @throws Exception
	 */
	public int firstInsertProcList (List<ProcStatusVo> procList) throws Exception{
			
			log.info("프로세스 목록 인서트 -----> " + "Start");
		
			int result = 0;
			
			SqlSession session = sqlSessionFactory.openSession();
	
			try {	
																				
				for(ProcStatusVo procStatusVo : procList) {					
			     	result += session.insert("saveDb.insertProcList", procStatusVo);				    
				 }
					
				 session.commit();
				 log.info("프로세스 목록 인서트 결과 -----> " + result);
				 
			} catch (Exception e) {
				session.rollback();
			    log.error("프로세스 목록 인서트 중 예외 발생", e);
			} finally {				
				 if (session != null) {
				    session.close();
				  }
			}
			
			return result;
	}		

	/**
	 * 첫 애플리케케이션 동작 시 
	 * 프로세스 목록 삭제
	 * @param procList
	 * @return
	 * @throws Exception
	 */
	public int firstDeleteProcList (String sid) throws Exception{
			
			log.info("프로세스 목록 삭제 -----> " + "Start");
		
			int result = 0;
			
			SqlSession session = sqlSessionFactory.openSession();
	
			try {	
																				
				result = session.delete("saveDb.deleteProcList", sid);
												
								
				 session.commit();
				 log.info("프로세스 목록 삭제 결과 -----> " + result);
				 
			} catch (Exception e) {
				session.rollback();
			    log.error("프로세스 목록 삭제 중 예외 발생", e);
			} finally {				
				 if (session != null) {
				    session.close();
				  }
			}
			
			return result;
	}		

	
	/**
	 * 첫 애플리케케이션 동작 시 
	 * 디스크 목록 읽은 후 저장
	 * @param procList
	 * @return
	 * @throws Exception
	 */
	public int firstInsertDiskList (List<ResourceStatusVo> diskList) throws Exception{
			
			log.info("디스크 목록 인서트 -----> " + "Start");
		
			int result = 0;
			
			SqlSession session = sqlSessionFactory.openSession();
	
			try {	
																				
				for(ResourceStatusVo reStatusVo : diskList) {					
			     	result += session.insert("saveDb.insertDiskList", reStatusVo);				    
				 }
					
				 session.commit();
				 log.info("디스크 목록 인서트 결과 -----> " + result);
				 
			} catch (Exception e) {
				session.rollback();
			    log.error("디스크 목록 인서트 중 예외 발생", e);
			} finally {				
				 if (session != null) {
				    session.close();
				  }
			}
			
			return result;
	}			
	
	/**
	 * 첫 애플리케케이션 동작 시 
	 * 디스크 목록 삭제
	 * @param procList
	 * @return
	 * @throws Exception
	 */
	public int firstDeleteDiskList (String diskName) throws Exception{
			
			log.info("디스크 목록 삭제 -----> " + "Start");
		
			int result = 0;
									
			SqlSession session = sqlSessionFactory.openSession();
			
			ResourceStatusVo resStatusVo = new ResourceStatusVo();
			
			resStatusVo.setResType(diskName);
			
			System.out.println(resStatusVo.getResType());
			
			try {	
																				
				 result = session.delete("saveDb.deleteDiskList", resStatusVo);												
								
				 session.commit();
				 log.info("디스크 목록 삭제 결과 -----> " + result);
				 
			} catch (Exception e) {
				session.rollback();
			    log.error("디스크 목록 삭제 중 예외 발생", e);
			} finally {				
				 if (session != null) {
				    session.close();
				  }
			}
			
			return result;
	}			
	
	/**
	 * 프로세스 상태 업데이트
	 * @param procList
	 * @return
	 * @throws Exception
	 */
	public int updateProcList (List<ProcStatusVo> procList) throws Exception{
			
			log.info("프로세스 상태 업데이트 -----> " + "Start");
			
			int result = 0;
			
			SqlSession session = sqlSessionFactory.openSession();
	
			try {				
				for(ProcStatusVo procStatusVo : procList) {
			     	result += session.insert("saveDb.updateProcList", procStatusVo);				    
				 }
				
				 session.commit();
				 log.info("프로세스 상태 업데이트 결과 -----> " + result);
				 
			} catch (Exception e) {
				session.rollback();
			    log.error("프로세스 상태 업데이트 중 예외 발생", e);
			} finally {				
				 if (session != null) {
				    session.close();
				  }
			}
			
			return result;
	}	
	
	/**
	 * 프로세스 자원 사용량 업데이트
	 * @param reStatusVo
	 * @return
	 * @throws Exception
	 */
	public int updateResource (ResourceStatusVo reStatusVo) throws Exception{
				
		log.info("자원 사용량 "+reStatusVo.getResType()+" 업데이트 -----> " + "Start");
			
		
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();

		try {
			result = session.insert("saveDb.updateResource",reStatusVo);
		}finally {
			session.commit();
			log.info("자원 사용량 "+reStatusVo.getResType()+" 업데이트 -----> " + result);
			session.close();
		}

		return result;
	}
	
	/**
	 * 프로세스 디스크 사용량 업데이트
	 * @param reStatusVo
	 * @return
	 * @throws Exception
	 */
	public int updateDiskList (List<ResourceStatusVo> reStatusVo) throws Exception{
				
		log.info("디스크 사용량 업데이트 -----> " + "Start");
			
		
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();

		try {
			
			for(ResourceStatusVo rsStatusVo : reStatusVo) {
		     	result += session.update("saveDb.updateDiskResource", rsStatusVo);				    
			 }
			
		}finally {
			session.commit();
			log.info("디스크 사용량 업데이트 -----> " + result);
			session.close();
		}

		return result;
	}
	
	/**
	 * 프로세스 자원 사용량 업데이트 (일별)
	 * @param reStatusVo
	 * @return
	 * @throws Exception
	 */
	public int updateResourceDay (ResourceStatusVo reStatusVo) throws Exception{
				
		log.info("자원 사용량 Day "+reStatusVo.getResType()+" 업데이트 -----> " + "Start");
		
		reStatusVo.setResDate(DateTime.nowDateHour());
		
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();

		try {
			result = session.insert("saveDb.updateResourceDay",reStatusVo);
		}finally {
			session.commit();
			log.info("자원 사용량 Day "+reStatusVo.getResType()+" 업데이트 -----> " + result);
			session.close();
		}

		return result;
	}
	
	/**
	 * 프로세스 디스크 사용량 업데이트 (일별)
	 * @param reStatusVo
	 * @return
	 * @throws Exception
	 */
	public int updateDiskListDay (List<ResourceStatusVo> reStatusVo) throws Exception{
				
		log.info("디스크 사용량 Day 업데이트 -----> " + "Start");
					
		
		int result = 0;
		
		SqlSession session = sqlSessionFactory.openSession();

		try {
			
			for(ResourceStatusVo rsStatusVo : reStatusVo) {
		     	result += session.update("saveDb.updateResourceDay", rsStatusVo);				    
			 }
			
		}finally {
			session.commit();
			log.info("디스크 사용량 Day 업데이트 -----> " + result);
			session.close();
		}

		return result;
	}
	
	/**
	 * SMS 발송 유저 목록 조회
	 * @param sid
	 * @return
	 * @throws Exception
	 */
	public List<SmsUserDbVo> selectSmsUserList() {
			
			SqlSession session = sqlSessionFactory.openSession();
			
			List<SmsUserDbVo> result = new ArrayList<SmsUserDbVo>();
			
			try {
				result = session.selectList("saveDb.selectSmsUserList");
				session.commit();				
			} catch (Exception e) {
				session.rollback();
			    log.error("SMS 발송 유저 목록 조회 중 예외 발생", e);	
			}finally {								
				 if (session != null) {
					    session.close();
					  }
			}
	
			return result;
		}

	
	/**
	 * 이벤트 등록
	 * @param procList
	 * @return
	 * @throws Exception
	 */
	public int updateEvent (EventDbVo eventDbVo) throws Exception{
			
			log.info("이벤트 등록 -----> " + "Start");
		
			int result = 0;
						
			SqlSession session = sqlSessionFactory.openSession();
			
			try {																														
			     result = session.insert("saveDb.updateEvent", eventDbVo);				    				 					
				 session.commit();
				 log.info("이벤트 등록 결과 -----> " + result);				 
			} catch (Exception e) {
				session.rollback();
			    log.error("이벤트 등록 중 예외 발생", e);
			} finally {				
				 if (session != null) {
				    session.close();
				  }
			}
			
			return result;
	}	
	
	/**
	 * 이벤트 등록
	 * @param procList
	 * @return
	 * @throws Exception
	 */
	public int insertEventLog (EventDbVo eventDbVo) throws Exception{
			
			log.info("이벤트 로그 등록 -----> " + "Start");
		
			int result = 0;
								
			SqlSession session = sqlSessionFactory.openSession();
			
			try {																														
			     result = session.insert("saveDb.insertEventLog", eventDbVo);				    				 					
				 session.commit();
				 log.info("이벤트 로그 등록 -----> " + result);				 
			} catch (Exception e) {
				session.rollback();
			    log.error("이벤트 로그 등록 중 예외 발생", e);
			} finally {				
				 if (session != null) {
				    session.close();
				  }
			}
			
			return result;
	}	
}
