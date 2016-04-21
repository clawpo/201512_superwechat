package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/4/5.
 */
public class DownloadCartListTask extends BaseActivity {
    public static final String TAG = DownloadCartListTask.class.getName();
    Context mContext;
    String userName;
    int pageId;
    int pageSize;
    String path;

    public DownloadCartListTask(Context context, String userName, int pageId, int pageSize) {
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
                    .getRequestUrl(I.REQUEST_FIND_CARTS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(){
        executeRequest(new GsonRequest<CartBean[]>(path,CartBean[].class,
                responseDownloadCartListener(), errorListener()));
    }

    private Response.Listener<CartBean[]> responseDownloadCartListener() {
        return new Response.Listener<CartBean[]>(){
            @Override
            public void onResponse(CartBean[] carts) {
                if(carts == null){
                    return;
                }
                final ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
                ArrayList<CartBean> cartArraylist = Utils.array2List(carts);
                try {
                    for(int i=0;i<cartArraylist.size();i++){//CartBean c : cartArraylist){
                        CartBean c = cartArraylist.get(i);
                        if(!cartList.contains(c)){
                            cartList.add(c);
                            path = new ApiParams().with(I.CategoryGood.GOODS_ID, c.getGoodsId()+"").getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
                            executeRequest(new GsonRequest<GoodDetailsBean>(path, GoodDetailsBean.class,
                                    new Response.Listener<GoodDetailsBean>() {
                                        @Override
                                        public void onResponse(GoodDetailsBean goodDetailsBean) {
                                            Log.e(TAG,"goodDetailsBean="+goodDetailsBean);
                                            if(goodDetailsBean!=null){
                                                for (int j=0;j<cartList.size();j++){
                                                    if(cartList.get(j).getGoodsId()==goodDetailsBean.getGoodsId()){
                                                        cartList.get(j).setGoods(goodDetailsBean);
                                                        Intent intent = new Intent("update_cart");
                                                        mContext.sendStickyBroadcast(intent);
                                                    }
                                                }
                                            }
                                        }
                                    }, errorListener()));
                            Intent intent = new Intent("update_cart");
                            mContext.sendStickyBroadcast(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
