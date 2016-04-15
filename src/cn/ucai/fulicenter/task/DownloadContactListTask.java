package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/4/5.
 */
public class DownloadContactListTask extends BaseActivity {
    public static final String TAG = DownloadContactListTask.class.getName();
    Context mContext;
    String userName;
    int pageId;
    int pageSize;
    String path;

    public DownloadContactListTask(Context context,String userName, int pageId, int pageSize) {
        this.mContext = context;
        this.userName = userName;
        this.pageId = pageId;
        this.pageSize = pageSize;
        initPath();
    }

    private void initPath(){
        try {
            path = new ApiParams()
                    .with(I.User.USER_NAME, userName)
                    .with(I.PAGE_ID, pageId + "")
                    .with(I.PAGE_SIZE, pageSize + "")
                    .getRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(){
        executeRequest(new GsonRequest<UserBean[]>(path,UserBean[].class,
                responseDownloadUserListListener(), errorListener()));
    }

    private Response.Listener<UserBean[]> responseDownloadUserListListener() {
        return new Response.Listener<UserBean[]>(){
            @Override
            public void onResponse(UserBean[] userList) {
                if(userList==null){
                    return;
                }
                ArrayList<UserBean> contactList = FuLiCenterApplication.getInstance().getContactList();
                ArrayList<UserBean> users = Utils.array2List(userList);
                contactList.clear();
                contactList.addAll(users);
                HashMap<String, UserBean> userBeanMap = FuLiCenterApplication.getInstance().getUserList();
                HashMap<String, UserBean> userMap = new HashMap<String, UserBean>();
                for (UserBean u : userList){
                    userMap.put(u.getUserName(),u);
                }
                userBeanMap.clear();
                userBeanMap.putAll(userMap);
                Intent intent = new Intent("update_contact_list");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }
}