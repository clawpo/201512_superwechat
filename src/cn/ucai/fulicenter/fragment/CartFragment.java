package cn.ucai.fulicenter.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;

/**
 * Created by clawpo on 16/4/21.
 */
public class CartFragment extends Fragment {
    public static final String TAG = CartFragment.class.getName();
    Context mContext;
    CartAdapter mAdapter;
    ArrayList<CartBean> mCartList;
    RecyclerView mrvCart;

    TextView mtvSumPrice;
    TextView mtvSavePrice;
    TextView mtvNothing;
    CartChangedReceiver mCartChangedReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout=View.inflate(getActivity(), R.layout.fragment_cart, null);
        mContext = getActivity();
        mCartList = new ArrayList<CartBean>();
        initView(layout);
        registerCartChangedReceiver();
        refresh();
        return layout;
    }

    private void initView(View layout) {
        mtvSumPrice=(TextView) layout.findViewById(R.id.tvSumPrice);
        mtvSavePrice=(TextView) layout.findViewById(R.id.tvSavePrice);
        mtvNothing= (TextView) layout.findViewById(R.id.tv_nothing);

        mrvCart = (RecyclerView) layout.findViewById(R.id.rv_cart);
        mrvCart.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter=new CartAdapter(mContext,mCartList);
        mrvCart.setHasFixedSize(true);
        mrvCart.setAdapter(mAdapter);
        mtvNothing.setVisibility(View.VISIBLE);
    }

    private void refresh() {
        ArrayList<CartBean> list = FuLiCenterApplication.getInstance().getCartList();
        mCartList.clear();
        mCartList.addAll(list);
        Log.e(TAG,"refresh,mCartList="+mCartList.size());
        mAdapter.notifyDataSetChanged();
        sumPrice();
        if(mCartList!=null&&mCartList.size()>0) {
            mtvNothing.setVisibility(View.GONE);
        }else{
            mtvNothing.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    /**
     * 接收来自DownloadCartTask发送的购物车数据改变的广播
     * @author yao
     */
    class CartChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    }

    private void registerCartChangedReceiver() {
        mCartChangedReceiver=new CartChangedReceiver();
        IntentFilter filter=new IntentFilter("update_cart");
        getActivity().registerReceiver(mCartChangedReceiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mCartChangedReceiver!=null) {
            getActivity().unregisterReceiver(mCartChangedReceiver);
        }
    }
    /**
     * 统计购物车中所有商品的总价和打折节省的钱
     */
    protected void sumPrice() {
        ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        int sumRankPrice=0;//人民币折扣价
        int sumCurrentPrice=0;//人民币价
        //遍历购物车
        for(int i=0;i<cartList.size();i++){
            CartBean cart = cartList.get(i);
            GoodDetailsBean goods = cart.getGoods();
            if(cart.isChecked()){
                //当同一种商品有多件时，需要多次累加该商品的价格
                for(int k=0;k<cart.getCount();k++){
                    if(goods!=null) {
                        int rankPrice = convertPrice(goods.getRankPrice());
                        int currentPrice = convertPrice(goods.getCurrencyPrice());
                        sumRankPrice += rankPrice;
                        sumCurrentPrice += currentPrice;
                    }
                }
            }
        }
        int sumSavePrice=sumCurrentPrice-sumRankPrice;
        mtvSumPrice.setText("合计:￥"+sumRankPrice);
        mtvSavePrice.setText("节省:￥"+sumSavePrice);
    }

    /**
     * 将头部带￥的商品价格转换为int类型
     * @param strPrice
     * @return
     */
    private int convertPrice(String strPrice) {
        strPrice=strPrice.substring(strPrice.indexOf("￥")+1);
        int price=Integer.parseInt(strPrice);
        return price;
    }
}
