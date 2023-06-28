package com.watchDog.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.watchDog.project.model.ProcStatusVo;
import com.watchDog.project.utill.Constans;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SendService {
	
	@Value("${dev.server.ip}")
	 String ip;
	
	@Value("${dev.server.port}")
	 int port;				
	
	public boolean SendRestartSuccess(ProcStatusVo proccessStatus) {
											
			log.info("메일 발송 -------> " + ip + ":" + port + " 에 " + proccessStatus.getProcName() + " " + Constans.RESTART_SUCESS );
				
		return true;
	}
	
	public boolean SendRestartFail(ProcStatusVo proccessStatus) {
				
		 
		
//			if (errCheckSum % 5 == 0) { // 5분마다 SMS 발송
//				log.info("메일 발송 -------> " + ip + ":" + port + " 에 " + proccessStatus.getProcName() + " " + Constans.RESTART_FAIL + " (경과시간 " + errCheckSum + " 분)");		    
//			} 
						
			log.info("-------> " + ip + ":" + port + " 에 " + proccessStatus.getProcName() + " " + Constans.RESTART_FAIL);	
		
			
		return true;
	}

	
	public void aas() {
		
		
		 // 프로퍼티 값을 출력합니다.
//		System.out.println(myComponent.getSid());
		
						
       
							
	}
	
}
