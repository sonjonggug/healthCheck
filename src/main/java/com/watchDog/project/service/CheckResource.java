package com.watchDog.project.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.watchDog.project.dao.SaveDbDao;
import com.watchDog.project.model.ResourceStatusVo;
import com.watchDog.project.utill.DbConnectionFactory;
import com.watchDog.project.utill.Init;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CheckResource {
	
	
	@Value("${dev.server.id}")
	 String id;
	
	@Value("${dev.server.pw}")
	 String pw;
	
	@Value("${dev.server.ip}")
	 String ip;
	
	@Value("${dev.server.port}")
	 int port;
	
	@Value("${rid.name}")
	 String rid;
	
	@Value("${sid}")
	 String sid;

	@Value("${disk.count}")
	 int diskCount;
	
	@Value("${mem.fail}")
	 int memFail;
	
	@Value("${cpu.fail}")
	 int cpuFail;
	
	@Value("${disk.fail}")
	 int diskFail;	
	

			
	/**
	 * 서버 메모리 사용량 체크
	 * @return
	 * @throws Exception
	 */
	public boolean checkRamUsed(){
		
	try {
		
		SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
	
		log.info("메모리 사용량 체크 -----> " + "Start");
								
		// JSch 라이브러리로 SSH 세션을 생성
	    JSch jsch = new JSch();
	    Session session = jsch.getSession(id, ip , port);
	    session.setPassword(pw);
	    
	    // 호스트 키 확인을 생략.
	    java.util.Properties config = new java.util.Properties();
	    config.put("StrictHostKeyChecking", "no");
	    session.setConfig(config);
	    
	    // SSH 연결을 시도.
	    session.connect();  
	    
	    // 채널 열기
	    Channel channel = session.openChannel("shell");
	    
	    // IO 스트림 설정
	    OutputStream outputStream = channel.getOutputStream();
	    InputStream inputStream = channel.getInputStream();
	    
	    // 채널 연결
	    channel.connect();
	    	
	    // 셸 입력
	    
	    outputStream.write(("free -h | awk 'NR==2 {print $3}'" + "\n").getBytes()); // 사용중인 램 용량(GB) 체크 ( available은 시스템에서 현재 사용 가능한 메모리의 양 )	    
//	    outputStream.write(("df -h | awk 'NR>1 && int(substr($5, 1, length($5)-1)) > 80 {gsub(/%/, \"%_\"); print}'" + "\n").getBytes()); // 디스크 사용량 80퍼 초과 시 , 값 추출을 위해 사용량 %_ 으로 출력후 변환
     	     	
	    // 셸 종료
	    outputStream.write("exit\n".getBytes());
	    outputStream.flush();

	    // 결과 출력
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	    
	    //값 담을 변수들
	    String line;	    
	    String ramUsed = "";	    
	    	    	    
	    // line별 읽어들이면서 ram 사용량 (Gi) , cpu사용량(%!) , disk사용량({) 확인 
	    while ((line = bufferedReader.readLine()) != null) {	    	
	    	if(line.contains("Gi")) {	    		
	    		ramUsed=line.replace("Gi", "");	    		
	    	} 	    		    	       
	    }
	    
	    // 1분마다 메모리 사용량 누적 저장 
	    Init.ramUsed += Double.parseDouble(ramUsed);	        
		
	    // 현재 ram 사용량이 ramMaxUsed보다 클 시 값 저장 
	    if(Double.parseDouble(ramUsed) > Init.ramMaxUsed) {
	    	Init.ramMaxUsed = Double.parseDouble(ramUsed);
	    }
	    	    	    
	    // 10분 마다 각 램 평균,최대값 DB update 
	    if(Init.usedCount == 10) {
	    	ResourceStatusVo reStatusVo  = new ResourceStatusVo();
	    	reStatusVo.setRid(rid+"RAM");
	    	reStatusVo.setSid(sid);
	    	reStatusVo.setResType("RAM");
	    	reStatusVo.setResAvg(Init.ramUsed / 10); 
	    	reStatusVo.setResPeak(Init.ramMaxUsed);
	    	reStatusVo.setResPath("");
	    	if(Init.ramUsed / 10 > memFail) { // 램 평균 사용량이 임계치 이상이면 장애
	    		reStatusVo.setResState("N");
	    	}else {
	    		reStatusVo.setResState("Y");
	    	}
	    	saveDbDao.updateResource(reStatusVo);
	    	
	    }
	    
	    	    
	    // 자원 해제
	    bufferedReader.close();
	    inputStream.close();
	    outputStream.close();
	    channel.disconnect();
	    session.disconnect();      	    	    	
	    
	    log.info("메모리 사용량 체크 -----> " + ramUsed+"GB 사용중");
	    
		} catch (Exception e) {
			e.printStackTrace();
			return false;		
		}	
		return true;
    }
	
	
	/**
	 * 서버 CPU 사용량 체크
	 * @return
	 * @throws Exception
	 */
	public boolean checkCpuUsed(){
		
	try {
			
		log.info("CPU 사용량 체크 -----> " + "Start");
								
		// JSch 라이브러리로 SSH 세션을 생성
	    JSch jsch = new JSch();
	    Session session = jsch.getSession(id, ip , port);
	    session.setPassword(pw);
	    
	    // 호스트 키 확인을 생략.
	    java.util.Properties config = new java.util.Properties();
	    config.put("StrictHostKeyChecking", "no");
	    session.setConfig(config);
	    
	    // SSH 연결을 시도.
	    session.connect();  
	    
	    // 채널 열기
	    Channel channel = session.openChannel("shell");
	    
	    // IO 스트림 설정
	    OutputStream outputStream = channel.getOutputStream();
	    InputStream inputStream = channel.getInputStream();
	    
	    // 채널 연결
	    channel.connect();
	    
	    // 셸 입력	    	    
	    outputStream.write(("top -bn 1 | awk '/%Cpu/ { print $2 \"%!\" }'" + "\n").getBytes()); // CPU 사용률 , 값 추출을 위해 사용량 %! 으로 출력후 변환
	    
     	     	
	    // 셸 종료
	    outputStream.write("exit\n".getBytes());
	    outputStream.flush();

	    // 결과 출력
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	    
	    //값 담을 변수들
	    String line;	    	    
	    String cpuUsed = "";
	    	    	    
	    // line별 읽어들이면서 ram 사용량 (Gi) , cpu사용량(%!) , disk사용량({) 확인 
	    while ((line = bufferedReader.readLine()) != null) {	    	
	    	if(line.contains("%!")) {	    		
	    		cpuUsed=line.replace("%!", "");	    			    	    		
	    	};	    		    	       
	    }
	    
	    
	    // 1분마다 CPU 사용량 누적 저장 
	    Init.cpuUsed += Double.parseDouble(cpuUsed);	        
		
	    // 현재 CPU 사용량이 cpuMaxUsed보다 클 시 값 저장 
	    if(Double.parseDouble(cpuUsed) > Init.cpuMaxUsed) {
	    	Init.cpuMaxUsed = Double.parseDouble(cpuUsed);
	    }
	    
	    SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
	    
	    // 10분 마다 각 CPU 평균,최대값 DB update 
	    if(Init.usedCount == 10) {
	    	ResourceStatusVo reStatusVo  = new ResourceStatusVo();
	    	reStatusVo.setRid(rid+"CPU");
	    	reStatusVo.setSid(sid);
	    	reStatusVo.setResType("CPU");
	    	reStatusVo.setResAvg(Init.cpuUsed / 10); 
	    	reStatusVo.setResPeak(Init.cpuMaxUsed);
	    	reStatusVo.setResPath("");
	    	if(Init.cpuUsed / 10 > cpuFail) { // CPU 평균 사용량이 임계치 이상이면 장애
	    		reStatusVo.setResState("N");
	    	}else {
	    		reStatusVo.setResState("Y");
	    	}
	    	saveDbDao.updateResource(reStatusVo);
	    	
	    }
	    
	    // 자원 해제
	    bufferedReader.close();
	    inputStream.close();
	    outputStream.close();
	    channel.disconnect();
	    session.disconnect();      	    	    	
	    
	    log.info("CPU 사용량 체크 -----> " + cpuUsed+"% 사용중");
	    
		} catch (Exception e) {
			e.printStackTrace();
			return false;		
		}	
		return true;
    }
	
	
	/**
	 * 서버 디스크 사용량 체크
	 * @return
	 * @throws Exception
	 */
	public boolean checkDiskUsed(){
		
	try {
		
		SaveDbDao saveDbDao = new SaveDbDao(DbConnectionFactory.getDbSaveSqlSessionFactory());
		
		log.info("디스크 사용량 체크 -----> " + "Start");
								
		// JSch 라이브러리로 SSH 세션을 생성
	    JSch jsch = new JSch();
	    Session session = jsch.getSession(id, ip , port);
	    session.setPassword(pw);
	    
	    // 호스트 키 확인을 생략.
	    java.util.Properties config = new java.util.Properties();
	    config.put("StrictHostKeyChecking", "no");
	    session.setConfig(config);
	    
	    // SSH 연결을 시도.
	    session.connect();  
	    
	    // 채널 열기
	    Channel channel = session.openChannel("shell");
	    
	    // IO 스트림 설정
	    OutputStream outputStream = channel.getOutputStream();
	    InputStream inputStream = channel.getInputStream();
	    
	    // 채널 연결
	    channel.connect();
	    
	    // 디스크 목록 가져오기
	    List<ResourceStatusVo> diskList = saveDbDao.selectDiskList();
	    
	    // 디스크 목록 셸 입력 , 디스크 사용량 80퍼 초과 시 에러
	    for(int i = 0; i < diskList.size(); i++) {	    	
	    	outputStream.write(("df --output=pcent " + diskList.get(i).getResPath() + " | tail -n 1 ;" + "\n").getBytes()); 
	    }
	    
	    	    
	    // 셸 종료
	    outputStream.write("exit\n".getBytes());
	    
	    outputStream.flush();

	    // 결과 출력
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	    
	    //값 담을 변수들
	    String line;	    
	    
	    // disk 사용량을 담을 리스트
	    List<ResourceStatusVo> diskUsed = new ArrayList<ResourceStatusVo>();
	    
	    int num = 0 ;
	    
	    // line별 읽어들이면서 disk 사용량 확인 | 디스크는 평균 , 피크 의미없어서 바로 체크
	    while ((line = bufferedReader.readLine()) != null) {	    	
	    	if (line.contains("%")) {	    		
	    		ResourceStatusVo disk = new ResourceStatusVo();
	    		disk.setRid(diskList.get(num).getRid().trim());	    			    		
	    		disk.setResAvg(Integer.valueOf(line.trim().replace("%", "")));
	    		disk.setResPeak(Integer.valueOf(line.trim().replace("%", "")));
	    		if(Integer.valueOf(line.trim().replace("%", "")) > diskFail) { // 디스크 사용량이 임계치 이상이면 장애
	    			disk.setResState("N");
	    		}else {
	    			disk.setResState("Y");
	    		}
	    		diskUsed.add(disk);
	    		num++;
	    	}	    		    	       
	    }
	    	        
		    	    	    	    	    	    
	    for(int i =0; i < diskUsed.size(); i++) {	    	
//	    	System.out.println("diskRid : "+diskUsed.get(i).getRid());
//	    	System.out.println("diskPath : "+diskUsed.get(i).getResPath());
//	    	System.out.println("diskAvg : "+diskUsed.get(i).getResAvg());
//	    	System.out.println("diskPeak : "+diskUsed.get(i).getResPeak());
	    }
	    
	    if(Init.usedCount == 10) {
	    	saveDbDao.updateDiskList(diskUsed);
	    }
	    
	    // 자원 해제
	    bufferedReader.close();
	    inputStream.close();
	    outputStream.close();
	    channel.disconnect();
	    session.disconnect();      	    	    	
	    	    
	    
	    log.info("디스크 사용량 체크 -----> " + "End");
	    
		} catch (Exception e) {
			e.printStackTrace();
			return false;		
		}	
		return true;
    }
}