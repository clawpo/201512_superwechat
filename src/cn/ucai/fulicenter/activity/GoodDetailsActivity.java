package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumBean;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

public class GoodDetailsActivity extends BaseActivity {
    public static final String TAG = GoodDetailsActivity.class.getName();
    GoodDetailsActivity mContext;
    GoodDetailsBean mGoodDetails;
    int mGoodsId;

    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;
    /** 显示颜色的容器布局*/
    LinearLayout mLayoutColors;
    ImageView mivCollect;
    ImageView mivAddCart;
    ImageView mivShare;
    TextView mtvCartCount;

    TextView tvGoodName;
    TextView tvGoodEngishName;
    TextView tvShopPrice;
    TextView tvCurrencyPrice;
    WebView wvGoodBrief;

    /** 当前的颜色值*/
    int mCurrentColor;
    /**当前商品是否收藏*/
    boolean isCollect;
    private int actionCollect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_details);
        mContext=this;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        setCollectClickListener();
        setAddCartClickListener();
        registerCartChangedReceiver();
        mivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
    }

    private void setAddCartClickListener() {
        mivAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"mGoodDetails="+mGoodDetails);
                Utils.addCart(mContext,mGoodDetails);
            }
        });
    }

    /**
     * 设置收藏/取消收藏按钮的点击事件监听
     */
    private void setCollectClickListener() {
        mivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserBean user = FuLiCenterApplication.getInstance().getUser();
                if(user==null){
                    startActivity(new Intent(GoodDetailsActivity.this,LoginActivity.class));
                }else {
                    String userName = user.getUserName();
                    try {
                        String path = "";
                        if(isCollect){
                            path = new ApiParams()
                                    .with(I.Collect.GOODS_ID, mGoodsId+"")
                                    .with(I.User.USER_NAME, userName)
                                    .getRequestUrl(I.REQUEST_DELETE_COLLECT);
                            actionCollect = I.ACTION_DELETE_COLLECT;
                        }else{
                            path = new ApiParams()
                                    .with(I.Collect.GOODS_ID, mGoodsId+"")
                                    .with(I.User.USER_NAME, userName)
                                    .with(I.Collect.GOODS_NAME, mGoodDetails.getGoodsName())
                                    .with(I.Collect.GOODS_ENGLISH_NAME, mGoodDetails.getGoodsEnglishName())
                                    .with(I.Collect.GOODS_THUMB, mGoodDetails.getGoodsThumb())
                                    .with(I.Collect.GOODS_IMG, mGoodDetails.getGoodsImg())
                                    .with(I.Collect.ADD_TIME, mGoodDetails.getAddTime()+"")
                                    .getRequestUrl(I.REQUEST_ADD_COLLECT);
                            Log.e(TAG,"path="+path);
                            actionCollect = I.ACTION_ADD_COLLECT;
                        }
                        executeRequest(new GsonRequest<MessageBean>(path,MessageBean.class,
                                responseSetCollectListener(actionCollect),errorListener()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private Response.Listener<MessageBean> responseSetCollectListener(final int action) {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if(messageBean.isSuccess()){
                    if(action == I.ACTION_ADD_COLLECT){
                        isCollect = true;
                        mivCollect.setImageResource(R.drawable.bg_collect_out);
                    }
                    if(action == I.ACTION_DELETE_COLLECT){
                        isCollect = false;
                        mivCollect.setImageResource(R.drawable.bg_collect_in);
                    }
                    new DownloadCollectCountTask(mContext).execute();
                }
                Utils.showToast(mContext,messageBean.getMsg(),Toast.LENGTH_SHORT);
            }
        };
    }

    private void initData() {
        mGoodsId=getIntent().getIntExtra(D.GoodDetails.KEY_GOODS_ID, 0);
        try {
            String path = new ApiParams().with(I.CategoryGood.GOODS_ID, mGoodsId+"")
                    .getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
            Log.e(TAG,"path="+path);
            executeRequest(new GsonRequest<GoodDetailsBean>(path,GoodDetailsBean.class,
                    responseDownloadGoodDetailsListener(),errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<GoodDetailsBean> responseDownloadGoodDetailsListener() {
        return new Response.Listener<GoodDetailsBean>() {
            @Override
            public void onResponse(GoodDetailsBean goodDetailsBean) {
                if(goodDetailsBean!=null){
                    mGoodDetails = goodDetailsBean;
                    DisplayUtils.initBackWithTitle(mContext,getResources().getString(R.string.title_good_details));
                    tvCurrencyPrice.setText(mGoodDetails.getCurrencyPrice());
                    tvGoodEngishName.setText(mGoodDetails.getGoodsEnglishName());
                    tvGoodName.setText(mGoodDetails.getGoodsName());
                    wvGoodBrief.loadDataWithBaseURL(null, mGoodDetails.getGoodsBrief().trim(), D.TEXT_HTML, D.UTF_8, null);

                    //初始化颜色面板
                    initColorsBanner();
                }else {
                    Utils.showToast(mContext, "商品详情下载失败", Toast.LENGTH_LONG);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCollectStatus();
        initCartCount();
    }

    private void initCartCount() {
        ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        if(cartList!=null&&cartList.size()>0){
            mtvCartCount.setVisibility(View.VISIBLE);
            int count=Utils.sumCartCount();
            Log.e(TAG,"CartChangedReceiver.count="+count);
            mtvCartCount.setText(""+count);
        }
    }

    private void initCollectStatus(){
        UserBean user = FuLiCenterApplication.getInstance().getUser();
        Log.e(TAG,"initCollectStatus,user="+user);
        if(user!=null){
            String userName = user.getUserName();
            try {
                String path = new ApiParams().with(I.Collect.GOODS_ID, mGoodsId+"")
                        .with(I.User.USER_NAME, userName)
                        .getRequestUrl(I.REQUEST_IS_COLLECT);
                executeRequest(new GsonRequest<MessageBean>(path,MessageBean.class,
                        responseIsCollectListener(),errorListener()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            isCollect = false;
            mivCollect.setImageResource(R.drawable.bg_collect_in);
        }
    }

    private Response.Listener<MessageBean> responseIsCollectListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if(messageBean.isSuccess()){
                    isCollect = true;
                    mivCollect.setImageResource(R.drawable.bg_collect_out);
                }else{
                    isCollect = false;
                    mivCollect.setImageResource(R.drawable.bg_collect_in);
                }
            }
        };
    }

    private void initColorsBanner() {
        //设置第一个颜色的图片轮播
        updateColor(0);
        for(int i=0;i<mGoodDetails.getProperties().length;i++){
            mCurrentColor=i;
            View layout=View.inflate(mContext, R.layout.layout_property_color, null);
            final NetworkImageView ivColor=(NetworkImageView) layout.findViewById(R.id.ivColorItem);
            Log.i(TAG,"initColorsBanner.goodDetails="+mGoodDetails.getProperties()[i].toString());
            String colorImg = mGoodDetails.getProperties()[i].getColorImg();
            if(colorImg.isEmpty()){
                continue;
            }
            ImageUtils.setGoodDetailThumb(colorImg,ivColor);
            mLayoutColors.addView(layout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateColor(mCurrentColor);
                }
            });
        }
    }
    /**
     * 设置指定属性的图片轮播
     * @param i
     */
    private void updateColor(int i) {
        AlbumBean[] albums = mGoodDetails.getProperties()[i].getAlbums();
        String[] albumImgUrl=new String[albums.length];
        for(int j=0;j<albumImgUrl.length;j++){
            albumImgUrl[j]=albums[j].getImgUrl();
        }
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator, albumImgUrl, albumImgUrl.length);
    }

    private void initView() {
        mivCollect=getViewById(R.id.ivCollect);
        mivAddCart=getViewById(R.id.ivAddCart);
        mivShare = getViewById(R.id.ivShare);
        mtvCartCount=getViewById(R.id.tvCartCount);

        mSlideAutoLoopView=getViewById(R.id.salv);
        mFlowIndicator=getViewById(R.id.indicator);
        mLayoutColors=getViewById(R.id.layoutColorSelector);
        tvCurrencyPrice=getViewById(R.id.tvCurrencyPrice);
        tvGoodEngishName=getViewById(R.id.tvGoodEnglishName);
        tvGoodName=getViewById(R.id.tvGoodName);
        tvShopPrice=getViewById(R.id.tvShopPrice);
        wvGoodBrief=getViewById(R.id.wvGoodBrief);
        WebSettings settings = wvGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);
    }
    CartChangedReceiver mCartChangedReceiver;
    /**
     * 接收来自DownloadCartTask发送的购物车数据改变的广播
     * @author yao
     */
    class CartChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            initCartCount();
        }
    }
    private void registerCartChangedReceiver() {
        mCartChangedReceiver=new CartChangedReceiver();
        IntentFilter filter=new IntentFilter("update_cart");
        registerReceiver(mCartChangedReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCartChangedReceiver!=null){
            unregisterReceiver(mCartChangedReceiver);
        }
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(mGoodDetails.getGoodsName());
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mGoodDetails.getGoodsBrief());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        String url = FuLiCenterApplication.SERVER_ROOT
                + "?" + I.KEY_REQUEST + "=" + I.REQUEST_FIND_GOOD_DETAILS
                + "&" + I.CategoryGood.GOODS_ID + "=" + mGoodDetails.getId();
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(mGoodDetails.getGoodsBrief());
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);

        // 启动分享GUI
        oks.show(this);
    }
}
