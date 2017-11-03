package xyz.wendyltanpcy.jandancomment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.wendyltanpcy.jandancomment.R;

/**
 * Created by Wendy on 2017/11/3.
 */

public class DuanziCommentsAdapter extends RecyclerView.Adapter<DuanziCommentsAdapter.ViewHolder> {

    private List<Map<String,String>> mList = new ArrayList<>();
    private Context mContext;


    public DuanziCommentsAdapter(List<Map<String,String>> list){
        mList = list;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String,String> map = mList.get(position);
        holder.tucaoAuthor.setText(map.get("tucaoAuthor"));
        holder.tucaoDate.setText(map.get("tucaoDate"));
        holder.tucaoAaginst.setText(map.get("tucaoAgainst"));
        holder.tucaoSupport.setText(map.get("tucaoSupport"));
        holder.tucaoContent.setText(map.get("tucaoContent"));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View v = LayoutInflater.from(mContext).inflate(R.layout.detail_comments_list_item,parent,false);
        ViewHolder hd = new ViewHolder(v);
        return hd;
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tucaoAuthor,tucaoDate,tucaoContent,tucaoSupport,tucaoAaginst;
        public ViewHolder(View v){
            super(v);
            tucaoAuthor  = v.findViewById(R.id.user_name);
            tucaoDate = v.findViewById(R.id.publish_time);
            tucaoContent = v.findViewById(R.id.content);
            tucaoSupport = v.findViewById(R.id.support);
            tucaoAaginst = v.findViewById(R.id.against);
        }
    }
}
