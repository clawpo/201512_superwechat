package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.GroupBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

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
                ArrayList<GroupBean> list = FuLiCenterApplication.getInstance().getPublicGroupList();
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
