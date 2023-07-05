package com.watchDog.project.utill;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.watchDog.project.WatchDogApplication;
import com.watchDog.project.dao.SaveDbDao;
import com.watchDog.project.model.ProcStatusVo;
import com.watchDog.project.model.ResourceStatusVo;
import com.watchDog.project.model.ServerStatusVo;

@Component
public class Init {
					
	@Value("${sid}") // WatchDog 서버 고유 ID
	private String sid ;
	
	@Value("${rid.name}") // 서버 자원 사용량 고유 ID
	private String rid ;	
	
	@Value("${farm}") // 서버가 속한 팜
	private String farm ;
	
	@Value("${name}") // 서버 이름
	private String name ;
	
	@Value("${desc}") // 설명
	private String desc ;	

	@Value("${health.check.use.yn}")
	private String tcpChkYn; 			//L4 TCP 체크
		
	@Value("${process.id}")
	private String pid;
	
	@Value("${proc.count}")
	private int procCount;
	
	@Value("${disk.count}")
	private int diskCount;	
		 

	
	/**
	 * 와치독 정보 업데이트
	 * @throws Exception
	 */
	public void updateWatchDog() throws Exception {
		
		SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
				
		// 로컬 IP 
        InetAddress localHost = InetAddress.getLocalHost();            
        String ip = localHost.getHostAddress();
                 
        // properties 문자 ISO-8859-1 에서 UTF-8로 변경                                                                               
		String farmName = Encoding.encodingToUTF(farm);			
		String description = Encoding.encodingToUTF(desc);			
		String serverName = Encoding.encodingToUTF(name);
					            						
		ServerStatusVo serverStatusVo = new ServerStatusVo(sid,farmName,serverName,description,"Y",ip);						
							
		// 와치독 현재상태 업데이트
		saveDbDao.serverUpdate(serverStatusVo);
	}	
	
	/**
	 * 프로세스 정보 업데이트
	 * @throws Exception
	 */
	public void updateProc() throws Exception {
		
		SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
		
		
		// 프로세스 리스트 저장
	    Properties properties = new Properties();
        InputStream input = null;
        
        // Properties 파일을 로드합니다.	    	 
        input = WatchDogApplication.class.getClassLoader().getResourceAsStream("config.properties");
        properties.load(input);
        
        // 프로세스 정보 properties에서 받아와 DB 저장
        List<ProcStatusVo> procUpdateList = new ArrayList<ProcStatusVo>();
        
        for(int i = 1 ; i <= procCount; i++) {
        	ProcStatusVo proc = new ProcStatusVo();
        	List<String> list = Arrays.asList(Encoding.encodingToUTF(properties.getProperty("process"+i)).split("\\|")); //  | 기준으로 잘라서 리스트에 넣기
        	proc.setPid(pid+i);            	            
        	proc.setSid(sid);
        	proc.setProcName(list.get(0));
        	proc.setProcRoute(list.get(1));
        	proc.setProcRun(list.get(2));
        	proc.setProcStatus("Y");
        	procUpdateList.add(proc);
        	            	            	            	
        }
        
        // 애플리케이션 처음 시작시 프로세스 목록 삭제 후 다시 인서트
        saveDbDao.firstDeleteProcList(sid);  
        saveDbDao.firstInsertProcList(procUpdateList);
        
        input.close();
	}
	
	/**
	 * 디스크 정보 업데이트
	 * @throws Exception
	 */
	public void updateDisk() throws Exception {
		
		SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
		
		// 프로세스 리스트 저장
	    Properties properties = new Properties();
        InputStream input = null;
        
        // Properties 파일을 로드합니다.	    	 
        input = WatchDogApplication.class.getClassLoader().getResourceAsStream("config.properties");
        properties.load(input);
		
		
        // 디스크 정보 properties에서 받아와 DB 저장
        List<ResourceStatusVo> diskUpdateList = new ArrayList<ResourceStatusVo>();
        
        for(int i = 1 ; i <= diskCount; i++) {
        	ResourceStatusVo disk = new ResourceStatusVo();            	 
        	disk.setRid(rid+"DISK"+i);
        	disk.setSid(sid);
        	disk.setResPath(Encoding.encodingToUTF(properties.getProperty("disk"+i)));            	            
        	disk.setResState("Y");
        	disk.setResType("DISK");
        	            	            	
        	diskUpdateList.add(disk);
        	            	            	            	
        }	              
        		
        // 애플리케이션 처음 시작시 디스크 목록 삭제 후 다시 인서트
        saveDbDao.firstDeleteDiskList("DISK");                       
        saveDbDao.firstInsertDiskList(diskUpdateList);
        
        input.close();
	}
	
	
	/**
	 * 프로세스 상태 체크
	 * @throws Exception
	 */
	public void procCheck() throws Exception {
		
//		SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
//		
//		// 감시 할 프로세스 목록
//		List<ProcStatusVo> procList = saveDbDao.selectProcList(sid);
//		
//		// 프로세스 상태 체크
//		List<ProcStatusVo> procStatus = checkServerProcess.serverConnect(procList);						
//		
//		ProcStatusVo changeList = new ProcStatusVo();
//		
//		// 프로세스 상태가 다운이면 재기동
//		for(int i = 0; i < procStatus.size(); i++) { 				
//			if(procStatus.get(i).getProcStatus().equals("N")) {
//				 changeList = checkServerProcess.restart(procStatus.get(i));
//			}else {
//				 changeList = procStatus.get(i);
//			}
//			procStatus.set(i, changeList); // 변경된 리스트 값을 set			
//		}  
//		saveDbDao.updateProcList(procStatus); // 변경된 프로세스 상태값 업데이트
	}
}
