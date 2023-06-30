package com.watchDog.project.utill;

import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;


@Getter
//@PropertySource("classpath:config.properties")
public class MyProperty {
        
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
    


}
