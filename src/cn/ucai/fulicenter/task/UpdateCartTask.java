package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;

/**
 * Created by clawpo on 16/4/21.
 */
public class UpdateCartTask extends BaseActivity {
    public static final String TAG = UpdateCartTask.class.getName();
    Context mContext;
    CartBean mCart;
    String path;

    public UpdateCartTask(Context context,CartBean cart){
        this.mContext = context;
        this.mCart = cart;
        initPath();
    }

    private void initPath() {
        try {
            ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
            if(cartList!=null&&cartList.contains(mCart)){
                if(mCart.getCount()<=0){
                    path = new ApiParams().with(I.Cart.ID, mCart.getId()+"")
                            .getRequestUrl(I.REQUEST_DELETE_CART);
                }else{
                    path = new ApiParams().with(I.Cart.IS_CHECKED, mCart.isChecked()+"")
                            .with(I.Cart.ID, mCart.getId()+"")
                            .with(I.Cart.COUNT, mCart.getCount()+"")
                            .getRequestUrl(I.REQUEST_UPDATE_CART);
                }
            }else{
                path = new ApiParams().with(I.Cart.COUNT, mCart.getCount()+"")
                        .with(I.Cart.GOODS_ID, mCart.getGoodsId()+"")
                        .with(I.Cart.IS_CHECKED, mCart.isChecked()+"")
                        .with(I.Cart.USER_NAME, mCart.getUserName()+"")
                        .getRequestUrl(I.REQUEST_ADD_CART);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(){
        if(path==null || path.isEmpty())return;
        executeRequest(new GsonRequest<MessageBean>(path,MessageBean.class,
                responseUpdateCartListener(),errorListener()));
    }

    private Response.Listener<MessageBean> responseUpdateCartListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(final MessageBean messageBean) {
                if(messageBean.isSuccess()){
                    ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
                    if(mCart.getCount()<=0){
                        cartList.remove(mCart);
                    }else {
                        if(cartList.contains(mCart)) {
                            cartList.set(cartList.indexOf(mCart), mCart);
                        }else {
                            try {
                                path = new ApiParams().with(I.CategoryGood.GOODS_ID, mCart.getGoodsId()+"").getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
                                executeRequest(new GsonRequest<GoodDetailsBean>(path, GoodDetailsBean.class,
                                        new Response.Listener<GoodDetailsBean>() {
                                            @Override
                                            public void onResponse(GoodDetailsBean goodDetailsBean) {
                                                if(goodDetailsBean!=null){
                                                    mCart.setGoods(goodDetailsBean);
                                                    mCart.setId(Integer.parseInt(messageBean.getMsg()));
                                                }
                                            }
                                        }, errorListener()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            cartList.add(mCart);
                        }
                    }
                }
                Intent intent = new Intent("update_cart");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }
}
