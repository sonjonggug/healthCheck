package com.watchDog.project.model;

public class ChkDbTableSpVo {
	private String tablespace;
	private double totalspace;
	private double usespace;
	private double freespace;
	private double usepercent;
	@Override
	public String toString() {
		return "ChkDbTableSpVo [tablespace=" + tablespace + ", totalspace=" + totalspace + ", usespace=" + usespace
				+ ", freespace=" + freespace + ", userpercent=" + usepercent + "]";
	}
	public String getTablespace() {
		return tablespace;
	}
	public void setTablespace(String tablespace) {
		this.tablespace = tablespace;
	}
	public double getTotalspace() {
		return totalspace;
	}
	public void setTotalspace(double totalspace) {
		this.totalspace = totalspace;
	}
	public double getUsespace() {
		return usespace;
	}
	public void setUsespace(double usespace) {
		this.usespace = usespace;
	}
	public double getFreespace() {
		return freespace;
	}
	public void setFreespace(double freespace) {
		this.freespace = freespace;
	}
	public double getUsepercent() {
		return usepercent;
	}
	public void setUsepercent(double usepercent) {
		this.usepercent = usepercent;
	}

}
