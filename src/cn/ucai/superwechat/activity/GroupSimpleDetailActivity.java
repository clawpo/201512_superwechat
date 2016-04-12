/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.ucai.superwechat.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.GroupBean;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.utils.UserUtils;
import cn.ucai.superwechat.utils.Utils;

public class GroupSimpleDetailActivity extends BaseActivity {
    Context mContext;
	private Button btn_add_group;
	private TextView tv_admin;
	private TextView tv_name;
	private TextView tv_introduction;
	private GroupBean group;
	private String groupid;
	private ProgressBar progressBar;
	private NetworkImageView niv_avatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_simle_details);
        mContext = this;
		tv_name = (TextView) findViewById(R.id.name);
		tv_admin = (TextView) findViewById(R.id.tv_admin);
		btn_add_group = (Button) findViewById(R.id.btn_add_to_group);
		tv_introduction = (TextView) findViewById(R.id.tv_introduction);
		progressBar = (ProgressBar) findViewById(R.id.loading);
        niv_avatar = (NetworkImageView) findViewById(R.id.avatar);

        GroupBean groupInfo = (GroupBean) getIntent().getSerializableExtra("groupinfo");
		String groupname = null;
		if(groupInfo != null){
            group = groupInfo;
		    groupname = groupInfo.getName();
		    groupid = groupInfo.getGroupId();
		}else{
		    group = PublicGroupsSeachActivity.searchedGroup;
		    if(group == null)
		        return;
		    groupname = group.getName();
		    groupid = group.getGroupId();
		}
		
		tv_name.setText(groupname);
		
		
		if(group != null){
		    showGroupDetail();
		    return;
		}
        try {
            String path = new ApiParams()
                    .with(I.Group.NAME, groupname)
                    .getRequestUrl(I.REQUEST_FIND_GROUP);
            executeRequest(new GsonRequest<GroupBean>(path,GroupBean.class,
                    responseFindGroupListener(),errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
    private Response.Listener<GroupBean> responseFindGroupListener() {
        return new Response.Listener<GroupBean>() {
            @Override
            public void onResponse(GroupBean groupBean) {
                if(groupBean!=null){
                    group = groupBean;
                    groupid = group.getGroupId();
                    showGroupDetail();
                }else{
                    final String st1 = getResources().getString(R.string.Failed_to_get_group_chat_information);
                    progressBar.setVisibility(View.INVISIBLE);
                    Utils.showToast(mContext,st1,Toast.LENGTH_SHORT);
                }
            }
        };
    }
	
	//加入群聊
	public void addToGroup(View view){
		String st1 = getResources().getString(R.string.Is_sending_a_request);
		final String st2 = getResources().getString(R.string.Request_to_join);
		final String st3 = getResources().getString(R.string.send_the_request_is);
		final String st4 = getResources().getString(R.string.Join_the_group_chat);
		final String st5 = getResources().getString(R.string.Failed_to_join_the_group_chat);
		final ProgressDialog pd = new ProgressDialog(this);
//		getResources().getString(R.string)
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					//如果是membersOnly的群，需要申请加入，不能直接join
					if(group.isExame()){
					    EMGroupManager.getInstance().applyJoinToGroup(groupid, st2);
					}else{
					    EMGroupManager.getInstance().joinGroup(groupid);
					}
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							if(group.isExame())
								Toast.makeText(GroupSimpleDetailActivity.this, st3, Toast.LENGTH_SHORT).show();
							else
								Toast.makeText(GroupSimpleDetailActivity.this, st4, Toast.LENGTH_SHORT).show();
							btn_add_group.setEnabled(false);
						}
					});
				} catch (final EaseMobException e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(GroupSimpleDetailActivity.this, st5+e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}
	
     private void showGroupDetail() {
         progressBar.setVisibility(View.INVISIBLE);
         //获取详情成功，并且自己不在群中，才让加入群聊按钮可点击
         if(!group.getMembers().contains(SuperWeChatApplication.getInstance().getUserName()))
             btn_add_group.setEnabled(true);
         tv_name.setText(group.getName());
         tv_admin.setText(group.getOwner());
         tv_introduction.setText(group.getIntro());
         UserUtils.setGroupBeanAvatar(group,niv_avatar);
     }
	
	public void back(View view){
		finish();
	}
}
