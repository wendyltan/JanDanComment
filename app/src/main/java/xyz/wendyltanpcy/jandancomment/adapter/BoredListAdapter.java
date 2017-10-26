package xyz.wendyltanpcy.jandancomment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.wendyltanpcy.jandancomment.helper.PictureHandle;
import xyz.wendyltanpcy.jandancomment.R;

/**
 * Created by Wendy on 2017/10/25.
 */

public class BoredListAdapter extends BaseRecyclerAdapter<BoredListAdapter.ViewHolder> {



    /**
     * Created by Wendy on 2017/10/18.
     */

    private List<Map<String,String>> boredList = new ArrayList<>();
    private Context mContext;


    @Override
    public  BoredListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.boring_list_item, parent, false);
        mContext = parent.getContext();
        ViewHolder vh = new ViewHolder(v, true);
        return vh;
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view,true);
    }

    public  BoredListAdapter(List<Map<String, String>> list){
        boredList = list;
    }


    @Override
    public void onBindViewHolder(BoredListAdapter.ViewHolder holder, int position, boolean isItem) {
        Map<String,String> map = boredList.get(position);
        holder.userName.setText(map.get("userName"));
        holder.number.setText(map.get("number"));
        holder.time.setText(map.get("time"));
        final String url  = map.get("boring");
        if (url.substring(url.length()-3,url.length())=="gif")
            Glide.with(mContext)
                    .load(url)
                    .asGif()
                    .placeholder(R.mipmap.icon)
                    .into(holder.boringContent);
        else{
            Glide.with(mContext)
                    .load(url)
                    .placeholder(R.mipmap.icon)
                    .into(holder.boringContent);
        }
        holder.against.setText(map.get("against"));
        holder.support.setText(map.get("support"));
        holder.boringContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PictureHandle.actionStart(mContext,url);
            }
        });

    }



    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }

    @Override
    public int getAdapterItemCount() {
        return boredList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView userName,number,time,against,support;
        ImageView boringContent;

        public ViewHolder(View view,boolean isItem){
            super(view);
            if (isItem) {
                userName = view.findViewById(R.id.user_name);
                number = view.findViewById(R.id.publish_number);
                time = view.findViewById(R.id.publish_time);
                boringContent = view.findViewById(R.id.boring_content);
                support = view.findViewById(R.id.support);
                against = view.findViewById(R.id.against);
            }

        }

    }

}

