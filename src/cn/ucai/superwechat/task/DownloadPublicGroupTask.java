package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.activity.BaseActivity;
import cn.ucai.superwechat.bean.GroupBean;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by sks on 2016/4/5.
 */
public class DownloadPublicGroupTask extends BaseActivity {
    public static final String TAG = DownloadPublicGroupTask.class.getName();
    Context mContext;
    String userName;
    int pageId;
    int pageSize;
    String path;

    public DownloadPublicGroupTask(Context context, String userName, int pageId, int pageSize) {
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
                    .with(I.PAGE_ID, ""+pageId)
                    .with(I.PAGE_SIZE, ""+pageSize)
                    .getRequestUrl(I.REQUEST_FIND_PUBLIC_GROUPS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(){
        executeRequest(new GsonRequest<GroupBean[]>(path,GroupBean[].class,
                responseDownloadPublicGroupListener(), errorListener()));
    }

    private Response.Listener<GroupBean[]> responseDownloadPublicGroupListener() {
        return new Response.Listener<GroupBean[]>(){
            @Override
            public void onResponse(GroupBean[] groupList) {
                if(groupList==null){
                    return;
                }
                ArrayList<GroupBean> list = SuperWeChatApplication.getInstance().getPublicGroupList();
                ArrayList<GroupBean> groups = Utils.array2List(groupList);
                for(GroupBean g:groups){
                    if(!list.contains(g)){
                        list.add(g);
                    }
                }
                Intent intent = new Intent("update_public_group");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }
}
