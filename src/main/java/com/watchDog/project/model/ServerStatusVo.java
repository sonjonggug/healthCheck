package com.watchDog.project.model;

import lombok.Data;

@Data
public class ServerStatusVo {

	private String sid;
	private String farm;
	private String name;
	private String desc;
	private String status;
	private String ip;
	private String connDate;
	private String bootingDate;

	
	
public ServerStatusVo(String sid, String farm, String name, String desc,  String status , String ip, String connDate, String bootingDate) {
	super();
	this.sid = sid;
	this.farm = farm;
	this.name = name;
	this.desc = desc;
	this.status = status;
	this.ip = ip;
	this.connDate = connDate;
	this.bootingDate = bootingDate;
}
	

	

	

			
		
	
	
	
	
	
	
	
	
	
	
	
}
