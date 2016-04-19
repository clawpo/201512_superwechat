package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CategoryChildActivity;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by clawpo on 16/4/19.
 */
public class CategoryAdapter extends BaseExpandableListAdapter {
    Context mContext;
    ArrayList<CategoryGroupBean> categoryList;
    ArrayList<ArrayList<CategoryChildBean>> childList;

    public CategoryAdapter(Context mContext, ArrayList<CategoryGroupBean> categoryList,
                           ArrayList<ArrayList<CategoryChildBean>> childList) {
        this.mContext = mContext;
        this.categoryList = categoryList;
        this.childList = childList;
    }

    @Override
    public int getGroupCount() {
        return categoryList==null?0:categoryList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList==null||childList.get(groupPosition)==null?0:childList.get(groupPosition).size();
    }

    @Override
    public CategoryGroupBean getGroup(int groupPosition) {
        return categoryList.get(groupPosition);
    }

    @Override
    public CategoryChildBean getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View layout, ViewGroup parent) {
        ViewGroupHolder holder=null;
        if(layout==null){
            layout=View.inflate(mContext, R.layout.item_category_group, null);
            holder=new ViewGroupHolder();
            holder.ivIndicator=(ImageView) layout.findViewById(R.id.ivIndicator);
            holder.ivThumb=(NetworkImageView) layout.findViewById(R.id.ivGroupThumb);
            holder.tvGroupName=(TextView) layout.findViewById(R.id.tvGroupName);
            layout.setTag(holder);
        }else{
            holder=(ViewGroupHolder) layout.getTag();
        }
        CategoryGroupBean group = getGroup(groupPosition);
        holder.tvGroupName.setText(group.getName());
        String imgUrl=group.getImageUrl();
        String url= I.DOWNLOAD_DOWNLOAD_CATEGORY_GROUP_IMAGE_URL+imgUrl;
        ImageUtils.setThumb(url,holder.ivThumb);
        if(isExpanded){
            holder.ivIndicator.setImageResource(R.drawable.expand_off);
        }else{
            holder.ivIndicator.setImageResource(R.drawable.expand_on);
        }
        return layout;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View layout, ViewGroup parent) {
        ViewChildHolder holder=null;
        if(layout==null){
            layout=View.inflate(mContext, R.layout.item_cateogry_child, null);
            holder=new ViewChildHolder();
            holder.layoutItem=(RelativeLayout) layout.findViewById(R.id.layout_category_child);
            holder.ivThumb=(NetworkImageView) layout.findViewById(R.id.ivCategoryChildThumb);
            holder.tvChildName=(TextView) layout.findViewById(R.id.tvCategoryChildName);
            layout.setTag(holder);
        }else{
            holder=(ViewChildHolder) layout.getTag();
        }
        final CategoryChildBean child = getChild(groupPosition, childPosition);
        String name=child.getName();
        holder.tvChildName.setText(name);

        String imgUrl=child.getImageUrl();
        String url=I.DOWNLOAD_DOWNLOAD_CATEGORY_CHILD_IMAGE_URL+imgUrl;
        ImageUtils.setThumb(url,holder.ivThumb);
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, CategoryChildActivity.class);
                intent.putExtra(I.CategoryChild.CAT_ID, child.getId());
                intent.putExtra(I.CategoryGroup.NAME, child.getName());
                mContext.startActivity(intent);
            }
        });
        return layout;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    class ViewGroupHolder{
        ImageView ivIndicator;
        NetworkImageView ivThumb;
        TextView tvGroupName;
    }
    class ViewChildHolder{
        RelativeLayout layoutItem;
        NetworkImageView ivThumb;
        TextView tvChildName;
    }

    public void addItems(ArrayList<CategoryGroupBean> groupList,
                         ArrayList<ArrayList<CategoryChildBean>> childList){
        this.categoryList.addAll(groupList);
        this.childList.addAll(childList);
        notifyDataSetChanged();
    }
}
