package cn.ucai.fulicenter.bean;

import java.io.Serializable;

public class UserBean implements Serializable {

	private int id;
	private String result;
	private String userName;
	private String nick;
	private String password;
	private String avatar;

	/**
     * 对联系人按header分类，用于手机端右侧的字母栏快速定位联系人
     */
    private String header;

	private int unreadMsgCount;
	
	public UserBean(){
		
	}
	public UserBean(String userName){
		super();
		this.userName = userName;
	}
	public UserBean(String userName, String nick, String password) {
		super();
		this.userName = userName;
		this.nick = nick;
		this.password = password;
	}
	public UserBean(int id, String result, String userName, String nick,
					String password, String avatar, int unreadMsgCount) {
		super();
		this.id = id;
		this.result = result;
		this.userName = userName;
		this.nick = nick;
		this.password = password;
		this.avatar = avatar;
		this.unreadMsgCount = unreadMsgCount;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public int getUnreadMsgCount() {
		return unreadMsgCount;
	}
	public void setUnreadMsgCount(int unreadMsgCount) {
		this.unreadMsgCount = unreadMsgCount;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserBean other = (UserBean) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "UserBean [id=" + id + ", result=" + result + ", userName=" + userName + ", nick=" + nick + ", password="
				+ password + ", avatar=" + avatar + ", header=" + header +  ", unreadMsgCount=" + unreadMsgCount + "]";
	}
	
}
