package com.watchDog.project;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.watchDog.project.service.TCPHealthCheckService;
import com.watchDog.project.utill.Constans;
import com.watchDog.project.utill.Init;
import com.watchDog.project.utill.SmsUtill;
import com.watchDog.project.utill.Stat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class WatchDogApplication {
	
	
	private final TCPHealthCheckService tcpHealthCheckService;
	private final Init init;
	private final SmsUtill smsUtill;
	

	@Value("${health.check.use.yn}")
	private String tcpChkYn; 			//L4 TCP 체크
		
	
	
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
			
//			System.out.println(smsUtill.sendSMS(Constans.MSG_PROC_RESTART_FAIL , "ss"));
//			System.out.println(smsUtill.sendSMS(Constans.MSG_CPU_OVER , "ss"));
//			System.out.println(smsUtill.sendSMS(Constans.MSG_DISK_ACCESS_FAIL , "ss"));
//			System.out.println(smsUtill.sendSMS(Constans.MSG_SERVER_RESTART , "ss"));
//			System.out.println(smsUtill.sendSMS(Constans.MSG_SERVER_STOP , "ss"));
			
			
//			//L4 TCP 체크
			if("Y".equals(tcpChkYn)) {
				Thread tcpHealthCheckThread = new Thread(() -> tcpHealthCheckService.startServer());
				tcpHealthCheckThread.start();
			}
			
			init.updateWatchDog();
			init.updateProc();
			init.updateDisk();
			
			Stat.START = "Y"; // 이후 스케쥴러 사용 Y
								  
			} catch (Exception e) {
			  e.printStackTrace();
			  log.info("Exception Error -----------------> " + e);			  
			}				
	}		
}
