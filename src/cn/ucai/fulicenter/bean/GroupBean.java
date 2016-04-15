package cn.ucai.fulicenter.bean;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * 群组实体类，存放所有群的信息
 * @author chen
 *
 */
public class GroupBean implements Serializable {
    private int         id;             //主键
    private String      name;           //群名
    private String      avatar;         //群图标
    private String      intro;          //群简介
    private String      owner;          //群主账号
    @JsonProperty("isPublic")
    private boolean     isPublic;       //是否公开
    @JsonProperty("isExame")
    private boolean     isExame;        //是否公开
    private String 		groupId;		//群组ID
    private int         modifiedTime;   //群信息修改的时间，单位：毫秒
    private String      members;        //存放群成员账号,格式:账号1,账号2,...
	
	public GroupBean(){
		
	}

	public GroupBean(String groupName){
		super();
		this.name = groupName;
	}
	
	public GroupBean (String groupId, String name, String intro,
			String owner, boolean isPublic, boolean isExame, String members) {
		super();
		this.groupId = groupId;
		this.name = name;
		this.intro = intro;
		this.owner = owner;
		this.isPublic = isPublic;
		this.isExame = isExame;
		this.members = members;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@JsonIgnore
	public boolean isPublic() {
		return isPublic;
	}

	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	@JsonIgnore
	public boolean isExame() {
		return isExame;
	}

	public void setIsExame(boolean isExame) {
		this.isExame = isExame;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(int modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public String getMembers() {
		return members;
	}

	public void setMembers(String members) {
		this.members = members;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		GroupBean other = (GroupBean) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GroupBean [id=" + id + ", name=" + name + ", avatar=" + avatar + ", intro=" + intro + ", owner=" + owner
				+ ", isPublic=" + isPublic + ", isExame=" + isExame + ", groupId=" + groupId + ", modifiedTime="
				+ modifiedTime + ", members=" + members + "]";
	}


	

}
