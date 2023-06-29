package com.watchDog.project;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.watchDog.project.dao.SaveDbDao;
import com.watchDog.project.model.ProcStatusVo;
import com.watchDog.project.model.ResourceStatusVo;
import com.watchDog.project.model.ServerStatusVo;
import com.watchDog.project.service.CheckServerProcess;
import com.watchDog.project.service.TCPHealthCheckService;
import com.watchDog.project.utill.DateTime;
import com.watchDog.project.utill.DbConnectionFactory;
import com.watchDog.project.utill.Init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class WatchDogApplication {
	
	private final CheckServerProcess checkServerProcess ;
	private final TCPHealthCheckService tcpHealthCheckService;
	
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
	
	public static void main(String[] args) {
		SpringApplication.run(WatchDogApplication.class, args);								
	}		
						
	/**
	 * 처음 application 실행 시 한번만 동작
	 * 이후 스케쥴러 작동
	 */
	@PostConstruct
	public void checkProcess() {
		
		log.info("WatchDog Start...");
				
		try {
			
			SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
			
			//L4 TCP 체크
			if("Y".equals(tcpChkYn)) {
				Thread tcpHealthCheckThread = new Thread(() -> tcpHealthCheckService.startServer());
				tcpHealthCheckThread.start();
			}
			
			
    	    Properties properties = new Properties();
            InputStream input = null;
            
            // Properties 파일을 로드합니다.	    	 
            input = WatchDogApplication.class.getClassLoader().getResourceAsStream("config.properties");
            properties.load(input);
            
            // 프로세스 리스트 저장 
            List<ProcStatusVo> procUpdateList = new ArrayList<ProcStatusVo>();
            
            // 프로세스 정보 properties에서 받아와 리스트 저장
            for(int i = 1 ; i <= procCount; i++) {
            	ProcStatusVo proc = new ProcStatusVo();
            	List<String> list = Arrays.asList(Init.encoding(properties.getProperty("process"+i)).split("\\|")); //  | 기준으로 잘라서 리스트에 넣기
            	proc.setPid(pid+i);            	            
            	proc.setSid(sid);
            	proc.setProcName(list.get(0)); // 이름
            	proc.setProcRoute(list.get(1)); // 경로
            	proc.setProcRun(list.get(2)); // 실행
            	proc.setProcStatus("N");
            	procUpdateList.add(proc);
            	            	            	            	
            }
            
            // 애플리케이션 처음 시작시 프로세스 목록 삭제 후 다시 인서트
            saveDbDao.firstDeleteProcList(sid);  
            saveDbDao.firstInsertProcList(procUpdateList);
                        
            // 디스크 리스트 저장
            List<ResourceStatusVo> diskUpdateList = new ArrayList<ResourceStatusVo>();
            
            // 디스크 정보 properties에서 받아와 DB 저장
            for(int i = 1 ; i <= diskCount; i++) {
            	ResourceStatusVo disk = new ResourceStatusVo();            	 
            	disk.setRid(rid+"RAM"+i);
            	disk.setSid(sid);
            	disk.setResPath(Init.encoding(properties.getProperty("disk"+i)));            	            
            	disk.setResState("Y");
            	disk.setResType("DISK");
            	            	            	
            	diskUpdateList.add(disk);
            	            	            	            	
            }	              
            input.close();
			
            // 애플리케이션 처음 시작시 디스크 목록 삭제 후 다시 인서트
            saveDbDao.firstDeleteDiskList("DISK");                       
            saveDbDao.firstInsertDiskList(diskUpdateList);                                       
			
			// 로컬 IP 
            InetAddress localHost = InetAddress.getLocalHost();            
            String ip = localHost.getHostAddress();
                     
            // properties 문자 ISO-8859-1 에서 UTF-8로 변경                                                                               
			String farmName = Init.encoding(farm);			
			String description = Init.encoding(desc);			
			String serverName = Init.encoding(name);
			
			// 와치독 상태 저장
			ServerStatusVo serverStatusVo = new ServerStatusVo(sid,farmName,serverName,description,"Y",ip,DateTime.nowDate(),DateTime.nowDate());						
								
			// 와치독 현재상태 업데이트
			saveDbDao.serverUpdate(serverStatusVo);
			
			// 감시 할 프로세스 목록
			List<ProcStatusVo> procList = saveDbDao.selectProcList(sid);
			
			// 프로세스 상태 체크 및 변경
			List<ProcStatusVo> procStatus = checkServerProcess.serverConnect(procList);						
			
			// 변경 될 프로세스 정보
			ProcStatusVo changeList = new ProcStatusVo();
			
			// 프로세스 상태가 다운이면 재기동
			for(int i = 0; i < procStatus.size(); i++) { 				
				if(procStatus.get(i).getProcStatus().equals("N")) {
					 changeList = checkServerProcess.restart(procStatus.get(i));
				}else {
					 changeList = procStatus.get(i);
				}
				procStatus.set(i, changeList); // 변경된 리스트 값을 set			
			}  
			saveDbDao.updateProcList(procStatus); // 변경된 프로세스 상태값 업데이트
			
				// 이후 스케쥴러 사용 Y
				Init.START = "Y";							  
			} catch (Exception e) {
			  e.printStackTrace();
			  log.info("Exception Error -----------------> ");			  
			}				
	}		
}
