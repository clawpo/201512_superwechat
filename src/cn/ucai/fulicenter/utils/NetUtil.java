package cn.ucai.fulicenter.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.ContactBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.UserBean;


public final class NetUtil {
    
    public static final String TAG="NetUtil";

	private String Server_root = "";

	/**
	 * 向app服务器注册
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
    public static boolean register(UserBean user) throws Exception {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(I.KEY_REQUEST, I.REQUEST_REGISTER));
		params.add(new BasicNameValuePair(I.User.USER_NAME, user.getUserName()));
		params.add(new BasicNameValuePair(I.User.NICK, user.getNick()));
		params.add(new BasicNameValuePair(I.User.PASSWORD, user.getPassword()));

		try {
			InputStream in = HttpUtils.getInputStream(FuLiCenterApplication.SERVER_ROOT,
					params, HttpUtils.METHOD_GET);
			ObjectMapper om = new ObjectMapper();
			MessageBean msg;
		    Log.i(TAG, "in="+in);
	        msg=om.readValue(in, MessageBean.class);
            Log.i(TAG, "msg="+msg.toString());
	        return msg.isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return false;
	}

	/**
	 * 上传头像
	 * 
	 * @param activity
	 *            ：当前Activity
	 * @param userName
	 *            ：用户账号
	 * @param avatarType
	 *            :上传图片类型(user_avatar或group_icon)，也是图片保存在sd卡的最后 一个文件夹。
	 * @return
	 * @throws IOException
	 */
	public static boolean uploadAvatar(Activity activity, String avatarType,
			String userName) throws Exception {
		HttpClient client = new DefaultHttpClient();
		String url= FuLiCenterApplication.SERVER_ROOT+"?"+I.KEY_REQUEST+"="+I.REQUEST_UPLOAD_AVATAR
				+"&"+I.User.USER_NAME+"="+userName
				+"&"+I.AVATAR_TYPE+"="+avatarType;
		HttpPost post = new HttpPost(url);
		try {
    		File file = new File(ImageUtils.getAvatarPath(activity, avatarType),
    				userName + ".jpg");
    		HttpEntity entity = HttpUtils.createInputStreamEntity(file);
    		post.setEntity(entity);
    		HttpResponse response = client.execute(post);
    		if (response.getStatusLine().getStatusCode() == 200) {
    			InputStream in = response.getEntity().getContent();
    			ObjectMapper om = new ObjectMapper();
    			MessageBean msg=om.readValue(in, MessageBean.class);
    			return msg.isSuccess();
    		}
        }catch(FileNotFoundException e){
            e.printStackTrace();
	    }catch (Exception e) {
            e.printStackTrace();
        }
		return false;
	}

	/**
	 * 从应用服务器下载头像
	 * @param file:头像保存的sd卡路径
	 * @param requestType：头像类型：user_avatar：个人，group_icon：群组头像
	 * @param avatar:服务端头像保存的文件名
	 */
	public static void downloadAvatar(File file, String requestType,String avatar) {
	    if(file==null){
	        return;
	    }
		if (!file.exists()) {
			try {
				ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair(I.KEY_REQUEST,I.REQUEST_DOWNLOAD_AVATAR));
				params.add(new BasicNameValuePair(I.User.AVATAR, avatar));
				InputStream in = HttpUtils.getInputStream(FuLiCenterApplication.SERVER_ROOT,params,HttpUtils.METHOD_GET);
				Bitmap bmpAvatar = BitmapFactory.decodeStream(in);
				OutputStream out = new FileOutputStream(file);
				if(null!=bmpAvatar){
				    bmpAvatar.compress(CompressFormat.JPEG, 100, out);
				}
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				HttpUtils.closeClient();
			}
		}
	}

	/**
	 * 登陆应用服务器
	 * 
	 * @param userName
	 *            ：账号
	 * @param password
	 *            ：密码
	 * @return true:登陆成功
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws IllegalStateException
	 */
	public static UserBean login(String userName, String password)
			throws IllegalStateException, ClientProtocolException, IOException {

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(I.KEY_REQUEST, I.REQUEST_LOGIN));
		params.add(new BasicNameValuePair(I.User.USER_NAME, userName));
		params.add(new BasicNameValuePair(I.User.PASSWORD, password));
		InputStream in = HttpUtils.getInputStream(FuLiCenterApplication.SERVER_ROOT, params,
				HttpUtils.METHOD_GET);
		Log.e("main","server_root="+ FuLiCenterApplication.SERVER_ROOT);
		ObjectMapper om = new ObjectMapper();
		UserBean user = om.readValue(in, UserBean.class);
		return user;
	}


	/**
	 * 向服务器添加联系人，并返回联系人完整信息->ContactBean类型
	 * 
	 * @param userName
	 *            ：当前用户账号
	 * @param name
	 *            ：联系人账号
	 * @return ContactBean
	 */
	public static ContactBean addContact(String userName, String name) {

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(I.KEY_REQUEST, I.REQUEST_ADD_CONTACT));
		params.add(new BasicNameValuePair(I.User.USER_NAME, userName));
		params.add(new BasicNameValuePair(I.Contact.NAME, name));
		try {
			InputStream in = HttpUtils.getInputStream(FuLiCenterApplication.SERVER_ROOT, params,
					HttpUtils.METHOD_GET);
			ObjectMapper om = new ObjectMapper();
			ContactBean contact = om.readValue(in, ContactBean.class);
			Log.e("main","NetUtil.addContact.contact="+contact);
			return contact;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			HttpUtils.closeClient();
		}
		return null;
	}

	/**
	 * 删除联系人
	 * 
	 * @param myuid
	 *            :当前用户的id
	 * @param cuid
	 *            ：联系人的id
	 */
	public static boolean deleteContact(int myuid, int cuid) {
		Log.e(TAG,"NetUtil.deleteContact.myuid="+myuid+",cuid="+cuid);

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(I.KEY_REQUEST,
				I.REQUEST_DELETE_CONTACT));
		params.add(new BasicNameValuePair(I.Contact.MYUID, myuid + ""));
		params.add(new BasicNameValuePair(I.Contact.CUID, cuid + ""));
		try {
			InputStream in = HttpUtils.getInputStream(FuLiCenterApplication.SERVER_ROOT, params,
					HttpUtils.METHOD_GET);
			ObjectMapper om = new ObjectMapper();
			Boolean isSuccess = om.readValue(in, Boolean.class);
			Log.i("main", "删除联系人成功:" + isSuccess);
			return isSuccess;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			HttpUtils.closeClient();
		}
		return false;
	}

	public static UserBean findUserByUserName(String userName) {

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(I.KEY_REQUEST, I.REQUEST_FIND_USER));
		params.add(new BasicNameValuePair(I.User.USER_NAME, userName));
		try {
			InputStream in = HttpUtils.getInputStream(FuLiCenterApplication.SERVER_ROOT, params,
					HttpUtils.METHOD_GET);
			ObjectMapper om = new ObjectMapper();
			UserBean user = om.readValue(in, UserBean.class);
			return user;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			HttpUtils.closeClient();
		}
		return null;
	}

	/**
	 * 上传当前用户的当前位置信息
	 * 
	 * @return
	 */
	public static boolean uploadLocation(UserBean user) {
		MessageBean msg = new MessageBean(false, "上传位置失败");

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(I.KEY_REQUEST,I.REQUEST_UPLOAD_LOCATION));
		params.add(new BasicNameValuePair(I.User.USER_NAME, user.getUserName()));
		params.add(new BasicNameValuePair(I.User.ID, user.getId() + ""));
		try {
			InputStream in = HttpUtils.getInputStream(FuLiCenterApplication.SERVER_ROOT, params,
					HttpUtils.METHOD_GET);
			ObjectMapper om = new ObjectMapper();
			msg = om.readValue(in, MessageBean.class);
            Log.e(TAG,"uploadLocation.msg="+msg);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			HttpUtils.closeClient();
		}
		return msg.isSuccess();
	}

	/**
	 * 下载指定范围的用户数据，目的：获取用户的地理位置信息
	 * 
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	public static ArrayList<UserBean> downloadLocation(String userName,int pageId, int pageSize) {
		ArrayList<UserBean> users = null;

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(I.KEY_REQUEST,I.REQUEST_DOWNLOAD_LOCATION));
		params.add(new BasicNameValuePair(I.User.USER_NAME, userName));
		params.add(new BasicNameValuePair(I.PAGE_ID, pageId + ""));
		params.add(new BasicNameValuePair(I.PAGE_SIZE, pageSize + ""));
		try {
			InputStream in = HttpUtils.getInputStream(FuLiCenterApplication.SERVER_ROOT, params,
					HttpUtils.METHOD_GET);
			ObjectMapper om = new ObjectMapper();
            if(in!=null){
                Log.e(TAG,"NetUtils.downloadLocation.in="+in);
                UserBean[] userArray = om.readValue(in, UserBean[].class);
                Log.e(TAG,"NetUtils.downloadLocation.userArray="+userArray.length);
                List<UserBean> list = Arrays.asList(userArray);
                users = new ArrayList<UserBean>(list);
            }
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			HttpUtils.closeClient();
		}
		return users;
	}


	/**
	 * 下载联系人->HashMap<Integer,ContactBean>
	 */
	public static boolean downloadContacts(FuLiCenterApplication instance, String userName, int pageId, int pageSize){
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(I.KEY_REQUEST,I.REQUEST_DOWNLOAD_CONTACTS));
		params.add(new BasicNameValuePair(I.User.USER_NAME, userName));
		params.add(new BasicNameValuePair(I.PAGE_ID, pageId + ""));
		params.add(new BasicNameValuePair(I.PAGE_SIZE, pageSize + ""));
		try {
			InputStream in=HttpUtils.getInputStream(FuLiCenterApplication.SERVER_ROOT, params,HttpUtils.METHOD_GET);
			ObjectMapper om = new ObjectMapper();
//			Log.e("main","in="+in.toString());
			ContactBean[] contacts = om.readValue(in, ContactBean[].class);
            Log.e(TAG,"downloadContacts,contacts.length="+contacts.length);
			HashMap<Integer, ContactBean> map = new HashMap<Integer, ContactBean>();
			for (ContactBean contact : contacts) {
				map.put(contact.getCuid(), contact);
			}
			HashMap<Integer,ContactBean> contactMap=instance.getContacts();
            Log.e(TAG,"downloadContacts,contactMap.size()="+contactMap.size());
            Log.e(TAG,"downloadContacts,contactMap.size()="+contactMap.size());
			contactMap.putAll(map);
            Log.e(TAG,"downloadContacts,contactMap.size()="+contactMap.size());
			return true;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			HttpUtils.closeClient();
		}
		return false;
	}
	
	/**
	 * 下载联系人集合：ArrayList<UserBean>
	 * @param userName
     * @[param pageId
     * @param pageSize
	 */
	public static boolean downloadContactList(String userName,int pageId,int pageSize) {

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(I.KEY_REQUEST,I.REQUEST_DOWNLOAD_CONTACT_LIST));
		params.add(new BasicNameValuePair(I.User.USER_NAME, userName));
		params.add(new BasicNameValuePair(I.PAGE_ID, pageId + ""));
		params.add(new BasicNameValuePair(I.PAGE_SIZE, pageSize + ""));
		try {
			InputStream in = HttpUtils.getInputStream(FuLiCenterApplication.SERVER_ROOT, params,HttpUtils.METHOD_GET);
//			if(in!=null){
//				byte b[] = new byte[1000];
//				int c = in.read(b);
//				String ss = new String(b,0,c);
//				Log.e("main","ss="+ss);
//
//			}
			ObjectMapper om = new ObjectMapper();
			UserBean[] userArray = om.readValue(in, UserBean[].class);
			if(userArray==null){
			    Log.e("main","download contact list false");
				return false;
			}
			//将数组转换为集合
			ArrayList<UserBean> userList=Utils.array2List(userArray);
            Log.e(TAG,"userList="+userList.size());
			//获取已添加的所有联系人的集合
			ArrayList<UserBean> contactList = FuLiCenterApplication.getInstance().getContactList();
            Log.e(TAG,"getInstance().getContactList()="+ FuLiCenterApplication.getInstance().getContactList().size());
			//将新下载的数据添加到原联系人集合中
			contactList.addAll(userList);
            Log.e(TAG,"getInstance().getContactList()="+ FuLiCenterApplication.getInstance().getContactList().size());
			return true;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			HttpUtils.closeClient();
		}
		return false;
	}
	/**
	 * 获得服务器状态的请求
	 */
	public static MessageBean getServerStatus() {
		MessageBean msg = new MessageBean(false, "连接失败");
		ArrayList<BasicNameValuePair> params=new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(I.KEY_REQUEST, I.REQUEST_SERVERSTATUS));
		try {
			InputStream in = HttpUtils.getInputStream(FuLiCenterApplication.SERVER_ROOT, params, HttpUtils.METHOD_GET);
			ObjectMapper om=new ObjectMapper();
			msg = om.readValue(in, MessageBean.class);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			HttpUtils.closeClient();
		}
		return msg;
	}
	/**
	 * 将注册账号为userName的用户从应用服务器删除,同时将上传的头像从服务器删除
	 * @param userName
	 */
    public static MessageBean unRegister(String userName) {
//		if(isServerConnectioned()){
//			return null;
//		}
		Log.e(TAG,"NetUtil.unRegister.userName="+userName);
        MessageBean msg = new MessageBean(false, "取消注册失败");
        ArrayList<BasicNameValuePair> params=new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(I.KEY_REQUEST, I.REQUEST_UNREGISTER));
        params.add(new BasicNameValuePair(I.User.USER_NAME, userName));
        try {
            InputStream in = HttpUtils.getInputStream(FuLiCenterApplication.SERVER_ROOT, params, HttpUtils.METHOD_GET);
            ObjectMapper om=new ObjectMapper();
            msg = om.readValue(in, MessageBean.class);
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            HttpUtils.closeClient();
        }
        return msg;
    }
	
}
