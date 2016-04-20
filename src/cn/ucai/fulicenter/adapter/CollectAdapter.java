package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CollectActivity;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by clawpo on 16/4/18.
 */
public class CollectAdapter extends RecyclerView.Adapter<ViewHolder> {
    CollectActivity mContext;
    ArrayList<CollectBean> collectList;

    CollectViewHolder collectViewHolder;
    FooterViewHolder footerHolder;
    String footerText;
    boolean isMore;

    public void setFooterText(String footerText) {
        this.footerText = footerText;
        notifyDataSetChanged();
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public boolean isMore() {
        return isMore;
    }

    public CollectAdapter(Context mContext, ArrayList<CollectBean> collectList) {
        this.mContext = (CollectActivity) mContext;
        this.collectList = collectList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder holder = null;
        switch (viewType){
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(inflater.inflate(R.layout.item_footer,parent,false));
                break;
            case I.TYPE_ITEM:
                holder = new CollectViewHolder(inflater.inflate(R.layout.item_collect,parent,false));
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
        if(holder instanceof CollectViewHolder){
            collectViewHolder = (CollectViewHolder) holder;
            final CollectBean collect = collectList.get(position);
            collectViewHolder.tvName.setText(collect.getGoodsName());
            ImageUtils.setNewGoodThumb(collect.getGoodsThumb(), collectViewHolder.iv);

            collectViewHolder.ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserBean user = FuLiCenterApplication.getInstance().getUser();
                    if(user!=null){
                        String userName = user.getUserName();
                        try {
                            String path = new ApiParams()
                                        .with(I.Collect.GOODS_ID, collect.getGoodsId()+"")
                                        .with(I.User.USER_NAME, userName)
                                        .getRequestUrl(I.REQUEST_DELETE_COLLECT);
                            mContext.executeRequest(new GsonRequest<MessageBean>(path,MessageBean.class,
                                    responseDelCollectListener(collect),mContext.errorListener()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            collectViewHolder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, GoodDetailsActivity.class)
                            .putExtra(D.NewGood.KEY_GOODS_ID, collect.getGoodsId()));
                }
            });
        }
    }

    private Response.Listener<MessageBean> responseDelCollectListener(final CollectBean collect) {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if(messageBean.isSuccess()){
                    collectList.remove(collect);
                    notifyDataSetChanged();
                    new DownloadCollectCountTask(mContext).execute();
                }
                Utils.showToast(mContext,messageBean.getMsg(), Toast.LENGTH_SHORT);
            }
        };
    }

    @Override
    public int getItemCount() {
        return collectList ==null?1: collectList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==getItemCount()-1){
            return I.TYPE_FOOTER;
        }else{
            return I.TYPE_ITEM;
        }
    }

    public void initItems(ArrayList<CollectBean> list) {
        if(collectList !=null && !collectList.isEmpty()){
            collectList.clear();
        }
        collectList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<CollectBean> list) {
        collectList.addAll(list);
        notifyDataSetChanged();
    }

    class CollectViewHolder extends ViewHolder{

        LinearLayout layoutItem;
        NetworkImageView iv;
        TextView tvName;
        ImageView ivDel;

        public CollectViewHolder(View itemView) {
            super(itemView);
            layoutItem=(LinearLayout) itemView.findViewById(R.id.layout_collect);
            iv=(NetworkImageView) itemView.findViewById(R.id.niv_collect_thumb);
            tvName=(TextView) itemView.findViewById(R.id.tv_collect_name);
            ivDel=(ImageView) itemView.findViewById(R.id.iv_del_collect);
        }
    }
}
