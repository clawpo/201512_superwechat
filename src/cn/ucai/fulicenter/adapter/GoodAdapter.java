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
import java.util.Collections;
import java.util.Comparator;

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
    int sortBy;

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
        sort(sortBy);
        notifyDataSetChanged();
    }

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

    public GoodAdapter(Context mContext, ArrayList<NewGoodBean> mGoodList, int sortBy) {
        this.mContext = mContext;
        this.mGoodList = mGoodList;
        this.sortBy = sortBy;
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

    public void initItems(ArrayList<NewGoodBean> list) {
        if(mGoodList!=null && !mGoodList.isEmpty()){
            mGoodList.clear();
        }
        mGoodList.addAll(list);
        sort(sortBy);
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<NewGoodBean> list) {
        mGoodList.addAll(list);
        sort(sortBy);
        notifyDataSetChanged();
    }

    private void sort(final int sortBy){
        Collections.sort(mGoodList, new Comparator<NewGoodBean>() {
            @Override
            public int compare(NewGoodBean g1, NewGoodBean g2) {
                int result =0;
                switch (sortBy){
                    case I.SORT_BY_ADDTIME_ASC:
                        result = (int) (g1.getAddTime()-g2.getAddTime());
                        break;
                    case I.SORT_BY_ADDTIME_DESC:
                        result = (int) (g2.getAddTime()-g1.getAddTime());
                        break;
                    case I.SORT_BY_PRICE_ASC:
                    {
                        int p1 = convertPrice(g1.getCurrencyPrice());
                        int p2 = convertPrice(g2.getCurrencyPrice());
                        result = p1-p2;
                    }
                    break;
                    case I.SORT_BY_PRICE_DESC:
                    {
                        int p1 = convertPrice(g1.getCurrencyPrice());
                        int p2 = convertPrice(g2.getCurrencyPrice());
                        result = p2-p1;
                    }
                    break;
                }
                return result;
            }
            private int convertPrice(String price){
                price = price.substring(price.indexOf("￥")+1);
                int p1 = Integer.parseInt(price);
                return p1;
            }
        });
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
