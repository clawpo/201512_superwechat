package cn.ucai.superwechat.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.easemob.util.HanziToPinyin;
import com.squareup.picasso.Picasso;

import cn.ucai.superwechat.Constant;
import cn.ucai.superwechat.DemoHXSDKHelper;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.applib.controller.HXSDKHelper;
import cn.ucai.superwechat.bean.UserBean;
import cn.ucai.superwechat.data.RequestManager;
import cn.ucai.superwechat.domain.User;

public class UserUtils {
    public static final String TAG = UserUtils.class.getName();
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static User getUserInfo(String username){
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(username);
        if(user == null){
            user = new User(username);
        }
            
        if(user != null){
            //demo没有这些数据，临时填充
        	if(TextUtils.isEmpty(user.getNick()))
        		user.setNick(username);
        }
        return user;
    }

    public static UserBean getUserBeanInfo(String username){
        UserBean user = SuperWeChatApplication.getInstance().getUserList().get(username);
        return user;
    }
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	User user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }

    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserBeanAvatar(String username, NetworkImageView imageView){
        UserBean user = getUserBeanInfo(username);
        setUserAvatar(user,imageView);
    }

    /**
     * 设置用户头像
     */
    public static void setUserBeanAvatar(UserBean user, NetworkImageView imageView){
        setUserAvatar(user,imageView);
    }
    
    /**
     * 设置当前用户头像
     */
	public static void setCurrentUserAvatar(Context context, ImageView imageView) {
		User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		if (user != null && user.getAvatar() != null) {
			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
		} else {
			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
		}
	}

	public static void setCurrentUserBeanAvatar(NetworkImageView imageView){
		UserBean user = SuperWeChatApplication.getInstance().getUser();
        setUserAvatar(user,imageView);
	}

    private static void setUserAvatar(UserBean user, NetworkImageView imageView){
        imageView.setDefaultImageResId(R.drawable.default_avatar);
        if(user != null && user.getAvatar() != null){
            String path = I.DOWNLOAD_AVATAR_URL + user.getAvatar();
            imageView.setImageUrl(path,RequestManager.getImageLoader());
        } else {
            imageView.setErrorImageResId(R.drawable.default_avatar);
        }
    }


    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
    	User user = getUserInfo(username);
    	if(user != null){
    		textView.setText(user.getNick());
    	}else{
    		textView.setText(username);
    	}
    }

    /**
     * 设置用户昵称
     */
    public static void setUserBeanNick(String username,TextView textView){
        UserBean user = getUserBeanInfo(username);
        if(user != null){
            textView.setText(user.getNick());
        }else{
            textView.setText(username);
        }
    }

    /**
     * 设置用户昵称
     */
    public static void setUserBeanNickNF(UserBean user,TextView textView){
        if(user != null && user.getAvatar()!=null){
            textView.setText(user.getNick());
        }else{
            textView.setText(user.getUserName());
        }
    }
    
    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView){
        UserBean user = SuperWeChatApplication.getInstance().getUser();
        if(textView != null){
    		textView.setText(user.getNick());
    	}
    }

    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserBeanNick(TextView textView){
        User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
        if(textView != null){
            textView.setText(user.getNick());
        }
    }
    
    /**
     * 保存或更新某个用户
     * @param newUser
     */
	public static void saveUserInfo(User newUser) {
		if (newUser == null || newUser.getUsername() == null) {
			return;
		}
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
	}


    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param username
     * @param user
     */
    public static void setUserHearder(String username, UserBean user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUserName();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)
                || username.equals(Constant.GROUP_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
                    .toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
    }
    
}
