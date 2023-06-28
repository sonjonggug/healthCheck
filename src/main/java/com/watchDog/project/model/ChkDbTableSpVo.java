package com.watchDog.project.model;

public class ChkDbTableSpVo {
	private String tablespace;
	private float totalspace;
	private float usespace;
	private float freespace;
	private float usepercent;
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
	public float getTotalspace() {
		return totalspace;
	}
	public void setTotalspace(float totalspace) {
		this.totalspace = totalspace;
	}
	public float getUsespace() {
		return usespace;
	}
	public void setUsespace(float usespace) {
		this.usespace = usespace;
	}
	public float getFreespace() {
		return freespace;
	}
	public void setFreespace(float freespace) {
		this.freespace = freespace;
	}
	public float getUsepercent() {
		return usepercent;
	}
	public void setUsepercent(float usepercent) {
		this.usepercent = usepercent;
	}

}
