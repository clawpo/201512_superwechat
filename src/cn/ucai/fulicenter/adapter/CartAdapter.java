package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.task.UpdateCartTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by clawpo on 16/4/21.
 */
public class CartAdapter extends RecyclerView.Adapter<ViewHolder> {
    public static final String TAG = CartAdapter.class.getName();
    Context mContext;
    ArrayList<CartBean> cartList;
    boolean isUpdate;

    FooterViewHolder footerHolder;
    CartViewHolder cartHolder;

    /**RecyclerView*/
    ViewGroup parent;
    String footerText;

    public CartAdapter(Context context,ArrayList<CartBean> list) {
        super();
        this.mContext = context;
        cartList= list;
        isUpdate=true;
        ArrayList<CartBean> mCartList = FuLiCenterApplication.getInstance().getCartList();
        Log.e(TAG,"CartAdapter,mCartList="+mCartList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder holder = null;
        switch (viewType){
            case I.TYPE_ITEM:
                holder = new CartViewHolder(inflater.inflate(R.layout.item_cart, parent,false));
                break;
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(inflater.inflate(R.layout.item_footer, parent,false));
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FooterViewHolder){
            footerHolder  = (FooterViewHolder) holder;
            footerHolder.tvFooter.setText(footerText);
            footerHolder.tvFooter.setVisibility(View.VISIBLE);
            return;
        }
        if(holder instanceof CartViewHolder){
            cartHolder = (CartViewHolder) holder;
            final CartBean cart = cartList.get(position);
            GoodDetailsBean goods = cart.getGoods();
            if(goods==null){
                return;
            }
            cartHolder.tvGoodsName.setText(goods.getGoodsName());
            cartHolder.tvCartCount.setText("("+cart.getCount()+")");
            cartHolder.tvGoodsPrice.setText(goods.getCurrencyPrice());
            String path = I.DOWNLOAD_GOODS_THUMB_URL+cart.getGoods().getGoodsThumb();
            ImageUtils.setThumb(path,cartHolder.ivGoodsThumb);

            AddDelCartListener listener=new AddDelCartListener(cart);
            cartHolder.ivAddCart.setOnClickListener(listener);
            cartHolder.ivReduceCart.setOnClickListener(listener);
            cartHolder.chkChecked.setChecked(cart.isChecked());
            cartHolder.chkChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    cart.setChecked(isChecked);
                    new UpdateCartTask(mContext, cart).execute();
//                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cartList==null?1:cartList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==getItemCount()-1){
            return I.TYPE_FOOTER;
        }else{
            return I.TYPE_ITEM;
        }
    }

    class CartViewHolder extends RecyclerView.ViewHolder{
        TextView tvGoodsName;
        TextView tvCartCount;
        ImageView ivAddCart;
        ImageView ivReduceCart;
        NetworkImageView ivGoodsThumb;
        TextView tvGoodsPrice;

        CheckBox chkChecked;

        public CartViewHolder(View itemView) {
            super(itemView);
            tvCartCount=(TextView) itemView.findViewById(R.id.tvCartCount);
            tvGoodsName=(TextView) itemView.findViewById(R.id.tvGoodsName);
            ivAddCart=(ImageView) itemView.findViewById(R.id.ivAddCart);
            ivReduceCart=(ImageView) itemView.findViewById(R.id.ivReduceCart);
            ivGoodsThumb=(NetworkImageView) itemView.findViewById(R.id.ivGoodsThumb);
            tvGoodsPrice=(TextView) itemView.findViewById(R.id.tvGoodsPrice);
            chkChecked=(CheckBox) itemView.findViewById(R.id.chkSelect);
        }
    }
    /**
     * 购物车中商品件数增减的事件监听
     * @author yao
     *
     */
    class AddDelCartListener implements View.OnClickListener {
        CartBean cart;

        public AddDelCartListener(CartBean cart) {
            super();
            this.cart = cart;
        }

        @Override
        public void onClick(View v) {
            cart.setChecked(true);
            switch (v.getId()) {
                case R.id.ivAddCart:
                    Utils.addCart(mContext, cart.getGoods());
                    break;
                case R.id.ivReduceCart:
                    Utils.delCart(mContext, cart.getGoods());
                    break;
            }
        }
    }
}
