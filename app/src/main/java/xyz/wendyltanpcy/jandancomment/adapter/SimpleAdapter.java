package xyz.wendyltanpcy.jandancomment.adapter;

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
 * Created by Wendy on 2017/10/18.
 */

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

    private List<Map<String,String>> commentList = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_list_item,parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    public SimpleAdapter(List<Map<String, String>> list){
        commentList = list;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String,String> map = commentList.get(position);
        holder.userName.setText(map.get("userName"));
        holder.number.setText(map.get("number"));
        holder.time.setText(map.get("time"));
        holder.content.setText(map.get("content"));
        holder.against.setText(map.get("against"));
        holder.support.setText(map.get("support"));

    }


    @Override
    public int getItemCount() {
        return commentList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView userName,number,time,content,against,support;

        public ViewHolder(View view){
            super(view);
            userName = view.findViewById(R.id.user_name);
            number = view.findViewById(R.id.publish_number);
            time = view.findViewById(R.id.publish_time);
            content = view.findViewById(R.id.content);
            support = view.findViewById(R.id.support);
            against = view.findViewById(R.id.against);

        }

    }
}
