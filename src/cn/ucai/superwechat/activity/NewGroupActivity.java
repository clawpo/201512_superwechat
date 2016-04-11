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
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.GroupBean;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.listener.OnSetAvatarListener;
import cn.ucai.superwechat.utils.NetUtil;
import cn.ucai.superwechat.utils.Utils;

public class NewGroupActivity extends BaseActivity {
    private EditText groupNameEditText;
    private ProgressDialog progressDialog;
    private EditText introductionEditText;
    private CheckBox checkBox;
    private CheckBox memberCheckbox;
    private LinearLayout openInviteContainer;
    static final int ACTION_CREATE_GROUP = 100;
    NewGroupActivity mContext;
    OnSetAvatarListener mOnSetAvatarListener;
    ImageView mivAvatar;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_new_group);
        initView();
        setListener();
    }

    private void setListener() {
        setOnCheckchangedListener();
        setSaveGroupClickListener();
        setGroupIconClickListener();
    }

    private void setGroupIconClickListener() {
        findViewById(R.id.layout_group_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = getGroupName();
                if(groupName!=null) {
                    mOnSetAvatarListener = new OnSetAvatarListener(mContext, R.id.layout_new_group, groupName, "group_icon");
                }
            }
        });
    }

    private String getGroupName() {
        String username = groupNameEditText.getText().toString();
        if (username.isEmpty()) {
            Utils.showToast(mContext, R.string.Group_name_cannot_be_empty, Toast.LENGTH_SHORT);
            return null;
        }
        return username;
    }

    private void setOnCheckchangedListener() {
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openInviteContainer.setVisibility(View.INVISIBLE);
                } else {
                    openInviteContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initView() {
        groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
        introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
        checkBox = (CheckBox) findViewById(R.id.cb_public);
        memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
        openInviteContainer = (LinearLayout) findViewById(R.id.ll_open_invite);
        mivAvatar = (ImageView) findViewById(R.id.iv_avatar);
    }

    public void setSaveGroupClickListener() {
        findViewById(R.id.btnSaveGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str6 = getResources().getString(R.string.Group_name_cannot_be_empty);
                String name = groupNameEditText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Intent intent = new Intent(mContext, AlertDialog.class);
                    intent.putExtra("msg", str6);
                    startActivity(intent);
                } else {
                    // 进通讯录选人
                    startActivityForResult(new Intent(mContext, GroupPickContactsActivity.class).putExtra("groupName", name), ACTION_CREATE_GROUP);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == ACTION_CREATE_GROUP) {
            createNewGroup(data);
        } else {
            mOnSetAvatarListener.setAvatar(requestCode, data, mivAvatar);
        }
    }

    private void createNewGroup(Intent data) {
        setProgressDialog();
        String groupName = groupNameEditText.getText().toString().trim();

        try {
            String path = new ApiParams()
                    .with(I.Group.NAME, groupName)
                    .getRequestUrl(I.REQUEST_FIND_GROUP);
            executeRequest(new GsonRequest<GroupBean>(path, GroupBean.class,
                    responseCheckGroupNameListener(data.getStringArrayExtra("newmembers")),
                    errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<GroupBean> responseCheckGroupNameListener(final String[] members) {
        return new Response.Listener<GroupBean>() {
            @Override
            public void onResponse(GroupBean group) {
                if (group != null) {
                    progressDialog.dismiss();
                    groupNameEditText.requestFocus();
                    groupNameEditText.setError(getResources().getString(R.string.Group_name_existed));
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 调用sdk创建群组方法
                            final String st2 = getResources().getString(R.string.Failed_to_create_groups);
                            String groupName = groupNameEditText.getText().toString().trim();
                            String desc = introductionEditText.getText().toString();
                            EMGroup emGroup;
                            try {
                                if (checkBox.isChecked()) {
                                    //创建公开群，此种方式创建的群，可以自由加入
                                    //创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
                                    emGroup = EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true, 200);
                                } else {
                                    //创建不公开群
                                    emGroup = EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked(), 200);
                                }
                                String userName = SuperWeChatApplication.getInstance().getUserName();
                                StringBuffer sbMemberBuffer = new StringBuffer();
                                for (String member : members) {
                                    sbMemberBuffer.append(member).append(",");
                                }
                                sbMemberBuffer.append(userName);
                                String groupId = emGroup.getGroupId();
                                boolean isPublic = checkBox.isChecked();
                                boolean isExam = !memberCheckbox.isChecked();
                                GroupBean toCreateGroup = new GroupBean(groupId, groupName, desc, userName, isPublic, isExam, sbMemberBuffer.toString());

                                boolean isSuccess = NetUtil.createGroup(toCreateGroup);
                                if (isSuccess){
                                    try {
                                        isSuccess=NetUtil.uploadAvatar(mContext, "group_icon", groupName);
                                        if(isSuccess) {
                                            toCreateGroup.setAvatar("group_icon/" + groupName + ".jpg");
                                            Intent intent = new Intent("update_group").putExtra("group",toCreateGroup);
                                            setResult(RESULT_OK,intent);
                                        }else{
                                            progressDialog.dismiss();
                                            Utils.showToast(mContext,R.string.upload_avatar_failed,Toast.LENGTH_SHORT);
                                        }
                                        progressDialog.dismiss();
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    progressDialog.dismiss();
                                    Utils.showToast(mContext,R.string.Failed_to_create_groups,Toast.LENGTH_SHORT);
                                }
                            } catch (final EaseMobException e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            }
        };
    }


    private void setProgressDialog() {
        String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(st1);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }


    public void back(View view) {
        finish();
    }

}
