package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.activity.BaseActivity;
import cn.ucai.superwechat.bean.GroupBean;
import cn.ucai.superwechat.bean.UserBean;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by sks on 2016/4/5.
 */
public class DownloadAllGroupMembersTask extends BaseActivity {
    public static final String TAG = DownloadAllGroupMembersTask.class.getName();
    Context mContext;
    String groupId;
    String path;
    ArrayList<GroupBean> groupList;

    public DownloadAllGroupMembersTask(Context context, String groupId) {
        this.mContext = context;
        this.groupId = groupId;
        initPath();

    }

    private void initPath(){
        try {
            path = new ApiParams()
                    .with(I.Group.GROUP_ID, groupId)
                    .getRequestUrl(I.REQUEST_DOWNLOAD_GROUP_MEMBERS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(){
        executeRequest(new GsonRequest<UserBean[]>(path,UserBean[].class,
                responseDownloadGroupMembersListener(), errorListener()));
    }

    private Response.Listener<UserBean[]> responseDownloadGroupMembersListener() {
        return new Response.Listener<UserBean[]>(){
            @Override
            public void onResponse(UserBean[] userList) {
                if(userList==null){
                    return;
                }
                Log.e(TAG,"responseDownloadGroupMembersListener,userList.length="+userList.length);
                HashMap<String, ArrayList<UserBean>> groupMembers =
                        SuperWeChatApplication.getInstance().getGroupMembers();
                ArrayList<UserBean> users = Utils.array2List(userList);
                groupMembers.put(groupId,users);
                Intent intent = new Intent("update_group_member");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }
}
