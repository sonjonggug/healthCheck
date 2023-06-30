package com.watchDog.project.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.watchDog.project.model.ProcStatusVo;
import com.watchDog.project.utill.DateTime;
import com.watchDog.project.utill.Stat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CheckServerProcess {
		
	
	@Value("${dev.server.id}")
	 String id;
	
	@Value("${dev.server.pw}")
	 String pw;
	
	@Value("${dev.server.ip}")
	 String ip;
	
	@Value("${dev.server.port}")
	 int port;
	
	@Value("${interval}")
	 int interval;
	
	

public List<ProcStatusVo> serverConnect(List<ProcStatusVo> procList) {
		
		log.info("프로세스 상태 체크 ... " );
				
		try {
										
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
        
        // SSH 채널을 열고, 실행할 명령어를 설정.
        Channel channel = session.openChannel("exec");  // 채널접속  exec = 명령을 요청하기 위함 
        ChannelExec channelExec = (ChannelExec) channel; // 명령 전송 채널사용
        channelExec.setPty(true);
        channelExec.setCommand("ps -ef | grep java"); // 실행시킬 명령어를 입력
      
        // 명령어의 실행 결과를 받을 준비.
        StringBuilder outputBuffer = new StringBuilder();
        InputStream in = channel.getInputStream();
        ((ChannelExec) channel).setErrStream(System.err); 
        
        // 명령어를 실행하고, 결과를 받아옴.
        channel.connect();  //실행
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                outputBuffer.append(new String(tmp, 0, i));
                if (i < 0) break;
            }
            // 명령어의 실행이 끝나면, 실행 결과를 분석
            if (channel.isClosed()) {            	            	
            	            	            	
            	for(int i = 0; i < procList.size(); i++) {              		            		            		
            		if(outputBuffer.toString().contains(procList.get(i).getProcName() +" ")) { // 정확한 프로세스 이름 검색 Ex: gasmon , gasmon_m
            			procList.get(i).setProcStatus("Y"); // 프로세스 상태 유무
            			procList.get(i).setUpTime(DateTime.nowDate()); // 프로세스 상태 유무
            			procList.get(i).setDownTime(""); // 프로세스 상태 유무
            			log.info("프로세스 정상---> " + procList.get(i).getProcName());            			
            			Stat.initCount(); // 에러 카운트 0으로 초기화
            		} else {
            			procList.get(i).setProcStatus("N");
            			log.info("프로세스 다운---> " + procList.get(i).getProcName());            			
            			            			          		
            		}            		                      	
            	}     
            	  // SSH 연결을 종료하고, 결과값을 반환
            	channel.disconnect();
            	session.disconnect();  
            	return procList;                                                                  
            }          		
        }  
			} catch (Exception e) {
				e.printStackTrace();
			}
				return procList;	
    }

/**
 * 프로세스가 다운됬을 시 재 실행
 * @return
 * @throws Exception
 */
public void start (ProcStatusVo proc) throws Exception{
	
	log.info("프로세스 기동... " + proc.getProcName());
							
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

    // 셸 실행
    channel.connect();
    
    // 셸 입력
 	outputStream.write((proc.getProcRoute() + "\n").getBytes());
 	outputStream.write((proc.getProcRun() + "\n").getBytes()); 
 	
    // 셸 종료
    outputStream.write("exit\n".getBytes());
    outputStream.flush();

    // 결과 출력
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    String line;
    
    while ((line = bufferedReader.readLine()) != null) {
        log.info(line);
    }
    
    Thread.sleep(interval);
    
    // 자원 해제
    bufferedReader.close();
    inputStream.close();
    outputStream.close();
    channel.disconnect();
    session.disconnect();      	    	    	    	         
			
}


	/**
	 * 프로세스가 다운됬을 시 재 실행
	 * @return
	 * @throws Exception
	 */
	public ProcStatusVo restart(ProcStatusVo proc) throws Exception{
		
		log.info("프로세스 다운 재기동... " + proc.getProcName());
								
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

	    // 셸 실행
	    channel.connect();
	    
	    // 셸 입력
     	outputStream.write((proc.getProcRoute() + "\n").getBytes());
     	outputStream.write((proc.getProcRun() + "\n").getBytes()); 
     	
	    // 셸 종료
	    outputStream.write("exit\n".getBytes());
	    outputStream.flush();

	    // 결과 출력
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	    String line;
	    
	    while ((line = bufferedReader.readLine()) != null) {
//	        log.info(line);
	    }
	    
	    Thread.sleep(interval);
	    
	    // 자원 해제
	    bufferedReader.close();
	    inputStream.close();
	    outputStream.close();
	    channel.disconnect();
	    session.disconnect();      	    	    	
	    	
	    
	        return restartCheck(proc);
				
    }
	
	
/**
 * 프로세스 재기동 제대로 됬는지 체크
 * @param list
 * @return
 * @throws Exception
 */
public ProcStatusVo restartCheck(ProcStatusVo proc) throws Exception{
		
		log.info("프로세스 재기동 체크 ... "+ proc.getProcName());
								
							
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
        
        // SSH 채널을 열고, 실행할 명령어를 설정.
        Channel channel = session.openChannel("exec");  // 채널접속  exec = 명령을 요청하기 위함 
        ChannelExec channelExec = (ChannelExec) channel; // 명령 전송 채널사용
        channelExec.setPty(true);
        
        // 실행시킬 명령어를 입력
        
        channelExec.setCommand("ps -ef | grep -cw " + proc.getProcName());	
                              
        // 명령어의 실행 결과를 받을 준비를 합니다.
        StringBuilder outputBuffer = new StringBuilder();
        InputStream in = channel.getInputStream();
        ((ChannelExec) channel).setErrStream(System.err); 
        
        // 명령어를 실행하고, 결과를 받아옴.
        channel.connect();  //실행
        byte[] tmp = new byte[1024];
        
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                outputBuffer.append(new String(tmp, 0, i));
                if (i < 0) break;
            }
            // 명령어의 실행이 끝나면, 실행 결과를 분석
            if (channel.isClosed()) {
                
            	
            	int procStatus = Integer.parseInt(outputBuffer.toString().trim());
             	            	
            	if(procStatus > 1) { // 프로세스 상태 1:down,2:live
            		log.info(proc.getProcName() +" 프로세스 재기동 성공");
            		proc.setProcStatus("Y"); // 프로세스 상태 유무
            		proc.setUpTime(DateTime.nowDate());
            		proc.setDownTime("");
            		Stat.initCount(); // 에러카운트 0으로 초기화
            	} else {
            		log.info(proc.getProcName() +" 프로세스 재기동 실패");            		        			
            		proc.setProcStatus("N"); // 프로세스 상태 유무
            		proc.setUpTime("");
            		proc.setDownTime(DateTime.nowDate());
        			Stat.addCount(); // 에러카운트 +1 
            		}            	            	            	            	
                             	
            	
                // SSH 연결을 종료하고, 결과값을 반환
                channel.disconnect();
                session.disconnect();
                
                return proc;
                
            }      
        } 
				
    }
	
    


			
 }





	

