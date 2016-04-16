package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by clawpo on 16/4/16.
 */
public class GoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<NewGoodBean> mGoodList;

    GoodItemViewHolder goodHolder;
    FooterViewHolder footerHolder;

    private String footerText;
    private boolean isMore;

    public void setFooterText(String footerText) {
        this.footerText = footerText;
        notifyDataSetChanged();
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public GoodAdapter(Context mContext, ArrayList<NewGoodBean> mGoodList) {
        this.mContext = mContext;
        this.mGoodList = mGoodList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder holder = null;
        switch (viewType){
            case I.TYPE_ITEM:
                holder = new GoodItemViewHolder(inflater.inflate(R.layout.item_new_good,parent,false));
                break;
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(inflater.inflate(R.layout.item_footer,parent,false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(holder instanceof FooterViewHolder){
            footerHolder = (FooterViewHolder) holder;
            footerHolder.tvFooter.setText(footerText);
            footerHolder.tvFooter.setVisibility(View.VISIBLE);
        }
        if(position == mGoodList.size()){
            return;
        }
        if(holder instanceof GoodItemViewHolder){
            goodHolder = (GoodItemViewHolder) holder;
            NewGoodBean good = mGoodList.get(position);
            goodHolder.tvGoodName.setText(good.getGoodsName());
            goodHolder.tvGoodPrice.setText(good.getCurrencyPrice());
            ImageUtils.setNewGoodThumb(good.getGoodsThumb(),goodHolder.nivThumb);
        }

    }

    @Override
    public int getItemCount() {
        return mGoodList==null?0:mGoodList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position==getItemCount()){
            return I.TYPE_FOOTER;
        }else{
            return I.TYPE_ITEM;
        }
    }

    class GoodItemViewHolder extends ViewHolder{
        LinearLayout layoutGood;
        NetworkImageView nivThumb;
        TextView tvGoodName;
        TextView tvGoodPrice;

        public GoodItemViewHolder(View itemView) {
            super(itemView);
            layoutGood = (LinearLayout) itemView.findViewById(R.id.layout_good);
            nivThumb = (NetworkImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            tvGoodPrice = (TextView) itemView.findViewById(R.id.tv_good_price);
        }
    }
}
