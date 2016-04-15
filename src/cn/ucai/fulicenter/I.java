package cn.ucai.fulicenter;

/**
 * Created by sks on 2016/4/5.
 */
public class I {


    public static final String AVATAR_PATH = "D:/project/github/SuperWeChat_Database/";
    public static final String PAGE_ID = "pageId";
    public static final String PAGE_SIZE = "pageSize";

    public static class User {
        public static final String ID = "id";
        public static final String UID = "uid";
        public static final String USER_NAME = "userName";
        public static final String NICK = "nick";
        public static final String AVATAR = "avatar";
        public static final String HEADER = "header";
        public static final String PASSWORD = "password";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String GROUPS = "groups";
        public static final String UN_READ_MSG_COUNT = "unreadMsgCount";
    }

    public static class Contact extends User {
        public static final String NAME = "name";
        public static final String MYUID = "myuid";
        public static final String CUID = "cuid";
        public static final String IS_GET_MY_LOCATION = "isGetMyLocation";
        public static final String IS_SHOW_MY_LOCATION = "isShowMyLocation";
    }

    public static class Group {
        public static final String ID = "id";// 主键
        public static final String GROUP_ID = "groupId";// 主键
        public static final String NAME = "name";// 群名
        public static final String GROUP_NAME = "groupName";// 群名，url中使用
        public static final String NEW_NAME = "new_name";// 群新名称
        public static final String AVATAR = "avatar";// 群图标
        public static final String INTRO = "intro";// 群简介
        public static final String OWNER = "owner";// 群主账号
        public static final String IS_PUBLIC = "isPublic";// 是否公开
        public static final String MODIFIED_TIME = "modifiedTime";// 群信息修改的时间，单位：毫秒
        public static final String MEMBERS = "members";// 群成员的账号
        public static final String IS_EXAME = "isExame";// 群成员的账号
    }

    /** 上传图片的类型：user_avatar或group_icon */
    public static final String AVATAR_TYPE = "avatarType";
    public static final String KEY_REQUEST = "request";
    public static final String REQUEST_SERVERSTATUS = "server_status";
    /**
     * 客户端发送的注册请求
     */
    public static final String REQUEST_REGISTER = "register";

    /**
     * 发送取消注册的请求
     */
    public static final String REQUEST_UNREGISTER = "unregister";

    /**
     * 客户端上传头像的请求
     */
    public static final String REQUEST_UPLOAD_AVATAR = "upload_avatar";
    /**
     * 客户端发送的登陆请求
     */
    public static final String REQUEST_LOGIN = "login";
    public static final String REQUEST_DOWNLOAD_AVATAR = "download_avatar";
    public static final String REQUEST_DOWNLOAD_GROUP_AVATAR = "download_group_avatar";
    public static final String DOWNLOAD_AVATAR_URL = FuLiCenterApplication.SERVER_ROOT +"?request="+REQUEST_DOWNLOAD_AVATAR+"&avatar=";
    public static final String ISON8859_1 = "iso8859-1";
    public static final String UTF_8 = "utf-8";
    public static final String REQUEST_DOWNLOAD_CONTACTS = "download_contacts";
    public static final String REQUEST_DOWNLOAD_CONTACT_LIST = "download_contact_list";
    public static final String REQUEST_DELETE_CONTACT = "delete_contact";
    public static final String REQUEST_ADD_CONTACT = "add_contact";
    public static final String REQUEST_FIND_USER = "find_user";
    public static final String REQUEST_DOWNLOAD_CONTACT = "download_contacts";
    public static final String REQUEST_UPLOAD_LOCATION = "upload_location";
    public static final String REQUEST_DOWNLOAD_LOCATION = "download_location";
    public static final String REQUEST_CREATE_GROUP = "create_group";
    public static final String REQUEST_ADD_GROUP_MEMBER = "add_group_member";
    public static final String REQUEST_ADD_GROUP_MEMBERS = "add_group_members";
    public static final String REQUEST_UPDATE_GROUP_NAME = "update_group_name";
    public static final String REQUEST_DOWNLOAD_GROUP_MEMBERS = "download_group_members";
    public static final String REQUEST_DELETE_GROUP_MEMBER = "delete_group_member";
    public static final String REQUEST_DELETE_GROUP = "delete_group";
    public static final String REQUEST_DOWNLOAD_GROUPS = "download_groups";
    public static final String REQUEST_FIND_PUBLIC_GROUPS = "download_public_groups";
    public static final String REQUEST_FIND_GROUP = "find_group_by_group_name";
}
