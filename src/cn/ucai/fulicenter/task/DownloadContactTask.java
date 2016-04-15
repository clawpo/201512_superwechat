package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.HashMap;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.ContactBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;

/**
 * Created by sks on 2016/4/5.
 */
public class DownloadContactTask extends BaseActivity {
    public static final String TAG = DownloadContactTask.class.getName();
    Context mContext;
    String userName;
    int pageId;
    int pageSize;
    String path;

    public DownloadContactTask(Context context,String userName, int pageId, int pageSize) {
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
                    .getRequestUrl(I.REQUEST_DOWNLOAD_CONTACTS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(){
        executeRequest(new GsonRequest<ContactBean[]>(path,ContactBean[].class,
                responseDownloadContactListener(), errorListener()));
    }

    private Response.Listener<ContactBean[]> responseDownloadContactListener() {
        return new Response.Listener<ContactBean[]>(){
            @Override
            public void onResponse(ContactBean[] contactList) {
                if(contactList == null){
                    return;
                }
                HashMap<Integer, ContactBean> contacts = FuLiCenterApplication.getInstance().getContacts();
                HashMap<Integer,ContactBean> contactMap = new HashMap<Integer, ContactBean>();
                for(ContactBean c : contactList){
                    contactMap.put(c.getCuid(),c);
                }
                contacts.putAll(contactMap);
                Intent intent = new Intent("update_contact");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }
}