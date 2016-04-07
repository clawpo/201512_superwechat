package cn.ucai.superwechat.bean;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class ContactBean implements Serializable  {
	
	private String 		result;
	private int 		myuid;
	private int 		cuid;
    @JsonProperty("isGetMyLocation")
    private boolean 	isGetMyLocation;
    @JsonProperty("isShowMyLocation")
    private boolean 	isShowMyLocation;
	
	public ContactBean(){
	}
	
	public ContactBean(String result, int myuid, int cuid, 
			boolean isGetMyLocation, boolean isShowMyLocation) {
		super();
		this.result = result;
		this.myuid = myuid;
		this.cuid = cuid;
		this.isGetMyLocation = isGetMyLocation;
		this.isShowMyLocation = isShowMyLocation;
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public int getMyuid() {
		return myuid;
	}
	public void setMyuid(int myuid) {
		this.myuid = myuid;
	}
	public int getCuid() {
		return cuid;
	}
	public void setCuid(int cuid) {
		this.cuid = cuid;
	}
	@JsonIgnore
	public boolean isGetMyLocation() {
		return isGetMyLocation;
	}
	public void setIsGetMyLocation(boolean isGetMyLocation) {
		this.isGetMyLocation = isGetMyLocation;
	}
	@JsonIgnore
	public boolean isShowMyLocation() {
		return isShowMyLocation;
	}
	public void setIsShowMyLocation(boolean isShowMyLocation) {
		this.isShowMyLocation = isShowMyLocation;
	}

	@Override
	public String toString() {
		return "ContactBean [result=" + result + ", myuid=" + myuid + ", cuid=" + cuid + ", isGetMyLocation="
				+ isGetMyLocation + ", isShowMyLocation=" + isShowMyLocation + "]";
	}


	

}
