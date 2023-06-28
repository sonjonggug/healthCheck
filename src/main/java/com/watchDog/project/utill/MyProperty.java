package com.watchDog.project.utill;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;


@Getter
@PropertySource("classpath:config.properties")
@Component
public class MyProperty {
        
    @Value("${sid}")
    private String sid;
    
    @Value("${name}")
    private String name;
    
    @Value("${farm}")
    private String farm;
    
    @Value("${interval}")
    private String interval;
    
    @Value("${rerun}")
    private String rerun;
    
    @Value("${dbRetyr}")
    private String dbRetyr;
    
    @Value("${proc.count}")
    private String procCount;
    
    @Value("${cpu.fail}")
    private String cpuFail;
    
    @Value("${mem.fail}")
    private String memFail;
    
    @Value("${fail.time}")
    private String failTime;
    


}
