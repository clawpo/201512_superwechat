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
public class DownloadAllGroupTask extends BaseActivity {
    public static final String TAG = DownloadAllGroupTask.class.getName();
    Context mContext;
    String userName;
    String path;

    public DownloadAllGroupTask(Context context,String userName) {
        this.mContext = context;
        this.userName = userName;
        initPath();
    }

    private void initPath(){
        try {
            path = new ApiParams()
                    .with(I.User.USER_NAME, userName)
                    .getRequestUrl(I.REQUEST_DOWNLOAD_GROUPS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(){
        executeRequest(new GsonRequest<GroupBean[]>(path,GroupBean[].class,
                responseDownloadAllGroupListener(), errorListener()));
    }

    private Response.Listener<GroupBean[]> responseDownloadAllGroupListener() {
        return new Response.Listener<GroupBean[]>(){
            @Override
            public void onResponse(GroupBean[] groupList) {
                if(groupList==null){
                    return;
                }
                ArrayList<GroupBean> list = FuLiCenterApplication.getInstance().getGroupList();
                ArrayList<GroupBean> groups = Utils.array2List(groupList);
                list.addAll(groups);
                Intent intent = new Intent("update_group");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }
}
